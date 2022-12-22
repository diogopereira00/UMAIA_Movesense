package com.umaia.movesense.fragments

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.data.AppDataBase
import com.umaia.movesense.data.acc.ACCRepository
import com.umaia.movesense.data.gyro.GYRORepository
import com.umaia.movesense.data.hr.HrRepository
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyRepository
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.databinding.FragmentHomeBinding
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.ui.home.convertDate
import com.umaia.movesense.ui.home.checkIntBoolean
import com.umaia.movesense.ui.home.observeOnce
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.sql.Time


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
        s_INSTANCE = this
        updateHR()

        binding.buttonStart.setOnClickListener {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
        binding.buttonStop.setOnClickListener {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }

        //TODO adiconar o resto dos sensores, efetuar mais alguns testes, e mudar o nome da resposta.
        binding.buttonTest.setOnClickListener {
            viewModel.getQuestionTypes(gv.authToken)

            viewModel.getQuestionTypes.observeOnce(viewLifecycleOwner){
                when (it) {
                    is Resource.Success -> {
                        val response = it.value
                        for(type in response.types){
                            viewModelStudies.addTypes(QuestionTypes(id = type.id.toLong(), name = type.name))
                        }
                        viewModel.getOptions(gv.authToken)

                    }
                    is Resource.Failure ->{
                        Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()

                    }
                }
            }

            viewModel.getOptionsReponse.observeOnce(viewLifecycleOwner){
                when (it) {
                    is Resource.Success -> {
                        val response = it.value
                        for(option in response.options){
                            viewModelStudies.optionAdd(Option(id = option.id.toLong(), text = option.text, isLikert = checkIntBoolean(option.isLikert), likertScale = option.likertScale))
                        }
                        viewModel.getAllStudiesFromId(gv.userID, gv.authToken)


                    }
                    is Resource.Failure ->{
                        Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()

                    }
                }
            }


//
            viewModel.getAllStudiesFromUserIDReponse.observeOnce(viewLifecycleOwner) {
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
                            viewModelStudies.userStudysAdd(UserStudies(user_id = gv.userID, study_id = study.study_id.toLong()))
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

                        Toast.makeText(context, "Dados adicionados", Toast.LENGTH_LONG).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        return binding.root
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