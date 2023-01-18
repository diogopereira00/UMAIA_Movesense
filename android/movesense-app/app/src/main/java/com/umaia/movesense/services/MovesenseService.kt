package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
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
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.responses.*
import com.umaia.movesense.fragments.Home
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.temp.TEMP
import com.umaia.movesense.data.temp.TEMPRepository
import com.umaia.movesense.model.MovesenseInternet
import com.umaia.movesense.model.MovesenseTimerEvent
import com.umaia.movesense.ui.home.observeOnce
import kotlinx.coroutines.Runnable

class MovesenseService : LifecycleService() {

    companion object {
        val moveSenseEvent = MutableLiveData<MoveSenseEvent>()
        val movesenseHeartRate = MutableLiveData<Hr>()
        val movesenseWifi = MutableLiveData<MovesenseWifi>()
        val movesenseInternet = MutableLiveData<MovesenseInternet>()
        val movesenseTimerEvent = MutableLiveData<MovesenseTimerEvent>()
        val movesenseTimer = MutableLiveData<String>()

    }

    private lateinit var readAllData: LiveData<List<Hr>>
    private lateinit var accRepository: ACCRepository
    private lateinit var hrRepository: HrRepository
    private lateinit var ecgRepository: ECGRepository
    private lateinit var gyroRepository: GYRORepository
    private lateinit var magnRepository: MAGNRepository
    private lateinit var tempRepository: TEMPRepository
    private lateinit var apiRepository: ApiRepository

    private var isServiceStopped = true

    lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification

    private var mHRSubscription: MdsSubscription? = null
    private var mECGSubscription: MdsSubscription? = null
    private var mACCSubscription: MdsSubscription? = null
    private var mGYROSubscription: MdsSubscription? = null
    private var mMAGNSubscription: MdsSubscription? = null
    private var mTempSubscription: MdsSubscription? = null
    private var mImuSubscription: MdsSubscription? = null


    private lateinit var networkChecker: NetworkChecker


    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private var mBleClient: RxBleClient? = null
    private var mMds: Mds? = null
    lateinit var gv: GlobalClass
    private val remoteDataSource = RemoteDataSource()

    private var listAcc: MutableList<ACC> = mutableListOf()
    private var listGyro: MutableList<GYRO> = mutableListOf()
    private var listMagn: MutableList<MAGN> = mutableListOf()
    private var listHr: MutableList<Hr> = mutableListOf()
    private var listECG: MutableList<ECG> = mutableListOf()
    private var listTemp: MutableList<TEMP> = mutableListOf()

    private lateinit var jsonStringAcc: String
    private lateinit var jsonStringGyro: String
    private lateinit var jsonStringMagn: String
    private lateinit var jsonStringHr: String
    private lateinit var jsonStringECG: String
    private lateinit var jsonStringTemp: String

    private lateinit var accTable: LiveData<List<ACC>>

    //Timer
    private var timerThread: Thread? = null
    private var timerRunning = false
    private var startTime = 0L
    private var timeInMilliseconds = 0L
    private var timeSwapBuff = 0L
    private var updatedTime = 0L


    init {

    }


    override fun onCreate() {
        super.onCreate()
        val datastore = UserPreferences(this)
        networkChecker = NetworkChecker(this)


        initValues()
        gv = this.applicationContext as GlobalClass
        bluetoothList = gv.bluetoothList

        val hrDao = AppDataBase.getDatabase(this).hrDao()
        val ecgDao = AppDataBase.getDatabase(this).ecgDao()
        val accDao = AppDataBase.getDatabase(this).accDao()
        val gyroDao = AppDataBase.getDatabase(this).gyroDao()
        val magnDao = AppDataBase.getDatabase(this).magnDao()
        val tempDao = AppDataBase.getDatabase(this).tempDao()

        hrRepository = HrRepository(hrDao)
        ecgRepository = ECGRepository(ecgDao)
        accRepository = ACCRepository(accDao)
        gyroRepository = GYRORepository(gyroDao)
        magnRepository = MAGNRepository(magnDao)
        tempRepository = TEMPRepository(tempDao)
        apiRepository = ApiRepository(api = remoteDataSource.buildApi(ServerApi::class.java), null)
        readAllData = hrRepository.readAllData


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
                        //Todo bug
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
        fiveMinutesTimer()
        isServiceStopped = false
        gv.isServiceRunning = true
        verificarSensoresAtivados()
        startTimer()
    }

