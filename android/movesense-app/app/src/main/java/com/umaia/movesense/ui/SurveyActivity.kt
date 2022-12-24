package com.umaia.movesense.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.OrderedTask
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.TextChoice
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.steps.InstructionStep
import com.quickbirdstudios.surveykit.steps.QuestionStep
import com.quickbirdstudios.surveykit.steps.Step
import com.quickbirdstudios.surveykit.survey.SurveyView
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.R
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.data.responses.studies_response.OptionsResponse
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.databinding.ActivityLoginBinding
import com.umaia.movesense.databinding.ActivitySurveyBinding
import com.umaia.movesense.ui.home.observeOnce
import com.umaia.movesense.ui.surveys.InitialStep
import com.umaia.movesense.util.ViewModelFactory

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var userPreferences: UserPreferences
    lateinit var gv: GlobalClass
    private lateinit var viewModelStudies: StudiesViewmodel

    private var steps: MutableList<Step> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gv = application as GlobalClass
        val factoryStudies = ViewModelFactory(context = this, repository = null)

        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]

        val OtherQuestion = QuestionStep(title=  "Por favor explicite", text = "", answerFormat = AnswerFormat.TextAnswerFormat(maxLines = 1, hintText = "Introduza a sua opção"))

        var surveyView: SurveyView = binding.surveyView
        val step1 = InitialStep(
            title = gv.currentSurvey.survey_title,
            text = gv.currentSurvey.survey_description,
            expectedTime = gv.currentSurvey.survey_expected_time,
            buttonText = getString(R.string.start)
        )
        steps.add(step1)

        val sections = gv.currentSurvey.sections
        for (section in sections) {
            val step = InstructionStep(
                title = section.section_name,
                buttonText = "Começar"
            )
            steps.add(step)

            for (question in section.questions) {

                if (question.question_type_id === 1) {

                    val options: MutableList<TextChoice> = mutableListOf()


                    for (option in question.options) {
                        val optionTextLiveData = viewModelStudies.getOptionTextById(option.option_id.toLong())
                        optionTextLiveData.observe(this@SurveyActivity, Observer { optionText ->
                            options.add(
                                TextChoice(
                                    text = optionText,
                                    value = option.option_id.toString()
                                )
                            )
                        })
                    }
                    val stepq = QuestionStep(
                        title = section.section_name,
                        text = question.question_text,
                        answerFormat = AnswerFormat.SingleChoiceAnswerFormat(
                            textChoices = options,
                        )
                    )
                    steps.add(stepq)

                }

            }
        }

        val task = OrderedTask(steps = steps)

        val configuration = SurveyTheme(
            themeColorDark = ContextCompat.getColor(
                this,
                com.quickbirdstudios.surveykit.R.color.black
            ),
            themeColor = ContextCompat.getColor(
                this,
                com.quickbirdstudios.surveykit.R.color.cyan_normal
            ),
            textColor = ContextCompat.getColor(
                this,
                com.quickbirdstudios.surveykit.R.color.cyan_text
            ),
            abortDialogConfiguration = AbortDialogConfiguration(
                title = com.quickbirdstudios.surveykit.R.string.abort_dialog_title,
                message = R.string.abort_dialog_message,
                neutralMessage = R.string.no,
                negativeMessage = R.string.yes
            )
        )

        surveyView.start(task, configuration)


    }
}