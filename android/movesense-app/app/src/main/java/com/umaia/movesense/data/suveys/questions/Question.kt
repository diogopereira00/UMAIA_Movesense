package com.umaia.movesense.data.suveys.questions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.sections.Section


@Entity(
    tableName = "questions_table"
    //,
//    foreignKeys = [
//
//        ForeignKey(
//            entity = QuestionTypes::class,
//            parentColumns = ["id"],
//            childColumns = ["question_type_id"],
//            onDelete = ForeignKey.CASCADE,
//        ),
//        ForeignKey(
//            entity = Section::class,
//            parentColumns = ["id"],
//            childColumns = ["section_id"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ]
)

data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String? = "",
    val question_type_id: Long? = 0,
    var section_id: Long? = 0,
)