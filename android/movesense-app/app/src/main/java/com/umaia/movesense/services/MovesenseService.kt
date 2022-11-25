package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.google.gson.Gson
import com.movesense.mds.*
import com.movesense.showcaseapp.model.ECGResponse
import com.polidea.rxandroidble2.RxBleClient
import com.umaia.movesense.*
import com.umaia.movesense.R
import com.umaia.movesense.data.*
import com.umaia.movesense.data.ecg.ECG
import com.umaia.movesense.data.ecg.ECGRepository
import com.umaia.movesense.data.hr.Hr
import com.umaia.movesense.data.hr.HrRepository
import com.umaia.movesense.fragments.Home
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.data.responses.HRResponse
import com.umaia.movesense.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MovesenseService : LifecycleService() {

    companion object {
        val moveSenseEvent = MutableLiveData<MoveSenseEvent>()
        val movesenseHeartRate = MutableLiveData<Hr>()

    }

    private lateinit var readAllData: LiveData<List<Hr>>
    private lateinit var hrRepository: HrRepository
    private lateinit var ecgRepository: ECGRepository

    private var isServiceStopped = true

    lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification

    private var mHRSubscription: MdsSubscription? = null
    private var mECGSubscription: MdsSubscription? = null


    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private var mBleClient: RxBleClient? = null
    private var mMds: Mds? = null
    lateinit var gv: GlobalClass

    init {

    }

    override fun onCreate() {
        super.onCreate()

        initValues()
        gv = this.applicationContext as GlobalClass
        bluetoothList = gv.bluetoothList

        val hrDao = AppDataBase.getDatabase(this).hrDao()
        val ecgDao = AppDataBase.getDatabase(this).ecgDao()

        hrRepository = HrRepository(hrDao)
        ecgRepository = ECGRepository(ecgDao)

        readAllData = hrRepository.readAllData

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    Timber.d("Started Service")
                    if (gv.currentDevice.isConnected) {
                        startForegroundService()
//                            createNotification2()

                    } else {
                        Timber.e("Errooooooooooooooooooooou")
                    }
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                    stopService()
                }
                Constants.ACTION_BLUETOOTH_CONNECTED -> {
                    initMds()
                    connectBLEDevice(gv.currentDevice)

                }
            }


        }


        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun initValues() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
    }

    private fun startForegroundService() {
        moveSenseEvent.postValue(MoveSenseEvent.START)
        isServiceStopped = false

        enableHRSubscription()
        enableECGSubscription()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel()
//        }

//        startForeground(Constants.NOTIFICATION_ID, notification)

    }

    private fun stopService() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
        isServiceStopped = true
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
        unsubscribeAll()
