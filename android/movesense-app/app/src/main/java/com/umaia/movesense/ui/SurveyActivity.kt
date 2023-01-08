package com.umaia.movesense.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.quickbirdstudios.surveykit.*
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.result.TaskResult
import com.quickbirdstudios.surveykit.steps.CompletionStep
import com.quickbirdstudios.surveykit.steps.InstructionStep
import com.quickbirdstudios.surveykit.steps.QuestionStep
import com.quickbirdstudios.surveykit.steps.Step
import com.quickbirdstudios.surveykit.survey.SurveyView
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.R
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.data.responses.studies_response.Option
import com.umaia.movesense.data.responses.studies_response.Question
import com.umaia.movesense.data.responses.studies_response.Section
import com.umaia.movesense.data.responses.studies_response.Survey
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.databinding.ActivitySurveyBinding
import com.umaia.movesense.ui.home.observeOnce
import com.umaia.movesense.ui.home.visible
import com.umaia.movesense.ui.surveys.InitialStep
import com.umaia.movesense.util.ViewModelFactory
import timber.log.Timber

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var userPreferences: UserPreferences
    lateinit var gv: GlobalClass
    private lateinit var viewModelStudies: StudiesViewmodel

    private var steps: MutableList<Step> = mutableListOf()
    private lateinit var survey: Survey
    private var start: Long? = null

    override fun onStop() {
        super.onStop()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)

        setContentView(binding.root)
        gv = application as GlobalClass
        val factoryStudies = ViewModelFactory(context = this, repository = null)

        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]


        var surveyView: SurveyView = binding.surveyView
        val step1 = InitialStep(
            title = gv.currentSurvey!!.survey_title,
            text = gv.currentSurvey!!.survey_description,
            expectedTime = gv.currentSurvey!!.survey_expected_time,
            buttonText = getString(R.string.start)
        )
        var complete = CompletionStep(
            title = "Done!",
            text = "Obrigado por realizar o questionario!",
            buttonText = "Submeter"
        )

        steps.add(step1)

        val sections = gv.currentSurvey!!.sections
        for (section in sections) {
            val step = InstructionStep(
                title = section.section_name,
                buttonText = "Começar"
            )
//            steps.add(step)

            for (question in section.questions) {

                if (question.question_type_id === 1) {

                    val options: MutableList<TextChoice> = mutableListOf()


                    for (option in question.options) {
                        val optionTextLiveData =
                            viewModelStudies.getOptionTextById(option.option_id.toLong())
                        optionTextLiveData.observeOnce(this@SurveyActivity, Observer { optionText ->
                            options.add(
                                TextChoice(
                                    text = optionText,
                                    value = option.option_id.toString()
                                )
                            )
                        })
                    }
                    val stepq = QuestionStep(
                        id = StepIdentifier(question.question_id.toString()),
                        title = question.question_text,
                        text = "",
                        answerFormat = AnswerFormat.SingleChoiceAnswerFormat(
                            textChoices = options,
                        )
                    )
                    steps.add(stepq)

                } else if (question.question_type_id == 2) {
                    //Texto Livre
                    val freeStep = QuestionStep(
                        id = StepIdentifier(question.question_id.toString()),
                        title = question.question_text,
                        text = "",
                        answerFormat = AnswerFormat.TextAnswerFormat(
                            maxLines = 2,
                            hintText = "Escreva aqui a sua resposta...",
                        )
                    )
                    steps.add(freeStep)


                } else if (question.question_type_id == 3) {
                    // Check if the question type is a Likert scale


                    // Get the option data for the question
                    try {
                        val surveyChecker = Thread {
                            while (question.options.size == 0) {
                                Thread.sleep(500)
                            }

                            this.runOnUiThread {
                                viewModelStudies.getOptionByID(question.options[0].option_id.toLong())


                            }
                        }
//                        viewModelStudies.getOptionByID(question.options[0].option_id.toLong())
                        surveyChecker.start()

                    } catch (e: InterruptedException) {
                        Timber.e(e.toString())
                    }


                    // Observe the option data and assign it to the option variable when it is available
                    viewModelStudies.optionsItem.observeOnce(
                        this@SurveyActivity,
                        Observer { optionData ->

                            // Check if the option text is not null or empty
                            if (!optionData.text.toString().isNullOrEmpty()) {
                                // Create a Likert scale step using the option data
                                val likertStep = QuestionStep(
                                    id = StepIdentifier(question.question_id.toString()),
                                    title = question.question_text,
                                    text = "Tenha em atenção que " + optionData.text,
                                    answerFormat = AnswerFormat.ScaleAnswerFormat(
                                        minimumValue = 1,
                                        maximumValue = optionData.likertScale!!.toInt(),
                                        defaultValue = optionData.likertScale!! / 2,
                                        minimumValueDescription = "1",
                                        maximumValueDescription = optionData.likertScale.toString(),
                                        // minimumValueDescription = getLikertScale(optionData.text!!, 0),
                                        //maximumValueDescription = getLikertScale(optionData.text!!, 1),
                                        step = 1f
                                    )
                                )
                                // Add the step to the list of steps
                                steps.add(likertStep)

                            }
                        })
                }


            }

        }

        surveyView.onStepResult = { step, stepResult ->
            if (step == step1) {
                Timber.e("teste1")
                start = System.currentTimeMillis()
            }
        }



        surveyView.onSurveyFinish = { taskResult: TaskResult, reason: FinishReason ->
            if (reason == FinishReason.Completed) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

                var userSurvey = UserSurveys(
                    id = 0,
                    user_id = gv.userID,
                    survey_id = gv.currentSurvey!!.surveys_id.toLong(),
                    start_time = start,
                    isCompleted = true
                )
                var y = Gson().toJson(userSurvey)
//                viewModelStudies.userSurveyID.observe(this@SurveyActivity) {
//                    Timber.e("x>${it.toString()}")
//                }
                var x = viewModelStudies.userSurveyAdd(userSurvey)

                taskResult.results.forEach { stepResult ->

                    Timber.e("answer ${stepResult.id} +  ${stepResult.results.firstOrNull()}")
//                    viewModelStudies.addAnswer(Answer(id=0, question_id = 3))
                    if (!stepResult.results.firstOrNull()!!.stringIdentifier.isNullOrEmpty()) {
                        val stepresult = stepResult.results.firstOrNull()
                        var answer = Answer(
                            id = 0,
                            question_id = stepResult.id.id.toLong(),
                            user_survey_id = gv.lastUserSurveyID!! +1L,
                            text = stepresult!!.stringIdentifier,
                            created_at = stepresult!!.startDate.time
                        )
                        var answex = Gson().toJson(answer)
                        viewModelStudies.addAnswer(answer)
                    }
                }

                gv.lastUserSurveyID = gv.lastUserSurveyID!! +1L

                finish()


            }

        }


        val configuration = SurveyTheme(
            themeColorDark = ContextCompat.getColor(
                this,
                com.quickbirdstudios.surveykit.R.color.black
            ),
            themeColor = ContextCompat.getColor(
                this,
                R.color.primary
            ),
            textColor = ContextCompat.getColor(
                this,
                R.color.defaultText
            ),
            abortDialogConfiguration = AbortDialogConfiguration(
                title = com.quickbirdstudios.surveykit.R.string.abort_dialog_title,
                message = R.string.abort_dialog_message,
                neutralMessage = R.string.no,
                negativeMessage = R.string.yes
            )
        )
        val handler = Handler()
        handler.postDelayed(Runnable
        {
            steps.add(complete)
        }, 1000
        ) //5 seconds

        val task = OrderedTask(steps = steps)

        surveyView.start(task, configuration)


    }
}