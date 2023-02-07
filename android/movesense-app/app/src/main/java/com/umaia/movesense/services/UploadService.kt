package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.*
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

class UploadService : LifecycleService() {

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
    private var listUserSurveys: MutableList<UserSurveys> = mutableListOf()
    private var listAnswers: MutableList<Answer> = mutableListOf()
    private lateinit var jsonStringAcc: String
    private lateinit var jsonStringGyro: String
    private lateinit var jsonStringMagn: String
    private lateinit var jsonStringHr: String
    private lateinit var jsonStringECG: String
    private lateinit var jsonStringTemp: String

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
        accTable = accRepository.getAllACC
        accTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listAcc = it.toMutableList()
                jsonStringAcc = Gson().toJson(listAcc)
                addACCData(jsonString = jsonStringAcc, authToken = gv.authToken)

            }
        }
        var ecgTable = ecgRepository.getAllECG
        ecgTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listECG = it.toMutableList()
                jsonStringECG = Gson().toJson(listECG)
                addECGData(jsonString = jsonStringECG, authToken = gv.authToken)

            }
        }
        var gyroTable = gyroRepository.getAllGYRO
        gyroTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listGyro = it.toMutableList()
                jsonStringGyro = Gson().toJson(listGyro)
                addGyroData(jsonString = jsonStringGyro, authToken = gv.authToken)
            }
        }
        var magnTable = magnRepository.getAllMagn
        magnTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listMagn = it.toMutableList()
                jsonStringMagn = Gson().toJson(listMagn)
                addMagnData(jsonString = jsonStringMagn, authToken = gv.authToken)
            }
        }
        var hrTable = hrRepository.getAllHr
        hrTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listHr = it.toMutableList()
                jsonStringHr = Gson().toJson(listHr)
                addHrData(jsonString = jsonStringHr, authToken = gv.authToken)
            }
        }

        var userSurveys = userSurveysRepository.getAllUserSurveys
        userSurveys.observeOnce(this@UploadService) { userSurveys ->
            if (userSurveys.isNotEmpty()) {
                listUserSurveys = userSurveys.toMutableList()
                var answers = answersRepository.getAllAnswers
                answers.observeOnce(this@UploadService) { answers ->
                    if (answers.isNotEmpty()) {
                        listAnswers = answers.toMutableList()

                        val userSurveyAnswers = ArrayList<UserSurveyAnswersItem>()
                        for (userSurvey in listUserSurveys) {
                            val answers = listAnswers.filter { it.user_survey_id == userSurvey.id }
                            val mappedAnswers = answers.map {
                                com.umaia.movesense.data.suveys.user_surveys.responses.Answer(
                                    created_at = it.created_at!!,
                                    id = it.id,
                                    question_id = it.question_id!!,
                                    text = it.text!!,
                                    user_survey_id = it.user_survey_id!!
                                )
                            }
                            val userSurveyAnswersItem = UserSurveyAnswersItem(
                                answers = mappedAnswers,
                                end_time = userSurvey.end_time!!,
                                id = userSurvey.id!!,
                                isCompleted = userSurvey.isCompleted!!,
                                start_time = userSurvey.start_time!!,
                                survey_id = userSurvey.survey_id!!,
                                user_id = userSurvey.user_id!!
                            )
                            userSurveyAnswers.add(userSurveyAnswersItem)


                        }
                        val userSurveysAnswerJSON = Gson().toJson(userSurveyAnswers)
                        Timber.e(userSurveysAnswerJSON)
                        addUserSurveyData(
                            jsonString = userSurveysAnswerJSON,
                            authToken = gv.authToken
                        )


                    }
                }
            }
        }

        var tempTable = tempRepository.getAllTemp
        tempTable.observeOnce(this@UploadService) {
            if (it.size >= 2) {
                listTemp = it.toMutableList()
                jsonStringTemp = Gson().toJson(listTemp)
                addTempData(jsonString = jsonStringTemp, authToken = gv.authToken)


                Thread {
                    Thread.sleep(5000)
                    stopSelf()
                    stopService()

                }.start()


            }
        }


    }


    private fun stopService() {
        uploadEvent.postValue(MoveSenseEvent.STOP)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopSelf()

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
            //Se a resposta for ok, então vai percorrer a listaAcc e vai remover todos os dados da room table.
            is Resource.Success -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    synchronized(listUserSurveys) {

                        if (!listUserSurveys.isNullOrEmpty()) {
                            for (acc in listUserSurveys) {
                                userSurveysRepository.deleteByID(acc.id)
                            }
                            for (answer in listAnswers) {
                                answersRepository.deleteByID(answer.id)
                            }
                            listUserSurveys.clear()
                            listAnswers.clear()
                        }
                    }
                }
//                Toast.makeText(this@MovesenseService, "Dados adicionados", Toast.LENGTH_LONG).show()

            }
            is Resource.Failure -> {
//                Toast.makeText(this@UploadService, "ErroACC", Toast.LENGTH_LONG).show()
            }
            else -> {}
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
//                Toast.makeText(this@UploadService, "ErroACC", Toast.LENGTH_LONG).show()
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
//                Toast.makeText(this@UploadService, "ErroGYRO", Toast.LENGTH_LONG).show()
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
//                Toast.makeText(this@UploadService, "ErroMAGN", Toast.LENGTH_LONG).show()
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
//                Toast.makeText(this@UploadService, "ErroECG", Toast.LENGTH_LONG).show()
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
//                Toast.makeText(this@UploadService, "ErroHR", Toast.LENGTH_LONG).show()
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
                    dataStore.edit { preferences ->
                        preferences[UserPreferences.KEY_IS_SYNCKED] = true
                    }
                }
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
//                Toast.makeText(this@UploadService, "ErroTEMP", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

}