//        stopSelf()

    }

    private fun getBleClient(): RxBleClient? {
        // Init RxAndroidBle (Ble helper library) if not yet initialized
        if (mBleClient == null) {
            mBleClient = RxBleClient.create(this)
        }
        return mBleClient
    }

    private fun initMds() {
        if (mMds == null) {
            mMds = Mds.builder().build(this)
        }
    }

    private fun connectBLEDevice(device: MyScanResult) {
        val bleDevice = getBleClient()!!.getBleDevice(device.macAddress)
        Timber.e("Connecting to BLE device: " + bleDevice.macAddress)

        mMds!!.connect(bleDevice.macAddress, object : MdsConnectionListener {
            override fun onConnect(s: String) {
                Timber.e("onConnect:$s")
            }

            override fun onConnectionComplete(macAddress: String, serial: String) {
                //Cria notificação, sensor conectado.
                gv.connected = true
                createNotification()

                for (sr in bluetoothList) {
                    if (sr.macAddress.equals(macAddress, true)) {
                        sr.markConnected(serial)
                        break
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()
                Toast.makeText(
                    this@MovesenseService,
                    "Conectado ao sensor ${serial}.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@MovesenseService, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                if (!isServiceStopped) {
                    startForegroundService()
                } else {
                    stopService()
                }
            }

            override fun onError(e: MdsException) {
                Timber.e("onError:$e")
                gv.connected = false
                showConnectionError(e)
            }

            override fun onDisconnect(bleAddress: String) {
                Timber.e("onDisconnect: $bleAddress")
                gv.connected = false
                createNotification()
                Toast.makeText(this@MovesenseService, "DESCONECTADOz<x<z.", Toast.LENGTH_SHORT)
                    .show()

                for (sr in bluetoothList) {
                    if (bleAddress == sr.macAddress) {

                        // Unsubscribe all from possible
                        if (sr.connectedSerial != null && Home.s_INSTANCE != null &&
                            sr.connectedSerial.equals(gv.currentDevice.connectedSerial)
                        ) {
                            unsubscribeAll()
                            Home.s_INSTANCE!!.activity?.finish()
//                            stopService(Intent(this@ScanDevice,MyService::class.java))
                        }
                        sr.markDisconnected()
                    }
                }
            }
        })
    }

    private fun enableHRSubscription() {
        // Make sure there is no subscription
        unsubscribeHR()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(gv.currentDevice.serial).append(Constants.URI_MEAS_HR)
                .append("\"}").toString()
        Timber.e(strContract)
        mHRSubscription = Mds.builder().build(this).subscribe(Constants.URI_EVENTLISTENER,
            strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")
                    val hrResponse: HRResponse = Gson().fromJson(
                        data, HRResponse::class.java
                    )
                    //Adicionar a base de dados Room
                    var teste = Hr(
                        id = 0,
                        userID = 1,
                        average = hrResponse.body.average,
                        rrData = hrResponse.body.rrData[0]
                    )
                    addHr(teste)


                    Toast.makeText(this@MovesenseService, "Guardado.", Toast.LENGTH_LONG).show()

                    if (hrResponse != null) {
                        gv.hrAvarage = hrResponse.body.average.toString()
                        movesenseHeartRate.postValue(teste)
                        gv.hrRRdata =
                            hrResponse.body.rrData[hrResponse.body.rrData.size - 1].toString()
                        Timber.e(gv.hrAvarage + "<-  adasdasdasdas")
                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("HRSubscription onError(): $error")
                    unsubscribeHR()
                }
            })
    }

    private fun addECG(ecg: ECG) {
        lifecycleScope.launch(Dispatchers.IO) {
            ecgRepository.addEcg(ecg)
        }
    }

    fun addHr(hr: Hr) {
        lifecycleScope.launch(Dispatchers.IO) {
            hrRepository.addHr(hr)
        }
    }

    private fun unsubscribeHR() {
        if (mHRSubscription != null) {
            mHRSubscription!!.unsubscribe()
            mHRSubscription = null
        }
    }

    private fun enableECGSubscription() {
        // Make sure there is no subscription
        unsubscribeECG()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = java.lang.StringBuilder()
//        val sampleRate = ("" + mSpinnerSampleRates.getSelectedItem()).toInt()
        val sampleRate = 125
        val GRAPH_WINDOW_WIDTH = sampleRate * 3
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_ECG_ROOT).append(sampleRate)
            .append("\"}").toString()
        Timber.e(strContract)
        // Clear graph

//        mSeriesECG.resetData(arrayOfNulls<DataPoint>(0))
//        val graph: GraphView = findViewById(R.id.graphECG) as GraphView
//        graph.getViewport().setMaxX(GRAPH_WINDOW_WIDTH)
//        mDataPointsAppended = 0

        mECGSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER,
                strContract, object : MdsNotificationListener {
                    override fun onNotification(data: String) {
                        Timber.e("onNotification(): $data")

                        val ecgResponse: ECGResponse = Gson().fromJson(
                            data, ECGResponse::class.java
                        )
                        if (ecgResponse != null) {

                            var teste = ECG(
                                id = 0,
                                userID = 1,
                                data = Gson().toJson(ecgResponse.body.data),
                                timestamp = ecgResponse.body.timestamp
                            )
                            addECG(teste)


                            for (sample in ecgResponse.body.data) {
                                try {

//                                    mSeriesECG.appendData(
//                                        DataPoint(mDataPointsAppended, sample), true,
//                                        GRAPH_WINDOW_WIDTH
//                                    )
                                } catch (e: IllegalArgumentException) {
                                    Timber.e("Erro $e")
//                                    Log.e(
//                                        com.movesense.samples.ecgsample.ECGActivity.LOG_TAG,
//                                        "GraphView error ",
//                                        e
//                                    )
                                }
//                                mDataPointsAppended++
                            }
                        }
                    }

                    override fun onError(error: MdsException) {
                        Timber.e("onError(): $error")

                        unsubscribeECG()
                    }
                })
    }

    private fun unsubscribeECG() {
        if (mECGSubscription != null) {
            mECGSubscription!!.unsubscribe()
            mECGSubscription = null
        }
    }


    fun unsubscribeAll() {
        Timber.e("unsubscribeAll()")
        // TODO: asdasd
        unsubscribeECG()
        unsubscribeHR()
    }

    private fun showConnectionError(e: MdsException) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Connection Error:")
            .setMessage(e.message)
        builder.create().show()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            moveSenseEvent.observe(this, Observer {
                when (it) {
                    is MoveSenseEvent.START -> {

                        var intent1 = Intent(this, MainActivity::class.java)
                        var title = "Sensor conectado"
                        var description = "Recolhendo dados..."
                        var icon = R.mipmap.ic_umaia_logo
                        if (!gv.connected) {
                            intent1 = Intent(this, ScanActivity::class.java)
                            title = "Sensor desconectado"
                            description = "Por favor verifique a conexão..."
                            icon = R.mipmap.ic_umaia_red_logo
                        }

                        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
                        } else {
                            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
                        }
                        notification =
                            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setSmallIcon(icon)
                                .setPriority(128)
                                .setContentIntent(pendingIntent).build()

                        startForeground(Constants.NOTIFICATION_ID, notification)

                    }

                    is MoveSenseEvent.STOP -> {

                    }
                }
            })
        }
    }


}