    private fun startTimer() {
        if (!timerRunning) {
            startTime = SystemClock.uptimeMillis()
            timerThread = Thread(
                object : Runnable {
                    override fun run() {
                        while (timerRunning) {
                            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
                            updatedTime = timeSwapBuff + timeInMilliseconds
                            val secs = (updatedTime / 1000).toInt()
                            val mins = secs / 60
                            val hrs = mins / 60
                            val secsRemaining = secs % 60
                            val minsRemaining = mins % 60
                            val formattedTime = "${String.format("%02d", hrs)}:${
                                String.format(
                                    "%02d",
                                    minsRemaining
                                )
                            }:${String.format("%02d", secsRemaining)}"
                            // update LiveData object
                            movesenseTimer.postValue(formattedTime)
                            try {
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                // thread was interrupted, exit loop
                                break
                            }
                        }
                    }

                })
            timerThread?.start()
            timerRunning = true
        }
    }

    private fun stopTimer() {
        if (timerRunning) {
            timerRunning = false
            timerThread?.interrupt()
            timerThread = null
        }
    }

    private fun verificarSensoresAtivados() {
        if (gv.isLiveDataActivated) {
            sendDataToServer()
        }
        if (gv.isAccActivated && gv.isGyroActivated && gv.isMagnActivated) {
            enableImu9()
        } else if (gv.isAccActivated && gv.isMagnActivated) {
            enableImu6m()
        } else if (gv.isAccActivated && gv.isGyroActivated) {
            enableImu6()
        } else if (gv.isAccActivated) {
            enableAccSubscription()
        }
        if (gv.isGyroActivated && !gv.isAccActivated) {
            enableGyroSubscription()
        }
        if (gv.isMagnActivated && !gv.isAccActivated) {
            enableMagnSubscription()
        }

        if (gv.isECGActivated) {
            enableECGSubscription()
        }
        if (gv.isHRActivated) {
            enableHRSubscription()
        }
        if (gv.isTempActivated) {
            enableTempSubscription()
        }


    }


    //Esta função serve para verificar, de 5 em 5 minutos, se o wifi está conectado.
    //Se o wifi tiver conectado efetuar um push de todos os dados para o servidor.
    private fun fiveMinutesTimer() {
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                //Se o LiveData tiver desativado, ativar o modo de se tiver internet enviar dados de 5 em 5 minutos
                if (!gv.isLiveDataActivated) {
                    Timber.e("LiveData Desativo")

                    if (networkChecker.hasInternetWifi()) {
                        movesenseWifi.postValue(MovesenseWifi.AVAILABLE)
                        sendDataToServer()


                    } else {
                        movesenseWifi.postValue(MovesenseWifi.UNAVAILABLE)
                        Timber.e("Nao há wifi")

                    }
                } else {
                    //LiveData Ativo, não fazer nada aqui
                    Timber.e("LiveData Ativo")
                }



                mainHandler.postDelayed(this, 300000)
            }
        })
    }

    //Envia as informações para o servidor.
    fun sendDataToServer() {
        accTable = accRepository.getAllACC
        accTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listAcc = it.toMutableList()
                jsonStringAcc = Gson().toJson(listAcc)
                addACCData(jsonString = jsonStringAcc, authToken = gv.authToken)

            }
        }
        var ecgTable = ecgRepository.getAllECG
        ecgTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listECG = it.toMutableList()
                jsonStringECG = Gson().toJson(listECG)
                addECGData(jsonString = jsonStringECG, authToken = gv.authToken)

            }
        }
        var gyroTable = gyroRepository.getAllGYRO
        gyroTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listGyro = it.toMutableList()
                jsonStringGyro = Gson().toJson(listGyro)
                addGyroData(jsonString = jsonStringGyro, authToken = gv.authToken)
            }
        }
        var magnTable = magnRepository.getAllMagn
        magnTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listMagn = it.toMutableList()
                jsonStringMagn = Gson().toJson(listMagn)
                addMagnData(jsonString = jsonStringMagn, authToken = gv.authToken)
            }
        }
        var hrTable = hrRepository.getAllHr
        hrTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listHr = it.toMutableList()
                jsonStringHr = Gson().toJson(listHr)
                addHrData(jsonString = jsonStringHr, authToken = gv.authToken)
            }
        }

        var tempTable = tempRepository.getAllTemp
        tempTable.observeOnce(this@MovesenseService) {
            if (it.size >= 2) {
                listTemp = it.toMutableList()
                jsonStringTemp = Gson().toJson(listTemp)
                addTempData(jsonString = jsonStringTemp, authToken = gv.authToken)
            }
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
                    this@MovesenseService, "Conectado ao sensor ${serial}.", Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    this@MovesenseService, MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                        if (sr.connectedSerial != null && Home.s_INSTANCE != null && sr.connectedSerial.equals(
                                gv.currentDevice.connectedSerial
                            )
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

    private fun enableAccSubscription() {
        Toast.makeText(
            this@MovesenseService, "ACC", Toast.LENGTH_SHORT
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
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_ACC_13).append("\"}").toString()
        Timber.e(strContract)
//        val sensorUI: View = findViewById(R.id.sensorUI)


        mACCSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
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
                    unsubscribeAcc()
                }
            })


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
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_MAGN_13).append("\"}").toString()
        Timber.e(strContract)
