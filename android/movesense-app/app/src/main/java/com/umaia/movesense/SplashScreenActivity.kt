package com.umaia.movesense

import android.app.Activity
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.databinding.ActivityMainBinding
import com.umaia.movesense.databinding.ActivitySplashScreenBinding
import com.umaia.movesense.ui.ConsentActivity
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.data.suveys.home.checkIntBoolean
import com.umaia.movesense.data.suveys.home.convertDate
import com.umaia.movesense.data.suveys.home.startNewActivity
import com.umaia.movesense.data.suveys.home.startNewActivityFromSplash
import com.umaia.movesense.util.ViewModelFactory
import timber.log.Timber


private lateinit var binding: ActivitySplashScreenBinding
internal lateinit var userPreferences: UserPreferences
lateinit var gv: GlobalClass
private var studyVersionDB: Double? = null
private var studyVersionAPI: Double? = null
private val remoteDataSource = RemoteDataSource()

private lateinit var networkChecker: NetworkChecker
private lateinit var viewModel: ApiViewModel
private lateinit var viewModelStudies: StudiesViewmodel
private lateinit var survey: com.umaia.movesense.data.responses.studies_response.Survey

class SplashScreenActivity : AppCompatActivity(), DialogWifi.OnDialogWifiDismissListener, DialogInternet.OnDialogInternetismissListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gv = application as GlobalClass
        supportActionBar?.hide()
        val factory =
            ViewModelFactory(
                ApiRepository(api = remoteDataSource.buildApi(ServerApi::class.java), null)
            )

        val factoryStudies = ViewModelFactory(context = this, repository = null)


        networkChecker = NetworkChecker(this)

        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]

        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]
        userPreferences = UserPreferences(this)

        userPreferences.consent.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.consent = isActivated
            }
        }

        userPreferences.token.asLiveData().observe(this) { token ->
            val activity =
                if (token.isNullOrEmpty()) {
                    startNewActivityFromSplash(LoginActivity::class.java)
                } else{
                    gv.authToken = token
                    checkForUpdates()
                }
//            Toast.makeText(this, token ?: "Empty", Toast.LENGTH_SHORT).show()
        }



        userPreferences.userid.asLiveData().observe(this) { userID ->
            if (!userID.isNullOrEmpty()) {
                gv.userID = userID
            }
        }
        userPreferences.liveData.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isLiveDataActivated = isActivated
            }
            else{
                gv.isAccActivated = false
            }

        }

        userPreferences.accStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isAccActivated = isActivated
            }
            else{
                gv.isAccActivated = true
            }
        }
        userPreferences.gyroStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isGyroActivated = isActivated
            }
            else{
                gv.isGyroActivated = true
            }
        }
        userPreferences.magnStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isMagnActivated = isActivated
            }
            else{
                gv.isMagnActivated = true
            }
        }
        userPreferences.ecgStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isECGActivated = isActivated
            }
            else{
                gv.isECGActivated = true
            }
        }
        userPreferences.hrStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isHRActivated = isActivated
            }
            else{
                gv.isHRActivated = true
            }
        }
        userPreferences.tempStatus.asLiveData().observe(this) { isActivated ->
            if (isActivated != null) {
                gv.isTempActivated = isActivated
            }
            else{
                gv.isTempActivated = true
            }
        }

        //Vai buscar o tipo de perguntas
        viewModel.getQuestionTypes.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (type in response.types) {
                        //Adiciona os tipos de perguntas a base de dados room
                        viewModelStudies.addTypes(
                            QuestionTypes(
                                id = type.id.toLong(),
                                name = type.name
                            )
                        )
                    }
                    //Pede as opçoes
                    viewModel.getOptions(gv.authToken)
                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()
                }
            }
        }
        //Vai buscar as opções
        viewModel.getOptionsReponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (option in response.options) {
                        //Adiciona todas as opções a base de dados room
                        viewModelStudies.optionAdd(
                            Option(
                                id = option.id.toLong(),
                                text = option.text,
                                isLikert = checkIntBoolean(option.isLikert),
                                likertScale = option.likertScale
                            )
                        )
                    }
                    //Pede todos os estudos do utilizador
                    viewModel.getAllStudiesFromId(gv.userID, gv.authToken)


                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()

                }
            }
        }
        //Recebe todos os estudos do utilizador
        viewModel.getAllStudiesFromUserIDReponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val studies = it.value
                    //Percorre todos os estudos do user
                    for (study in studies) {
                        //Adiciona todos os estudos a base de dados room
                        viewModelStudies.studyAdd(
                            Study(
                                id = study.study_id.toLong(),
                                name = study.study_name,
                                description = study.study_description,
                                adminPassword = study.study_adminPassword,
                                start_date = convertDate(study.study_startdate).time,
                                end_date = convertDate(study.study_enddate).time,
                                version = study.study_version
                            )
                        )
                        //adiciona os estudos dos utilizador a base dados room
                        viewModelStudies.userStudysAdd(
                            UserStudies(
                                user_id = gv.userID,
                                study_id = study.study_id.toLong()
                            )
                        )
                        var studyID = study.study_id
                        //Percorre todos os questionarios dos estudos
                        for (survey in study.surveys) {
                            //Adiciona todos os surveys dos studies
                            viewModelStudies.surveyAdd(
                                Survey(
                                    id = survey.surveys_id.toLong(),
                                    study_id = studyID.toLong(),
                                    title = survey.survey_title,
                                    description = survey.survey_description,
                                    expected_time = survey.survey_expected_time
                                )
                            )


                            val surveyId = survey.surveys_id
                            for (section in survey.sections) {
                                //Adiciona todas as secções aos dos surveys
                                viewModelStudies.sectionAdd(
                                    Section(
                                        id = section.section_id.toLong(),
                                        survey_id = surveyId.toLong(),
                                        name = section.section_name
                                    )
                                )
                                val sectionID = section.section_id
                                for (question in section.questions) {
                                    //Adiciona todas as questoes aos das secçoes
                                    viewModelStudies.questionAdd(
                                        Question(
                                            id = question.question_id.toLong(),
                                            text = question.question_text,
                                            question_type_id = question.question_type_id.toLong(),
                                            section_id = sectionID.toLong()
                                        )
                                    )
                                }
                            }
                        }

                    }
                    //Pede as opçoes de cada pergunta
                    viewModel.getQuestionOptions(gv.authToken)



                    Toast.makeText(this, "Dados adicionados", Toast.LENGTH_LONG).show()

                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()
                }
            }
        }
        //Recebe as opções das perguntas
        viewModel.getQuestionOptions.observe(this) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (questionOption in response.question_options) {
                        //Adiciona as opções a cada pergunta
                        viewModelStudies.questionOptionsAdd(
                            QuestionOption(
                                id = questionOption.id.toLong(),
                                question_id = questionOption.question_id.toLong(),
                                option_id = questionOption.option_id.toLong()
                            )
                        )
                    }
                    //Houveram alterações então o current survey é alterado.
                    if (gv.currentSurvey == null) {
                        getSurvey(4)
                    }

                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()

                }
            }
        }


    }

    private fun changeActivity(){
        if (!gv.consent) {
            startNewActivityFromSplash(ConsentActivity::class.java)
        } else if (!gv.connected) {
            startNewActivityFromSplash(ScanActivity::class.java)
        } else {
            startNewActivityFromSplash(MainActivity::class.java)
        }
    }

    private fun checkForUpdates() {

        var checkCurrentUserSurveys = viewModelStudies.getUserSurveysIdFromLastRecord()
        checkCurrentUserSurveys.observe(this, Observer { userStudyID ->
            if (userStudyID == null) {
                gv.lastUserSurveyID = 0

            } else {
                gv.lastUserSurveyID = userStudyID
            }
        })

        var checkForStudies = viewModelStudies.getStudyVersionById(studyID = "3")
        checkForStudies.observe(this, Observer { studyVersion ->
            studyVersionDB = studyVersion

            //se nao houver versao na base de dados, significa que nao há estudos
            if (studyVersion == null) {
                //vou buscar estudos a api
                checkInternetAndGetStudies()

            } else {
                //se ja houver estudos, verifico a versão se é igual a do servidor
                if (networkChecker.hasInternet()) {

                    viewModel.getStudyVersion(studyID = "3", authToken = gv.authToken)
                    viewModel.getStudyVersionResponse.observe(
                        this,
                        Observer {
//                        binding.progressBar.visible(false)
                            when (it) {
                                is Resource.Success -> {
                                    //todo exprimentar quando tiver no servidor
                                    studyVersionAPI = it.value.version
                                    if (studyVersionAPI != studyVersionDB) {
                                        gv.foundNewStudyVersion = true
                                        checkInternetAndGetStudies()
                                    }
                                    //Os estudos nao foram alterados, então uso a base de dados local e guardo o current survey.
                                    else {
                                        changeActivity()
                                    }
                                    Timber.e("api: " + it.value.version.toString())
                                    Timber.e("db: " + studyVersion)


                                }
                                is Resource.Failure -> {
                                    Timber.e("Erro")
                                    changeActivity()

                                }
                            }
                        })
                }
                else{
                    changeActivity()
                }
            }

        })
    }

    private fun getSurvey(id: Long) {
        viewModelStudies.getSurveyByID(id)
        viewModelStudies.surveyItem.observe(
            this,
            Observer { surveyInfo ->
//                Timber.e(surveyInfo.title)
                survey = com.umaia.movesense.data.responses.studies_response.Survey(
                    sections = mutableListOf<com.umaia.movesense.data.responses.studies_response.Section>(),
                    survey_description = surveyInfo!!.description!!,
                    survey_expected_time = surveyInfo.expected_time!!,
                    survey_title = surveyInfo.title!!,
                    surveys_id = surveyInfo.id.toInt()!!
                )
                viewModelStudies.getSectionsByID(surveyInfo.id)

            })

        viewModelStudies.sectionItem.observe(this, Observer { sections ->
//            Timber.e(sections[0].name)
            for (section in sections) {
                survey.sections.add(
                    com.umaia.movesense.data.responses.studies_response.Section(
                        section_id = section.id.toInt(),
                        section_name = section.name!!,
                        questions = mutableListOf<com.umaia.movesense.data.responses.studies_response.Question>()
                    )
                )

                viewModelStudies.getQuestionsBySectionID(section.id)
            }
//            Timber.e(survey.sections[0].section_name)

        })
// Declare optionsByQuestionId in a higher scope so that it can be accessed by both observers
        val optionsByQuestionId =
            mutableMapOf<Int, MutableList<com.umaia.movesense.data.responses.studies_response.Option>>()

        viewModelStudies.questionsItem.observe(this, Observer { questions ->
//            Timber.e(questions[0].text)

            // Add the questions to the survey
            for (section in survey.sections) {
                for (question in questions) {
                    if (section.section_id == question.section_id!!.toInt()) {
                        val newQuestion =
                            com.umaia.movesense.data.responses.studies_response.Question(
                                options = mutableListOf(),
                                question_id = question.id.toInt(),
                                question_text = question.text!!,
                                question_type_id = question.question_type_id!!.toInt()
                            )
                        survey.sections[survey.sections.indexOf(section)].questions.add(newQuestion)

                        // Store the options for this question in the map
                        optionsByQuestionId[question.id.toInt()] = newQuestion.options
                    }
                }
            }

            // Retrieve the options for each question
            for (question in questions) {
                viewModelStudies.getQuestionOptions(question.id)
            }
        })

        viewModelStudies.questionOptionItem.observe(this, Observer { options ->
//            Timber.e(options[0].option_id.toString())

            // Add the options to the correct question using the map
            optionsByQuestionId[options[0].question_id!!.toInt()]?.addAll(options.map {
                com.umaia.movesense.data.responses.studies_response.Option(
                    option_id = it.option_id!!.toInt()
                )
            })
        })

    }

    private fun checkInternetAndGetStudies() {
        //se tiver internet
        if (networkChecker.hasInternet()) {
            //verifico se tem wifi,ou se optou por usar dados moveis.
            if (networkChecker.hasInternetWifi() || gv.useMobileDataThisTime) {
                //se tiver vou buscar todos os dados
                changeActivity()
                viewModel.getQuestionTypes(gv.authToken)
            }
            //caso contrario, mostro um dialog que informa que tem internet mas nao ta com wifi.
            else {
                var dialog = DialogWifi(viewModel, (this))
                dialog.setOnDialogDismissListener(this)
                dialog.show((this).supportFragmentManager, ContentValues.TAG)
            }
        }
        //se nao tiver internet, mostro um dialog que pede para ligar a internet.
        else {
            var dialog = DialogInternet(viewModel, (this))
            dialog.setOnDialogDismissListener(this)
            dialog.show(
                (this as FragmentActivity).supportFragmentManager,
                ContentValues.TAG
            )

//            checkInternetAndGetStudies()
        }
    }

    override fun onDialogWifiDismiss() {
        checkInternetAndGetStudies()
    }

    override fun onDialogInternetDismiss() {
        checkInternetAndGetStudies()
    }

}