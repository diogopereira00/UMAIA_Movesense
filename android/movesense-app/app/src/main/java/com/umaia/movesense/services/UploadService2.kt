package com.umaia.movesense.services


import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.movesense.mds.*
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
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.answers.AnswerRepository
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.temp.TEMP
import com.umaia.movesense.data.temp.TEMPRepository
import com.umaia.movesense.data.suveys.home.observeOnce
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.data.suveys.user_surveys.UserSurveysRepository
import com.umaia.movesense.data.suveys.user_surveys.responses.UserSurveyAnswersItem
import com.umaia.movesense.data.uploadData.UploadData
import com.umaia.movesense.data.uploadData.UserSurveysA
import kotlinx.coroutines.withContext

class UploadService2 : LifecycleService() {

    companion object {
        val uploadEvent = MutableLiveData<MoveSenseEvent>()
    }

    private lateinit var accRepository: ACCRepository
    private lateinit var hrRepository: HrRepository
    private lateinit var ecgRepository: ECGRepository
    private lateinit var gyroRepository: GYRORepository
    private lateinit var magnRepository: MAGNRepository
    private lateinit var tempRepository: TEMPRepository
    private lateinit var apiRepository: ApiRepository
    private lateinit var userSurveysRepository: UserSurveysRepository
    private lateinit var answersRepository: AnswerRepository
    private var isServiceStopped = true

    private lateinit var notification: Notification


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
    private var listUserSurveys: MutableList<UserSurveysA> = mutableListOf()
    private var listAnswers: MutableList<Answer> = mutableListOf()
    private lateinit var jsonStringAcc: String
    private lateinit var jsonStringGyro: String
    private lateinit var jsonStringMagn: String
    private lateinit var jsonStringHr: String
    private lateinit var jsonStringECG: String
    private lateinit var jsonStringTemp: String
    private lateinit var queryResult: UploadData
    private lateinit var accTable: LiveData<List<ACC>>


