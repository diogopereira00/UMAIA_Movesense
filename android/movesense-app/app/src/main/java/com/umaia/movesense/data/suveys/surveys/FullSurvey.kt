package com.umaia.movesense.data.suveys.surveys

import androidx.room.Embedded
import androidx.room.Relation
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.sections.Section

// Pojo for full survey
data class FullSurvey(
    @Embedded
    var survey: Survey,

    @Relation(
        parentColumn = "id",
        entityColumn = "survey_id",
        entity = Section::class
    )
    var sections: List<SectionWithQuestions>
)

// Pojo for questions with options
data class QuestionWithOptions(
    @Embedded
    var question: Question,

    @Relation(
        parentColumn = "id",
        entityColumn = "question_id",
        entity = QuestionOption::class,

    )
    var options: List<QuestionOptionWithOption>
)
{
    val sortedOptions: List<QuestionOptionWithOption>
        get() = options.sortedBy { it.option.id }
}
data class QuestionOptionWithOption(
    @Embedded
    var questionOption: QuestionOption,

    @Relation(
        parentColumn = "option_id",
        entityColumn = "id",
        entity = Option::class
    )
    var option: Option
)
// Pojo for sections with questions
data class SectionWithQuestions(
    @Embedded
    var section: Section,

    @Relation(
        parentColumn = "id",
        entityColumn = "section_id",
        entity = Question::class
    )
    var questions: List<QuestionWithOptions>
)
