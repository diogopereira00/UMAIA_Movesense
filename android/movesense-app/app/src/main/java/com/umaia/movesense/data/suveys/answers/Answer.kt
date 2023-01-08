package com.umaia.movesense.data.suveys.answers


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.questions.Question


@Entity(tableName = "answers_table", foreignKeys = [ForeignKey(entity = Question::class, parentColumns = ["id"], childColumns = ["question_id"], onDelete = ForeignKey.CASCADE)])
data class Answer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var question_id: Long? = 0,
    var user_survey_id: Long? = 0,
    var text: String?,
    var created_at: Long? = 0,
)