    override fun onCreate() {
        super.onCreate()
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
        val userSurveysDao = AppDataBase.getDatabase(this).userSurveysDao()
        val answerDao = AppDataBase.getDatabase(this).answerDao()

        hrRepository = HrRepository(hrDao)
        ecgRepository = ECGRepository(ecgDao)
        accRepository = ACCRepository(accDao)
        gyroRepository = GYRORepository(gyroDao)
        magnRepository = MAGNRepository(magnDao)
        tempRepository = TEMPRepository(tempDao)
        userSurveysRepository = UserSurveysRepository(userSurveysDao)
        answersRepository = AnswerRepository(answerDao)
        apiRepository = ApiRepository(api = remoteDataSource.buildApi(ServerApi::class.java), null)


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    Timber.d("Started Service")
                    startForegroundService()


                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                    stopService()
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun initValues() {
        uploadEvent.postValue(MoveSenseEvent.STOP)
    }

    private fun startForegroundService() {
        uploadEvent.postValue(MoveSenseEvent.START)
        sendDataToServer()

    }


    //Envia as informações para o servidor.
    fun sendDataToServer() {
        if (networkChecker.hasInternetWifi()) {
            lifecycleScope.launch() {
                queryResult = getQueryResult()
                Timber.e(queryResult.accList.size.toString())
                Timber.e(queryResult.magnList.size.toString())
                Timber.e(queryResult.gyroList.size.toString())
                Timber.e(queryResult.ecgList.size.toString())
                Timber.e(queryResult.hrList.size.toString())
                Timber.e(queryResult.tempList.size.toString())

                Timber.e(queryResult.usersSurveysWithAnswersList.size.toString())
//            val json = Gson().toJson(queryResult)
//            val accJson = Gson().toJson(queryResult.accList)
//            val magnJson = Gson().toJson(queryResult.magnList)
//            val gyroJson = Gson().toJson(queryResult.gyroList)
//            val hrJson = Gson().toJson(queryResult.hrList)
//            val ecgJson = Gson().toJson(queryResult.ecgList)
//            val tempJson = Gson().toJson(queryResult.tempList)

                val authToken = gv.authToken
//            listUserSurveys = queryResult.usersSurveysWithAnswersList.toMutableList()

                if (queryResult.usersSurveysWithAnswersList.isNotEmpty()) {
                    createNotification()
                    gv.isSyncing = true
                    val userSurveysA =
                        ObjectMapper().writeValueAsString(queryResult.usersSurveysWithAnswersList)
                    addUserSurveyData(jsonString = userSurveysA, authToken = authToken)
                } else {
                    if (queryResult.accList.isNotEmpty() || queryResult.gyroList.isNotEmpty() || queryResult.magnList.isNotEmpty() || queryResult.ecgList.isNotEmpty() || queryResult.hrList.isNotEmpty() || queryResult.tempList.isNotEmpty()) {
                        createNotification()
                        gv.isSyncing = true

                        addAllData(
                            queryResult, authToken = "Bearer $authToken"
                        )
                    }
                }
            }

        } else {
            stopService()
        }


    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            UploadService2.uploadEvent.observe(this, Observer {
                when (it) {
                    is MoveSenseEvent.START -> {
//                        var intent1 = Intent(this, MainActivity::class.java)
                        var title = "Sincronizando os dados"
                        var description = "Aguarde por favor"
                        var icon = R.mipmap.ic_umaia_yellow_logo


//                        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                            PendingIntent.getActivity(
//                                this, 0, intent1, PendingIntent.FLAG_MUTABLE
//                            )
//                        } else {
//                            PendingIntent.getActivity(
//                                this, 0, intent1, PendingIntent.FLAG_ONE_SHOT
//                            )
//                        }
                        notification =
                            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID2)
                                .setContentTitle(title).setContentText(description)
                                .setSmallIcon(icon).setPriority(1).setOngoing(true).build()

                        startForeground(Constants.NOTIFICATION_ID2, notification)

                    }

                    is MoveSenseEvent.STOP -> {

                    }
                }
            })
        }
    }


    private fun stopService() {
        uploadEvent.postValue(MoveSenseEvent.STOP)
        gv.isSyncing = false
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopSelf()

    }

    suspend fun getQueryResult(): UploadData {
        return withContext(Dispatchers.IO) {
            val accList = AppDataBase.getDatabase(application).accDao().getAll()
            val gyroList = AppDataBase.getDatabase(application).gyroDao().getAll()
            val magnList = AppDataBase.getDatabase(application).magnDao().getAll()
            val ecgList = AppDataBase.getDatabase(application).ecgDao().getAll()
            val hrList = AppDataBase.getDatabase(application).hrDao().getAll()
            val tempList = AppDataBase.getDatabase(application).tempDao().getAll()
//            val usersSurveysWithAnswersList = AppDataBase.getDatabase(application).userSurveysDao().getAll()

            val userSurveysWithAnswersList =
                AppDataBase.getDatabase(application).userSurveysDao().getAll()

            val userSurveyList = mutableListOf<UserSurveysA>()

            for (userSurveysWithAnswers in userSurveysWithAnswersList) {
                val userSurvey = userSurveysWithAnswers.userSurvey
                val answers = userSurveysWithAnswers.answers

                userSurveyList.add(
                    UserSurveysA(
                        userSurvey.id,
                        userSurvey.user_id,
                        userSurvey.survey_id,
                        userSurvey.start_time,
                        userSurvey.end_time,
                        userSurvey.isCompleted,
                        answers
                    )
                )
//                userSurvey.answers = answers
            }


            UploadData(accList, gyroList, magnList, ecgList, hrList, tempList, userSurveyList)
        }
    }


    private val _uploadAllDataResponses: MutableLiveData<Resource<UploadDataResponse>> =
        MutableLiveData()
    val uploadAllDataResponses: LiveData<Resource<UploadDataResponse>>
        get() = _uploadAllDataResponses

    fun addAllData(
        uploadData: UploadData, authToken: String
    ) = lifecycleScope.launch {
        _uploadAllDataResponses.value = apiRepository.addAllData(
            uploadData, authToken
        )
        when (_uploadAllDataResponses.value) {
            is Resource.Success -> {
                Timber.e("aqui")
                lifecycleScope.launch(Dispatchers.IO) {
                    if (!queryResult.accList.isNullOrEmpty()) {
                        for (acc in queryResult.accList) {
                            accRepository.deleteByID(acc.id)
                        }
                    }
                    if (!queryResult.gyroList.isNullOrEmpty()) {
                        for (gyro in queryResult.gyroList) {
                            gyroRepository.deleteByID(gyro.id)
                        }
                    }
                    if (!queryResult.magnList.isNullOrEmpty()) {
                        for (magn in queryResult.magnList) {
                            magnRepository.deleteByID(magn.id)
                        }
                    }
                    if (!queryResult.ecgList.isNullOrEmpty()) {
                        for (ecg in queryResult.ecgList) {
                            ecgRepository.deleteByID(ecg.id)
                        }
                    }
                    if (!queryResult.hrList.isNullOrEmpty()) {
                        for (hr in queryResult.hrList) {
                            hrRepository.deleteByID(hr.id)
                        }
                    }
                    if (!queryResult.tempList.isNullOrEmpty()) {
                        for (temp in queryResult.tempList) {
                            tempRepository.deleteByID(temp.id)
                        }
                    }

                    stopService()

                }
//
//                stopSelf()

            }
            is Resource.Failure -> {
                stopService()
            }
            else -> {}
        }
    }

    private val _uploadDataUserSurveysResponses: MutableLiveData<Resource<UploadUserSurveysResponse>> =
        MutableLiveData()
    val uploadDataUserSurveysResponses: LiveData<Resource<UploadUserSurveysResponse>>
        get() = uploadDataUserSurveysResponses

    fun addUserSurveyData(jsonString: String, authToken: String) = lifecycleScope.launch {
        //Efetua o post request atraves do apiRepository e guarda a resposta.
        _uploadDataUserSurveysResponses.value =
            apiRepository.addUserSurvey(jsonString = jsonString, authToken = "Bearer $authToken")
        when (_uploadDataUserSurveysResponses.value) {
            is Resource.Success -> {
                Timber.e("aqui")
                addAllData(
                    queryResult, authToken = "Bearer $authToken"
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(queryResult.usersSurveysWithAnswersList) {
                        if (!queryResult.usersSurveysWithAnswersList.isNullOrEmpty()) {
                            for (userSurvey in queryResult.usersSurveysWithAnswersList) {
                                for (answer in userSurvey.answers) {
                                    answersRepository.deleteByID(answer.id)
                                }
                                userSurveysRepository.deleteByID(userSurvey.id)
                            }
                        }
                    }
                }


//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
//                Toast.makeText(this@UploadService, "ErroACC", Toast.LENGTH_LONG).show()
                stopService()

            }
            else -> {}
        }
    }
}