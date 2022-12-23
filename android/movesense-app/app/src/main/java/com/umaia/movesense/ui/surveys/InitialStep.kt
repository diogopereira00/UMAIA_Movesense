package com.umaia.movesense.ui.surveys

import android.content.Context
import android.provider.Settings.Global.getString
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.backend.views.questions.IntroQuestionView
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.steps.Step
import com.umaia.movesense.R
import kotlin.math.exp

open class InitialStep(
    private val title: String? = null,
    private val text: String? = null,
    private val expectedTime : Int? = 0,
    private val buttonText: String = "Start",
    override var isOptional: Boolean = false,
    override val id: StepIdentifier = StepIdentifier()
) : Step {
    override fun createView(context: Context, stepResult: StepResult?) =
        IntroQuestionView(
            context = context,
            id = id,
            isOptional = isOptional,
            title = title,
            text = text +"\nTempo estimado: " + expectedTime.toString() + " minutos.",
            startButtonText = buttonText
        )
}