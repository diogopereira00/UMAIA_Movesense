package com.umaia.movesense.data.suveys.sections

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.surveys.Survey


@Entity(
    tableName = "sections_table",
    foreignKeys = [ForeignKey(
        entity = Survey::class,
        parentColumns = ["id"],
        childColumns = ["survey_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Section(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var survey_id: Long? = 0,
    var name: String? = "",
)