//        val sensorUI: View = findViewById(R.id.sensorUI)

        mMAGNSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")

                    // If UI not enabled, do it now
//                    if (sensorUI.visibility == View.GONE) sensorUI.visibility = View.VISIBLE
                    val magnResponse: MagnResponse = Gson().fromJson(data, MagnResponse::class.java)
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
                    unsubscribeMagn()
                }
            })
    }


    private fun enableGyroSubscription() {
        // Clean up existing subscription (if there is one)
        if (mGYROSubscription != null) {
            unsubscribeGYRO()
        }

        val sb = java.lang.StringBuilder()
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_GYRO_13).append("\"}").toString()
        Timber.e(strContract)

        mGYROSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")

                    val gyroResponse: GyroResponse = Gson().fromJson(data, GyroResponse::class.java)
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
                    unsubscribeGYRO()
                }
            })
    }


    private fun stopService() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
        isServiceStopped = true
        gv.isServiceRunning = false
        stopTimer()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
        unsubscribeAll()
//        stopSelf()

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
        mHRSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
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


//                    Toast.makeText(this@MovesenseService, "Guardado.", Toast.LENGTH_LONG).show()

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
        val strContract: String =
            sb.append("{\"Uri\": \"").append(gv.currentDevice.serial).append(Constants.URI_ECG_ROOT)
                .append(sampleRate).append("\"}").toString()
        Timber.e(strContract)
        // Clear graph

//        mSeriesECG.resetData(arrayOfNulls<DataPoint>(0))
//        val graph: GraphView = findViewById(R.id.graphECG) as GraphView
//        graph.getViewport().setMaxX(GRAPH_WINDOW_WIDTH)
//        mDataPointsAppended = 0

        mECGSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
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


                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("onError(): $error")

                    unsubscribeECG()
                }
            })
    }

    private fun enableTempSubscription() {
        // Make sure there is no subscription
        unsubscribeTemp()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_TEMP).append("\"}").toString()
        Timber.e(strContract)
        mTempSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")
                    val tempResponse: TempResponse = Gson().fromJson(
                        data, TempResponse::class.java
                    )
                    //Adicionar a base de dados Room
                    var teste = TEMP(
                        id = 0, userID = gv.userID, measurement = tempResponse.body.measurement
                    )
                    addTemp(teste)
