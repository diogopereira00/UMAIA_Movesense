package com.umaia.movesense.data.suveys.questions_types

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.surveys.Survey


@Entity(tableName = "questions_types")

data class QuestionTypes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String? = "",
)