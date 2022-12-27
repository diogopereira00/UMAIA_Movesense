package com.umaia.movesense.data.suveys.questions_options

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.questions.Question

@Entity(tableName = "questions_options_table"
    //,
//    foreignKeys = [
//    ForeignKey(entity = Question::class, parentColumns = ["id"], childColumns = ["question_id"], onDelete = ForeignKey.CASCADE),
//    ForeignKey(entity = Option::class, parentColumns = ["id"], childColumns = ["option_id"], onDelete = ForeignKey.CASCADE)
//]
)
data class QuestionOption(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var question_id: Long? = 0,
    var option_id: Long? = 0
)