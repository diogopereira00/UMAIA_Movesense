package com.umaia.movesense.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.*
import com.umaia.movesense.data.AppDataBase
import com.umaia.movesense.data.acc.ACCRepository
import com.umaia.movesense.data.gyro.GYRORepository
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyRepository
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.databinding.FragmentHomeBinding
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.ui.SurveyActivity
import com.umaia.movesense.ui.home.*
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.ViewModelFactory
import timber.log.Timber


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var gv: GlobalClass
    private lateinit var accRepository: ACCRepository
    private lateinit var gyroRepository: GYRORepository

    private lateinit var jsonString: String

    private lateinit var viewModel: ApiViewModel
    private lateinit var viewModelStudies: StudiesViewmodel

    private val remoteDataSource = RemoteDataSource()
    private lateinit var studyRepository: StudyRepository

    private var countAcc: Int = 0

    private var hasWifi = false

    private var studyVersionDB: Double? = null
    private var studyVersionAPI: Double? = null
    private lateinit var networkChecker: NetworkChecker

    private var survey: com.umaia.movesense.data.responses.studies_response.Survey? = null

    companion object Foo {
        var s_INSTANCE: Home? = null
    }


    override fun onResume() {
        super.onResume()
        Timber.e("Atenção ->>>>>>>>>>>>>>>>>>>> ${gv.getscannerECG()}")
        Timber.e("->>>>>>>>>>>>>>>>>> ${gv.isAccActivated}")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        gv = activity?.application as GlobalClass
        networkChecker = NetworkChecker(requireContext())
        binding.progressBar.visible(false)

        Timber.e("Atenção ->>>>>>>>>>>>>>>>>>>> ${gv.getscannerECG()}")
        val accDao = AppDataBase.getDatabase(requireContext()).accDao()
        accRepository = ACCRepository(accDao)
        val gyroDao = AppDataBase.getDatabase(requireContext()).gyroDao()
        gyroRepository = GYRORepository(gyroDao)
        val studyDao = AppDataBase.getDatabase(requireContext()).studyDao()
        studyRepository = StudyRepository(studyDao)

        val factory =
            ViewModelFactory(
                ApiRepository(api = remoteDataSource.buildApi(ServerApi::class.java), null)
            )

        val factoryStudies = ViewModelFactory(context = requireContext(), repository = null)



        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]

        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]



        setObservers()

        //TODO : mover isto para o splashscree se o login tiver efetuado, se nao faz login e faz pedido tambem
//        var checkForStudies = viewModelStudies.getStudyVersionById(studyID = "3")
//        checkForStudies.observe(viewLifecycleOwner, Observer { studyVersion ->
//            studyVersionDB = studyVersion
//
//            //se nao houver versao na base de dados, significa que nao há estudos
//            if (studyVersion == null) {
//                //vou buscar estudos a api
//                checkInternetAndGetStudies()
//
//            } else {
//                //se ja houver estudos, verifico a versão se é igual a do servidor
//                if (networkChecker.hasInternet()) {
//
//                    viewModel.getStudyVersion(studyID = "3", authToken = gv.authToken)
//                    viewModel.getStudyVersionResponse.observe(viewLifecycleOwner, Observer {
////                        binding.progressBar.visible(false)
//                        when (it) {
//                            is Resource.Success -> {
//                                //todo exprimentar quando tiver no servidor
//                                studyVersionAPI = it.value.version
//                                if (studyVersionAPI != studyVersionDB) {
//                                    gv.foundNewStudyVersion = true
//                                    checkInternetAndGetStudies()
//                                }
//                                //Os estudos nao foram alterados, então uso a base de dados local e guardo o current survey.
//                                else {
//                                    if (gv.currentSurvey == null) {
//                                        getSurvey(4)
//
//                                    }
//                                }
//                                Timber.e("api: " + it.value.version.toString())
//                                Timber.e("db: " + studyVersion)
//
//
//                            }
//                            is Resource.Failure -> {
//                                Timber.e("Erro")
//                            }
//                        }
//                    })
//                }
//            }
//
//        })
        s_INSTANCE = this
