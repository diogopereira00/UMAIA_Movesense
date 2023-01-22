package com.umaia.movesense.ui.auth

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.umaia.movesense.*
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.databinding.ActivityLoginBinding
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.ui.ConsentActivity
import com.umaia.movesense.data.suveys.home.checkIntBoolean
import com.umaia.movesense.data.suveys.home.convertDate
import com.umaia.movesense.data.suveys.home.startNewActivity
import com.umaia.movesense.data.suveys.home.visible
import com.umaia.movesense.util.ViewModelFactory
import timber.log.Timber

open class LoginActivity : AppCompatActivity(), DialogWifi.OnDialogWifiDismissListener, DialogInternet.OnDialogInternetismissListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: ApiViewModel
    private val remoteDataSource = RemoteDataSource()
    private lateinit var userPreferences: UserPreferences
    lateinit var gv: GlobalClass

    private var studyVersionDB: Double? = null
    private var studyVersionAPI: Double? = null
    private lateinit var networkChecker: NetworkChecker
    private lateinit var viewModelStudies: StudiesViewmodel
    private lateinit var survey: com.umaia.movesense.data.responses.studies_response.Survey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        gv = application as GlobalClass

        val factory =
            ViewModelFactory(
                ApiRepository(
                    remoteDataSource.buildApi(ServerApi::class.java),
                    userPreferences
                )
            )
        val factoryStudies = ViewModelFactory(context = this, repository = null)
        networkChecker = NetworkChecker(this)

        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]
        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]

        if(gv.isLogged){
            checkForUpdates()
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
                    viewModel.getOptions(com.umaia.movesense.gv.authToken)
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
                    viewModel.getAllStudiesFromId(
                        com.umaia.movesense.gv.userID,
                        com.umaia.movesense.gv.authToken
                    )


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
                                user_id = com.umaia.movesense.gv.userID,
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
                    viewModel.getQuestionOptions(com.umaia.movesense.gv.authToken)



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

                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show()

                }
            }
        }

        binding.progressBar.visible(false)

// TODO: corrigir este metodo de validação
        binding.editTextPassword.addTextChangedListener {
            val username = binding.editTextUsername.text.toString().trim()
        }

        viewModel.loginResponse.observe(this, Observer {
            binding.progressBar.visible(false)
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    viewModel.saveAuthToken(it.value.user.access_token)
                    viewModel.saveUserID(it.value.user.id)
                    gv.userID = it.value.user.id
                    gv.authToken = it.value.user.access_token
                    gv.isLogged  =true
                    checkForUpdates()



                }
                is Resource.Failure -> {
                    Toast.makeText(this, "Login Failure", Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.loginButton.setOnClickListener {
            //@todo: validations
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            binding.progressBar.visible(true)
            viewModel.login(username, password)
        }
    }


    private fun checkForUpdates() {

        var checkCurrentUserSurveys = viewModelStudies.getUserSurveysIdFromLastRecord()
        checkCurrentUserSurveys.observe(this, Observer { userStudyID ->
            if(userStudyID == null){
                gv.lastUserSurveyID = 0

            }
            else{
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
                                    else{
                                        if (gv.consent) {
                                            this@LoginActivity.startNewActivity(ScanActivity::class.java)
                                        } else {
                                            this@LoginActivity.startNewActivity(ConsentActivity::class.java)
                                        }
                                    }
                                    //Os estudos nao foram alterados, então uso a base de dados local e guardo o current survey.
                                    Timber.e("api: " + it.value.version.toString())
                                    Timber.e("db: " + studyVersion)


                                }
                                is Resource.Failure -> {
                                    Timber.e("Erro")
                                }
                            }
                        })
                }
            }

        })
    }

    private fun checkInternetAndGetStudies() {
        //se tiver internet
        if (networkChecker.hasInternet()) {
            //verifico se tem wifi,ou se optou por usar dados moveis.
            if (networkChecker.hasInternetWifi() || gv.useMobileDataThisTime) {
                //se tiver vou buscar todos os dados
                viewModel.getQuestionTypes(gv.authToken)

                if (gv.consent) {
                    this@LoginActivity.startNewActivity(ScanActivity::class.java)
                } else {
                    this@LoginActivity.startNewActivity(ConsentActivity::class.java)
                }
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