//
//
//                    Toast.makeText(this@MovesenseService, "Guardado.", Toast.LENGTH_LONG).show()
//
//                    if (hrResponse != null) {
//                        gv.hrAvarage = hrResponse.body.average.toString()
//                        movesenseHeartRate.postValue(teste)
//                        gv.hrRRdata =
//                            hrResponse.body.rrData[hrResponse.body.rrData.size - 1].toString()
//                        Timber.e(gv.hrAvarage + "<-  adasdasdasdas")
//                    }
                }

                override fun onError(error: MdsException) {
                    Timber.e("HRSubscription onError(): $error")
                    unsubscribeTemp()
                }
            })
    }

    private fun enableImu9() {
        // Make sure there is no subscription
        unsubscribeImu()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_IMU_9).append("\"}").toString()
        Timber.e(strContract)
        mImuSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")
                    val imuResponse: ImuResponse = Gson().fromJson(
                        data, ImuResponse::class.java
                    )
                    var acc = ACC(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayAcc[0].x.toString(),
                        y = imuResponse.body.arrayAcc[0].y.toString(),
                        z = imuResponse.body.arrayAcc[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addACC(acc)

                    var gyro = GYRO(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayGyro[0].x.toString(),
                        y = imuResponse.body.arrayGyro[0].y.toString(),
                        z = imuResponse.body.arrayGyro[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addGYRO(gyro)

                    var magn = MAGN(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayMagnl[0].x.toString(),
                        y = imuResponse.body.arrayMagnl[0].y.toString(),
                        z = imuResponse.body.arrayMagnl[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addMagn(magn)

                }

                override fun onError(error: MdsException) {
                    Timber.e("IMU9 onError(): $error")
                    unsubscribeImu()
                }
            })
    }

    private fun enableImu6() {
        // Make sure there is no subscription
        unsubscribeImu()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_IMU_6).append("\"}").toString()
        Timber.e(strContract)
        mImuSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")
                    val imuResponse: ImuResponse = Gson().fromJson(
                        data, ImuResponse::class.java
                    )
                    var acc = ACC(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayAcc[0].x.toString(),
                        y = imuResponse.body.arrayAcc[0].y.toString(),
                        z = imuResponse.body.arrayAcc[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addACC(acc)

                    var gyro = GYRO(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayGyro[0].x.toString(),
                        y = imuResponse.body.arrayGyro[0].y.toString(),
                        z = imuResponse.body.arrayGyro[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addGYRO(gyro)


                }

                override fun onError(error: MdsException) {
                    Timber.e("imu6 onError(): $error")
                    unsubscribeImu()
                }
            })
    }

    private fun enableImu6m() {
        // Make sure there is no subscription
        unsubscribeImu()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String = sb.append("{\"Uri\": \"").append(gv.currentDevice.serial)
            .append(Constants.URI_MEAS_IMU_6M).append("\"}").toString()
        Timber.e(strContract)
        mImuSubscription = Mds.builder().build(this)
            .subscribe(Constants.URI_EVENTLISTENER, strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Timber.e("onNotification(): $data")
                    val imuResponse: ImuResponse = Gson().fromJson(
                        data, ImuResponse::class.java
                    )
                    var acc = ACC(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayAcc[0].x.toString(),
                        y = imuResponse.body.arrayAcc[0].y.toString(),
                        z = imuResponse.body.arrayAcc[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addACC(acc)

                    var magn = MAGN(
                        id = 0,
                        userID = gv.userID,
                        x = imuResponse.body.arrayMagnl[0].x.toString(),
                        y = imuResponse.body.arrayMagnl[0].y.toString(),
                        z = imuResponse.body.arrayMagnl[0].z.toString(),
                        timestamp = imuResponse.body.timestamp,
                    )
                    addMagn(magn)


                }

                override fun onError(error: MdsException) {
                    Timber.e("imu6 onError(): $error")
                    unsubscribeImu()
                }
            })
    }

    fun unsubscribeAll() {
        Timber.e("unsubscribeAll()")
        unsubscribeImu()
        unsubscribeAcc()
        unsubscribeGYRO()
        unsubscribeECG()
        unsubscribeHR()
        unsubscribeMagn()
        unsubscribeTemp()
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

    private fun unsubscribeTemp() {
        if (mTempSubscription != null) {
            mTempSubscription!!.unsubscribe()
            mTempSubscription = null
        }
    }

    private fun unsubscribeImu() {
        if (mImuSubscription != null) {
            mImuSubscription!!.unsubscribe()
            mImuSubscription = null
        }
    }

    private fun addACC(acc: ACC) {
        lifecycleScope.launch(Dispatchers.IO) {
            accRepository.add(acc)
            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = ACC(
                        id = accRepository.getIdFromLastRecord(),
                        x = acc.x,
                        y = acc.y,
                        z = acc.z,
                        timestamp = acc.timestamp,
                        userID = acc.userID,
                        created = acc.created
                    )
                    addACCData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }

        }
    }

    private fun addMagn(magn: MAGN) {
        lifecycleScope.launch(Dispatchers.IO) {
            magnRepository.add(magn)

            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = GYRO(
                        id = magnRepository.getIdFromLastRecord(),
                        x = magn.x,
                        y = magn.y,
                        z = magn.z,
                        timestamp = magn.timestamp,
                        userID = magn.userID,
                        created = magn.created
                    )
                    addMagnData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }
        }
    }

    private fun addGYRO(gyro: GYRO) {
        lifecycleScope.launch(Dispatchers.IO) {
            gyroRepository.add(gyro)

            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = GYRO(
                        id = gyroRepository.getIdFromLastRecord(),
                        x = gyro.x,
                        y = gyro.y,
                        z = gyro.z,
                        timestamp = gyro.timestamp,
                        userID = gyro.userID,
                        created = gyro.created
                    )
                    addGyroData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }
        }
    }


    private fun addECG(ecg: ECG) {
        lifecycleScope.launch(Dispatchers.IO) {
            ecgRepository.add(ecg)

            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = ECG(
                        id = ecgRepository.getIdFromLastRecord(),
                        data = ecg.data,
                        timestamp = ecg.timestamp,
                        userID = ecg.userID,
                        created = ecg.created
                    )
                    addECGData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }
        }
    }

    fun addHr(hr: Hr) {
        lifecycleScope.launch(Dispatchers.IO) {
            hrRepository.add(hr)

            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = Hr(
                        id = hrRepository.getIdFromLastRecord(),
                        average = hr.average,
                        rrData = hr.rrData,
                        userID = hr.userID,
                        created = hr.created
                    )
                    addHrData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }
        }
    }

    fun addTemp(temp: TEMP) {
        lifecycleScope.launch(Dispatchers.IO) {
            tempRepository.add(temp)

            if (gv.isLiveDataActivated) {
                lifecycleScope.launch {
                    var aux = TEMP(
                        id = tempRepository.getIdFromLastRecord(),
                        measurement = temp.measurement,
                        timestamp = temp.timestamp,
                        userID = temp.userID,
                        created = temp.created
                    )
                    addTempData(
                        jsonString = "[" + Gson().toJson(aux) + "]", authToken = gv.authToken
                    )
                }

            }
        }
    }

    private fun showConnectionError(e: MdsException) {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this).setTitle("Connection Error:").setMessage(e.message)
        builder.create().show()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            moveSenseEvent.observe(this, Observer {
                when (it) {
                    is MoveSenseEvent.START -> {
                        Timber.e("estou aquiiiiii")
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
                            PendingIntent.getActivity(
                                this, 0, intent1, PendingIntent.FLAG_MUTABLE
                            )
                        } else {
                            PendingIntent.getActivity(
                                this, 0, intent1, PendingIntent.FLAG_ONE_SHOT
                            )
                        }
                        notification =
                            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title).setContentText(description)
                                .setSmallIcon(icon).setPriority(128).setContentIntent(pendingIntent)
                                .setOngoing(true)
                                .build()

                        startForeground(Constants.NOTIFICATION_ID, notification)

                    }

                    is MoveSenseEvent.STOP -> {

                    }
                }
            })
        }
    }

    private val _uploadDataAccResponses: MutableLiveData<Resource<UploadAccRespose>> =
        MutableLiveData()
    val uploadDataAccResponses: LiveData<Resource<UploadAccRespose>>
        get() = _uploadDataAccResponses

    fun addACCData(jsonString: String, authToken: String) = lifecycleScope.launch {
        //Efetua o post request atraves do apiRepository e guarda a resposta.
        _uploadDataAccResponses.value =
            apiRepository.addAccData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataAccResponses.value) {
            //Se a resposta for ok, então vai percorrer a listaAcc e vai remover todos os dados da room table.
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listAcc) {

                        if (!listAcc.isNullOrEmpty()) {
                            for (acc in listAcc) {
                                accRepository.deleteByID(acc.id)
                            }
                            listAcc.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroACC", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private val _uploadDataGyroResponses: MutableLiveData<Resource<UploadGyroRespose>> =
        MutableLiveData()
    val uploadDataGyroResponses: LiveData<Resource<UploadGyroRespose>>
        get() = _uploadDataGyroResponses

    fun addGyroData(jsonString: String, authToken: String) = lifecycleScope.launch {
        _uploadDataGyroResponses.value =
            apiRepository.addGyroData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataGyroResponses.value) {
            //Se a resposta for ok, então vai percorrer a listaGyro e vai remover todos os dados da room table.
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listGyro) {
                        if (!listGyro.isNullOrEmpty()) {
                            for (acc in listGyro) {
                                gyroRepository.deleteByID(acc.id)
                            }
                            listGyro.clear()
                        }
                    }


                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroGYRO", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private val _uploadDataMagnResponses: MutableLiveData<Resource<UploadMagnRespose>> =
        MutableLiveData()
    val uploadDataMagnResponses: LiveData<Resource<UploadMagnRespose>>
        get() = _uploadDataMagnResponses

    fun addMagnData(jsonString: String, authToken: String) = lifecycleScope.launch {
        _uploadDataMagnResponses.value =
            apiRepository.addMagnData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataMagnResponses.value) {
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listMagn) {

                        if (!listMagn.isNullOrEmpty()) {
                            for (acc in listMagn) {
                                magnRepository.deleteByID(acc.id)
                            }
                            listMagn.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroMAGN", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private val _uploadDataECGResponses: MutableLiveData<Resource<UploadECGRespose>> =
        MutableLiveData()
    val uploadDataECGResponses: LiveData<Resource<UploadECGRespose>>
        get() = _uploadDataECGResponses

    fun addECGData(jsonString: String, authToken: String) = lifecycleScope.launch {
        _uploadDataECGResponses.value =
            apiRepository.addEcgData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataAccResponses.value) {
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listECG) {

                        if (!listECG.isNullOrEmpty()) {
                            for (ecg in listECG) {
                                ecgRepository.deleteByID(ecg.id)
                            }
                            listECG.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroECG", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private val _uploadDataHRResponses: MutableLiveData<Resource<UploadHrRespose>> =
        MutableLiveData()
    val uploadDataHRResponses: LiveData<Resource<UploadHrRespose>>
        get() = _uploadDataHRResponses

    fun addHrData(jsonString: String, authToken: String) = lifecycleScope.launch {
        _uploadDataHRResponses.value =
            apiRepository.addHrData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataHRResponses.value) {
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listHr) {

                        if (!listHr.isNullOrEmpty()) {
                            for (acc in listHr) {
                                hrRepository.deleteByID(acc.id)
                            }
                            listHr.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroHR", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }


    private val _uploadDataTempResponses: MutableLiveData<Resource<UploadTempResponse>> =
        MutableLiveData()
    val uploadDataTempResponses: LiveData<Resource<UploadTempResponse>>
        get() = _uploadDataTempResponses

    fun addTempData(jsonString: String, authToken: String) = lifecycleScope.launch {
        _uploadDataTempResponses.value =
            apiRepository.addTempData(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataTempResponses.value) {
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listTemp) {

                        if (!listTemp.isNullOrEmpty()) {
                            for (temp in listTemp) {
                                tempRepository.deleteByID(temp.id)
                            }
                            listTemp.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
                Toast.makeText(this@MovesenseService, "ErroTEMP", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

}