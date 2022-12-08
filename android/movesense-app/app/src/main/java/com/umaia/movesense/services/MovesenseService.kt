package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.gson.Gson
import com.movesense.mds.*
import com.movesense.showcaseapp.model.ECGResponse
import com.polidea.rxandroidble2.RxBleClient
import com.umaia.movesense.*
import com.umaia.movesense.R
import com.umaia.movesense.data.*
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.acc.ACCRepository
import com.umaia.movesense.data.ecg.ECG
import com.umaia.movesense.data.ecg.ECGRepository
import com.umaia.movesense.data.gyro.GYRO
import com.umaia.movesense.data.gyro.GYRORepository
import com.umaia.movesense.data.hr.Hr
import com.umaia.movesense.data.hr.HrRepository
import com.umaia.movesense.data.magn.MAGN
import com.umaia.movesense.data.magn.MAGNRepository
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.data.responses.*
import com.umaia.movesense.fragments.Home
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MovesenseService : LifecycleService() {

    companion object {
        val moveSenseEvent = MutableLiveData<MoveSenseEvent>()
        val movesenseHeartRate = MutableLiveData<Hr>()
        val movesenseWifi = MutableLiveData<MovesenseWifi>()

    }

    private lateinit var readAllData: LiveData<List<Hr>>
    private lateinit var accRepository: ACCRepository
    private lateinit var hrRepository: HrRepository
    private lateinit var ecgRepository: ECGRepository
    private lateinit var gyroRepository: GYRORepository
    private lateinit var magnRepository: MAGNRepository

    private var isServiceStopped = true

    lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification

    private var mHRSubscription: MdsSubscription? = null
    private var mECGSubscription: MdsSubscription? = null
    private var mACCSubscription: MdsSubscription? = null
    private var mGYROSubscription: MdsSubscription? = null
    private var mMAGNSubscription: MdsSubscription? = null

    private lateinit var networkChecker: NetworkChecker


    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private var mBleClient: RxBleClient? = null
    private var mMds: Mds? = null
    lateinit var gv: GlobalClass

    init {

    }

    override fun onCreate() {
        super.onCreate()
        val datastore = UserPreferences(this)
        networkChecker = NetworkChecker(this)


        initValues()
        createTimer()
        gv = this.applicationContext as GlobalClass
        bluetoothList = gv.bluetoothList

        val hrDao = AppDataBase.getDatabase(this).hrDao()
        val ecgDao = AppDataBase.getDatabase(this).ecgDao()
        val accDao = AppDataBase.getDatabase(this).accDao()
        val gyroDao = AppDataBase.getDatabase(this).gyroDao()
        val magnDao = AppDataBase.getDatabase(this).magnDao()

        hrRepository = HrRepository(hrDao)
        ecgRepository = ECGRepository(ecgDao)
        accRepository = ACCRepository(accDao)
        gyroRepository = GYRORepository(gyroDao)
        magnRepository = MAGNRepository(magnDao)

        readAllData = hrRepository.readAllData


    }


    //Esta função serve para verificar, de x em x tempo, se o wifi está conectado.
    //TODO Se o wifi tiver conectado efetuar um push de todos os dados para o servidor.
    private fun createTimer() {
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                if (networkChecker.hasInternet()) {
                    movesenseWifi.postValue(MovesenseWifi.AVAILABLE)

                } else {
                    movesenseWifi.postValue(MovesenseWifi.UNAVAILABLE)

                }
                mainHandler.postDelayed(this, 5000)
            }
        })
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
                Constants.ACTION_REFRESH_SERVICE -> {
                    stopService()
                    startForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun initValues() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
    }

    private fun startForegroundService() {
        moveSenseEvent.postValue(MoveSenseEvent.START)
        isServiceStopped = false
        gv.isServiceRunning = true


        //TODO ADICIONAR O IMU9, IMU6m, IMU GYRO, e TEMP, adicionar tambem frequencias

//        verificarSensoresAtivados()


        if (gv.isAccActivated && gv.isGyroActivated && gv.isMagnActivated) {
            // /Meas/IMU9: Combined Acc, Gyro & Magn
            Toast.makeText(
                this@MovesenseService,
                "ACC GYRO MAGN",
                Toast.LENGTH_SHORT
            ).show()
        } else if (gv.isAccActivated && gv.isMagnActivated) {
            Toast.makeText(
                this@MovesenseService,
                "ACC MAGN",
                Toast.LENGTH_SHORT
            ).show()
            // /Meas/IMU6m: Combined Acc & Magn
        } else if (gv.isAccActivated && gv.isGyroActivated) {
            Toast.makeText(
                this@MovesenseService,
                "ACC GYRO",
                Toast.LENGTH_SHORT
            ).show()
            //  /Meas/IMU6: Combined Acc & Gyro
        } else if (gv.isAccActivated) {
            enableAccSubscription()
        }
        if (gv.isGyroActivated && !gv.isAccActivated) {
            Toast.makeText(
                this@MovesenseService,
                "GYRO",
                Toast.LENGTH_SHORT
            ).show()

            enableGyroSubscription()

        }
        if (gv.isMagnActivated && !gv.isAccActivated) {
            Toast.makeText(
                this@MovesenseService,
                "MAGN",
                Toast.LENGTH_SHORT
            ).show()

            enableMagnSubscription()
        }

        if (gv.isECGActivated) {
            enableECGSubscription()
            Toast.makeText(
                this@MovesenseService,
                "ECG",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (gv.isHRActivated) {
            enableHRSubscription()
            Toast.makeText(
                this@MovesenseService,
                "HR",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (gv.isTempActivated) {
            Toast.makeText(
                this@MovesenseService,
                "TEMP",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    private fun enableMagnSubscription() {
        // Clean up existing subscription (if there is one)
        if (mMAGNSubscription != null) {
            unsubscribeMagn()
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        val sb = java.lang.StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
                .append(Constants.URI_MEAS_MAGN_13)
                .append("\"}").toString()
        Timber.e(strContract)
//        val sensorUI: View = findViewById(R.id.sensorUI)

        mMAGNSubscription = Mds.builder().build(this).subscribe(Constants.URI_EVENTLISTENER,
            strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")

                    // If UI not enabled, do it now
//                    if (sensorUI.visibility == View.GONE) sensorUI.visibility = View.VISIBLE
                    val magnResponse: MagnResponse =
                        Gson().fromJson(data, MagnResponse::class.java)
                    if (magnResponse != null) {
                        val accStr = java.lang.String.format(
                            "%.02f, %.02f, %.02f",
                            magnResponse.body.array.get(0).x,
                            magnResponse.body.array.get(0).y,
                            magnResponse.body.array.get(0).z
                        )
                        Timber.e(magnResponse.body.timestamp.toString() + " " + accStr.toString())
                        addMagn(
                            MAGN(
                                id = 0,
                                userID = gv.userID,
                                x = magnResponse.body.array[0].x.toString(),
                                y = magnResponse.body.array[0].y.toString(),
                                z = magnResponse.body.array[0].z.toString(),
                                timestamp = magnResponse.body.timestamp
                            )
                        )
                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("ACC onError(): $error")
                    unsubscribeHR()
                }
            })
    }


    private fun enableGyroSubscription() {
        // Clean up existing subscription (if there is one)
        if (mGYROSubscription != null) {
            unsubscribeGYRO()
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        val sb = java.lang.StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
                .append(Constants.URI_MEAS_GYRO_13)
                .append("\"}").toString()
        Timber.e(strContract)
//        val sensorUI: View = findViewById(R.id.sensorUI)

        mGYROSubscription = Mds.builder().build(this).subscribe(Constants.URI_EVENTLISTENER,
            strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")

                    // If UI not enabled, do it now
//                    if (sensorUI.visibility == View.GONE) sensorUI.visibility = View.VISIBLE
                    val gyroResponse: GyroResponse =
                        Gson().fromJson(data, GyroResponse::class.java)
                    if (gyroResponse != null) {
                        val accStr = java.lang.String.format(
                            "%.02f, %.02f, %.02f",
                            gyroResponse.body.array.get(0).x,
                            gyroResponse.body.array.get(0).y,
                            gyroResponse.body.array.get(0).z
                        )
                        Timber.e(gyroResponse.body.timestamp.toString() + " " + accStr.toString())
                        addGYRO(
                            GYRO(
                                id = 0,
                                userID = gv.userID,
                                x = gyroResponse.body.array[0].x.toString(),
                                y = gyroResponse.body.array[0].y.toString(),
                                z = gyroResponse.body.array[0].z.toString(),
                                timestamp = gyroResponse.body.timestamp
                            )
                        )
                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("ACC onError(): $error")
                    unsubscribeHR()
                }
            })
    }

    private fun enableAccSubscription() {
        Toast.makeText(
            this@MovesenseService,
            "ACC",
            Toast.LENGTH_SHORT
        ).show()

        // Clean up existing subscription (if there is one)
        if (mACCSubscription != null) {
            unsubscribeAcc()
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        val sb = java.lang.StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
                .append(Constants.URI_MEAS_ACC_13)
                .append("\"}").toString()
        Timber.e(strContract)
//        val sensorUI: View = findViewById(R.id.sensorUI)


        mACCSubscription = Mds.builder().build(this).subscribe(Constants.URI_EVENTLISTENER,
            strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")

                    // If UI not enabled, do it now
//                    if (sensorUI.visibility == View.GONE) sensorUI.visibility = View.VISIBLE
                    val accResponse: AccDataResponse =
                        Gson().fromJson(data, AccDataResponse::class.java)
                    if (accResponse != null) {
                        val accStr = java.lang.String.format(
                            "%.02f, %.02f, %.02f",
                            accResponse.body.array.get(0).x,
                            accResponse.body.array.get(0).y,
                            accResponse.body.array.get(0).z
                        )
                        Timber.e(accResponse.body.timestamp.toString() + " " + accStr.toString())
                        addACC(
                            ACC(
                                id = 0,
                                userID = gv.userID,
                                x = accResponse.body.array[0].x.toString(),
                                y = accResponse.body.array[0].y.toString(),
                                z = accResponse.body.array[0].z.toString(),
                                timestamp = accResponse.body.timestamp
                            )
                        )
                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("ACC onError(): $error")
                    unsubscribeHR()
                }
            })


    }


    private fun stopService() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
        isServiceStopped = true
        gv.isServiceRunning = false

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
        unsubscribeAll()
//        stopSelf()

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
                        userID = gv.userID,
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
                                userID = gv.userID,
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


    fun unsubscribeAll() {
        Timber.e("unsubscribeAll()")
        // TODO: asdasd
        unsubscribeAcc()
        unsubscribeGYRO()
        unsubscribeECG()
        unsubscribeHR()
        unsubscribeMagn()
    }

    private fun unsubscribeAcc() {
        if (mACCSubscription != null) {
            mACCSubscription!!.unsubscribe()
            mACCSubscription = null
        }
    }

    private fun unsubscribeGYRO() {
        if (mGYROSubscription != null) {
            mGYROSubscription!!.unsubscribe()
            mGYROSubscription = null
        }
    }

    private fun unsubscribeMagn() {
        if (mMAGNSubscription != null) {
            mMAGNSubscription!!.unsubscribe()
            mMAGNSubscription = null
        }
    }

    private fun unsubscribeHR() {
        if (mHRSubscription != null) {
            mHRSubscription!!.unsubscribe()
            mHRSubscription = null
        }
    }

    private fun unsubscribeECG() {
        if (mECGSubscription != null) {
            mECGSubscription!!.unsubscribe()
            mECGSubscription = null
        }
    }

    private fun addMagn(magn: MAGN) {
        lifecycleScope.launch(Dispatchers.IO) {
            magnRepository.add(magn)
        }
    }

    private fun addGYRO(gyro: GYRO) {
        lifecycleScope.launch(Dispatchers.IO) {
            gyroRepository.add(gyro)
        }
    }

    private fun addACC(acc: ACC) {
        lifecycleScope.launch(Dispatchers.IO) {
            accRepository.add(acc)
        }
    }

    private fun addECG(ecg: ECG) {
        lifecycleScope.launch(Dispatchers.IO) {
            ecgRepository.add(ecg)
        }
    }

    fun addHr(hr: Hr) {
        lifecycleScope.launch(Dispatchers.IO) {
            hrRepository.add(hr)
        }
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