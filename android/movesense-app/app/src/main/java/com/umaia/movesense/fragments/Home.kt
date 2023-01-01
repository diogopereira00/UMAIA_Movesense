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
import com.quickbirdstudios.surveykit.TextChoice
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
import java.net.IDN


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
        var checkForStudies = viewModelStudies.getStudyVersionById(studyID = "3")
        checkForStudies.observe(viewLifecycleOwner, Observer { studyVersion ->
            studyVersionDB  = studyVersion

            //se nao houver versao na base de dados, significa que nao há estudos
            if (studyVersion == null) {
                //vou buscar estudos a api
                checkInternetAndGetStudies()

            } else {
                //se ja houver estudos, verifico a versão se é igual a do servidor
                if (networkChecker.hasInternet()) {

                    viewModel.getStudyVersion(studyID = "3", authToken = gv.authToken)
                    viewModel.getStudyVersionResponse.observe(viewLifecycleOwner, Observer {
//                        binding.progressBar.visible(false)
                        when (it) {
                            is Resource.Success -> {
                                //todo exprimentar quando tiver no servidor
                                studyVersionAPI = it.value.version
                                Timber.e(studyVersionAPI.toString())
                                Timber.e(studyVersionDB.toString())
                                if(studyVersionAPI != studyVersionDB){
                                    gv.foundNewStudyVersion = true
                                    checkInternetAndGetStudies()
                                }
                                Timber.e("api  :" +it.value.version.toDouble().toString())
                                Timber.e("db :" + studyVersion )


                            }
                            is Resource.Failure -> {
                                Timber.e("Erro")
                            }
                        }
                    })
                }
            }
        })
        s_INSTANCE = this
        updateHR()

        binding.buttonStart.setOnClickListener {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
        binding.buttonStop.setOnClickListener {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }









        binding.buttonTest.setOnClickListener {
//
            val intent = Intent(context, SurveyActivity::class.java)
            startActivity(intent)
//            (context as Activity).startNewActivity(SurveyActivity::class.java)
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        viewModel.getQuestionTypes.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (type in response.types) {
                        viewModelStudies.addTypes(
                            QuestionTypes(
                                id = type.id.toLong(),
                                name = type.name
                            )
                        )
                    }
                    viewModel.getOptions(gv.authToken)
                }
                is Resource.Failure -> {
                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getOptionsReponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (option in response.options) {
                        viewModelStudies.optionAdd(
                            Option(
                                id = option.id.toLong(),
                                text = option.text,
                                isLikert = checkIntBoolean(option.isLikert),
                                likertScale = option.likertScale
                            )
                        )
                    }
                    viewModel.getAllStudiesFromId(gv.userID, gv.authToken)


                }
                is Resource.Failure -> {
                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()

                }
            }
        }
        viewModel.getAllStudiesFromUserIDReponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val studies = it.value
                    //Percorre todos os estudos do user
                    for (study in studies) {
                        viewModelStudies.studyAdd(
                            Study(
                                id = study.study_id.toLong(),
                                name = study.study_name,
                                description = study.study_description,
                                start_date = convertDate(study.study_startdate).time,
                                end_date = convertDate(study.study_enddate).time,
                                version = study.study_version
                            )
                        )
                        viewModelStudies.userStudysAdd(
                            UserStudies(
                                user_id = gv.userID,
                                study_id = study.study_id.toLong()
                            )
                        )
                        var studyID = study.study_id
                        //Percorre todos os questionarios dos estudos
                        for (survey in study.surveys) {

                            viewModelStudies.surveyAdd(
                                Survey(
                                    id = survey.surveys_id.toLong(),
                                    study_id = studyID.toLong(),
                                    title = survey.survey_title,
                                    description = survey.survey_description,
                                    expected_time = survey.survey_expected_time
                                )
                            )
                            if (survey.surveys_id === 4) {
                                gv.currentSurvey = survey
                            }

                            val surveyId = survey.surveys_id
                            for (section in survey.sections) {
                                viewModelStudies.sectionAdd(
                                    Section(
                                        id = section.section_id.toLong(),
                                        survey_id = surveyId.toLong(),
                                        name = section.section_name
                                    )
                                )
                                val sectionID = section.section_id
                                for (question in section.questions) {
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

                    viewModel.getQuestionOptions(gv.authToken)



                    Toast.makeText(context, "Dados adicionados", Toast.LENGTH_LONG).show()

                }
                is Resource.Failure -> {
                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getQuestionOptions.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val response = it.value
                    for (questionOption in response.question_options) {
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
                    Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()

                }
            }
        }
        return binding.root
    }

    private fun checkInternetAndGetStudies() {
        //se tiver internet
        if (networkChecker.hasInternet()) {
            //verifico se tem wifi,ou se optou por usar dados moveis.
            if (networkChecker.hasInternetWifi() || gv.useMobileDataThisTime) {
                //se tiver vou buscar todos os dados
                viewModel.getQuestionTypes(gv.authToken)
            }
            //caso contrario, mostro um dialog que informa que tem internet mas nao ta com wifi.
            else {
                var dialog = DialogWifi(viewModel, (context as Activity))
                dialog.show((context as FragmentActivity).supportFragmentManager, ContentValues.TAG)
            }
        }
        //se nao tiver internet, mostro um dialog que pede para ligar a internet.
        else {
            var dialog = DialogInternet(viewModel, (context as Activity))
            dialog.show(
                (context as FragmentActivity).supportFragmentManager,
                ContentValues.TAG
            )

//            checkInternetAndGetStudies()
        }
    }


    private fun updateHR() {
        binding.textViewHR.text = gv.hrAvarage.toString()
        binding.textViewIBI.text = gv.hrRRdata

    }

    private fun setObservers() {

        MovesenseService.moveSenseEvent.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
        MovesenseService.movesenseWifi.observe(viewLifecycleOwner, Observer {
            when (it) {
                is MovesenseWifi.AVAILABLE -> {
                    binding.textviewConnectiviy.text = "Status is available"
                    viewModel.getQuestionTypes(gv.authToken)

                }

                is MovesenseWifi.UNAVAILABLE -> {
                    binding.textviewConnectiviy.text = "Status is unavailable"
                }
            }
        })

        MovesenseService.movesenseHeartRate.observe(viewLifecycleOwner, Observer {
            updateHR()
        })


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
            Timber.e("on")

        } else {
            binding.buttonStart.visibility = View.VISIBLE
            binding.buttonStop.visibility = View.GONE
            Timber.e("off")

        }

    }


}