//        updateHR()

        binding.buttonStart.setOnClickListener {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
        binding.buttonStop.setOnClickListener {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }
        binding.buttonTest.setOnClickListener {
            gv.currentSurveyID = 3
            gv.currentSurvey = null
            survey = com.umaia.movesense.data.responses.studies_response.Survey(
                sections = mutableListOf(),
                survey_description = "",
                survey_expected_time = 0,
                survey_title = "",
                surveys_id = 0
            )
            if (gv.isServiceRunning) {
                getSurvey(4)
            } else {
                getSurvey(3)
            }
            if (!isSurveyCompleted) {
                binding.progressBar.visible(true)
            }
            val surveyChecker = Thread {
                while (!isSurveyCompleted) {

                    Thread.sleep(500)
                }
                Thread.sleep(500)

                (context as Activity).runOnUiThread {
                    binding.progressBar.visible(false)
                    try {

                        val intent = Intent(context, SurveyActivity::class.java)
                        startActivity(intent)
                        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    } catch (e: InterruptedException) {
                        Timber.e(e.toString())
                    }

                }

            }
            surveyChecker.start()


        }

//        //Vai buscar o tipo de perguntas
//        viewModel.getQuestionTypes.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Success -> {
//                    val response = it.value
//                    for (type in response.types) {
//                        //Adiciona os tipos de perguntas a base de dados room
//                        viewModelStudies.addTypes(
//                            QuestionTypes(
//                                id = type.id.toLong(),
//                                name = type.name
//                            )
//                        )
//                    }
//                    //Pede as opçoes
//                    viewModel.getOptions(gv.authToken)
//                }
//                is Resource.Failure -> {
//                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//        //Vai buscar as opções
//        viewModel.getOptionsReponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Success -> {
//                    val response = it.value
//                    for (option in response.options) {
//                        //Adiciona todas as opções a base de dados room
//                        viewModelStudies.optionAdd(
//                            Option(
//                                id = option.id.toLong(),
//                                text = option.text,
//                                isLikert = checkIntBoolean(option.isLikert),
//                                likertScale = option.likertScale
//                            )
//                        )
//                    }
//                    //Pede todos os estudos do utilizador
//                    viewModel.getAllStudiesFromId(gv.userID, gv.authToken)
//
//
//                }
//                is Resource.Failure -> {
//                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
//
//                }
//            }
//        }
//        //Recebe todos os estudos do utilizador
//        viewModel.getAllStudiesFromUserIDReponse.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Success -> {
//                    val studies = it.value
//                    //Percorre todos os estudos do user
//                    for (study in studies) {
//                        //Adiciona todos os estudos a base de dados room
//                        viewModelStudies.studyAdd(
//                            Study(
//                                id = study.study_id.toLong(),
//                                name = study.study_name,
//                                description = study.study_description,
//                                adminPassword = study.study_adminPassword,
//                                start_date = convertDate(study.study_startdate).time,
//                                end_date = convertDate(study.study_enddate).time,
//                                version = study.study_version
//                            )
//                        )
//                        //adiciona os estudos dos utilizador a base dados room
//                        viewModelStudies.userStudysAdd(
//                            UserStudies(
//                                user_id = gv.userID,
//                                study_id = study.study_id.toLong()
//                            )
//                        )
//                        var studyID = study.study_id
//                        //Percorre todos os questionarios dos estudos
//                        for (survey in study.surveys) {
//                            //Adiciona todos os surveys dos studies
//                            viewModelStudies.surveyAdd(
//                                Survey(
//                                    id = survey.surveys_id.toLong(),
//                                    study_id = studyID.toLong(),
//                                    title = survey.survey_title,
//                                    description = survey.survey_description,
//                                    expected_time = survey.survey_expected_time
//                                )
//                            )
//
//
//                            val surveyId = survey.surveys_id
//                            for (section in survey.sections) {
//                                //Adiciona todas as secções aos dos surveys
//                                viewModelStudies.sectionAdd(
//                                    Section(
//                                        id = section.section_id.toLong(),
//                                        survey_id = surveyId.toLong(),
//                                        name = section.section_name
//                                    )
//                                )
//                                val sectionID = section.section_id
//                                for (question in section.questions) {
//                                    //Adiciona todas as questoes aos das secçoes
//                                    viewModelStudies.questionAdd(
//                                        Question(
//                                            id = question.question_id.toLong(),
//                                            text = question.question_text,
//                                            question_type_id = question.question_type_id.toLong(),
//                                            section_id = sectionID.toLong()
//                                        )
//                                    )
//                                }
//                            }
//                        }
//
//                    }
//                    //Pede as opçoes de cada pergunta
//                    viewModel.getQuestionOptions(gv.authToken)
//
//
//
//                    Toast.makeText(context, "Dados adicionados", Toast.LENGTH_LONG).show()
//
//                }
//                is Resource.Failure -> {
//                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//        //Recebe as opções das perguntas
//        viewModel.getQuestionOptions.observe(viewLifecycleOwner) {
//            when (it) {
//                is Resource.Success -> {
//                    val response = it.value
//                    for (questionOption in response.question_options) {
//                        //Adiciona as opções a cada pergunta
//                        viewModelStudies.questionOptionsAdd(
//                            QuestionOption(
//                                id = questionOption.id.toLong(),
//                                question_id = questionOption.question_id.toLong(),
//                                option_id = questionOption.option_id.toLong()
//                            )
//                        )
//                    }
//                    //Houveram alterações então o current survey é alterado.
//                    if (gv.currentSurvey == null) {
//                        getSurvey(4)
//
//                    }
//
//                }
//                is Resource.Failure -> {
//                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
//
//                }
//            }
//        }
        return binding.root
    }


    private var isSurveyCompleted = false
    private val optionsByQuestionId =
        mutableMapOf<Int, MutableList<com.umaia.movesense.data.responses.studies_response.Option>>()

    private fun getSurvey(id: Long) {
        isSurveyCompleted = false
        optionsByQuestionId.clear()
        viewModelStudies.getSurveyByID(id)
        viewModelStudies.surveyItem.observe(viewLifecycleOwner, Observer { surveyInfo ->
            survey = null
            survey = com.umaia.movesense.data.responses.studies_response.Survey(
                sections = mutableListOf(),
                survey_description = surveyInfo!!.description!!,
                survey_expected_time = surveyInfo.expected_time!!,
                survey_title = surveyInfo.title!!,
                surveys_id = surveyInfo.id.toInt()!!
            )
            viewModelStudies.getSectionsByID(surveyInfo.id)
        })

        viewModelStudies.sectionItem.observe(viewLifecycleOwner, Observer { sections ->
            for (section in sections) {
                if (section.survey_id!!.toInt() == survey!!.surveys_id && !survey!!.sections.any { it.section_id == section.id.toInt() }) {
                    survey!!.sections.add(
                        com.umaia.movesense.data.responses.studies_response.Section(
                            section_id = section.id.toInt(),
                            section_name = section.name!!,
                            questions = mutableListOf()
                        )
                    )
                    viewModelStudies.getQuestionsBySectionID(section.id)
                }
            }
        })


        viewModelStudies.questionsItem.observe(viewLifecycleOwner, Observer { questions ->
            for (section in survey!!.sections) {
                for (question in questions) {
                    if (section.section_id == question.section_id!!.toInt() && ! survey!!.sections[survey!!.sections.indexOf(section)].questions.any { it.question_id == question.id.toInt() }) {
                        val newQuestion =
                            com.umaia.movesense.data.responses.studies_response.Question(
                                options = mutableListOf(),
                                question_id = question.id.toInt(),
                                question_text = question.text!!,
                                question_type_id = question.question_type_id!!.toInt()
                            )
                        survey!!.sections[survey!!.sections.indexOf(section)].questions.add(
                            newQuestion
                        )
                        optionsByQuestionId[question.id.toInt()] = newQuestion.options
                    }
                }
            }

            for (question in questions) {
                viewModelStudies.getQuestionOptions(question.id)
            }
        })
        viewModelStudies.questionOptionItem.observe(viewLifecycleOwner, Observer { options ->
            optionsByQuestionId[options[0].question_id!!.toInt()]?.addAll(options.map {
                com.umaia.movesense.data.responses.studies_response.Option(
                    option_id = it.option_id!!.toInt()
                )
            })
            // Check if all the options for all questions have been retrieved
            if (optionsByQuestionId.size == survey!!.sections.flatMap { it.questions }.size) {
                isSurveyCompleted = true
                // Do something with the completed survey here, for example:
                // showSurvey(survey)
                gv.currentSurvey = survey

            }
        })
    }


    private fun setObservers() {

        MovesenseService.moveSenseEvent.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
        MovesenseService.movesenseWifi.observe(viewLifecycleOwner, Observer {
            when (it) {
                is MovesenseWifi.AVAILABLE -> {
//                    binding.textviewConnectiviy.text = "Status is available"
                    viewModel.getQuestionTypes(gv.authToken)

                }

                is MovesenseWifi.UNAVAILABLE -> {
//                    binding.textviewConnectiviy.text = "Status is unavailable"
                }
            }
        })

        MovesenseService.movesenseTimer.observe(viewLifecycleOwner, Observer {
            updateTimer(it)
        })


    }

    private fun updateTimer(it: String?) {
        binding.timer.text = it.toString()
    }

    private fun updateUi(event: MoveSenseEvent) {
        when (event) {
            is MoveSenseEvent.START -> {
                switchService(true)
            }
            is MoveSenseEvent.STOP -> {
                switchService(false)
            }
        }
    }

    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        Timber.e("Status changed")
    }


    fun onStopClicked(view: View?) {
//        switchService(false)
        sendCommandToService(Constants.ACTION_STOP_SERVICE)

    }

    private fun sendCommandToService(action: String) {


        requireActivity().startService(Intent(context, MovesenseService::class.java).apply {
            this.action = action
        })
    }

    private fun switchService(isStarted: Boolean) {
        if (isStarted) {
            binding.buttonStart.visibility = View.GONE
            binding.buttonStop.visibility = View.VISIBLE
            binding.buttonTest.text = "TERMINAR TREINO"
//            binding.buttonTest.visibility = View.GONE
//            binding.buttonPosTreino.visibility = View.VISIBLE

            Timber.e("on")

        } else {
            binding.buttonStart.visibility = View.VISIBLE
            binding.buttonStop.visibility = View.GONE
            binding.buttonTest.text = "COMEÇAR TREINO"

//            binding.buttonTest.visibility = View.VISIBLE
//            binding.buttonPosTreino.visibility = View.GONE
            Timber.e("off")

        }

    }


}