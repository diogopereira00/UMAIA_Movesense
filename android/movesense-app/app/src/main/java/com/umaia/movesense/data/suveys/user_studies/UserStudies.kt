package com.umaia.movesense.data.suveys.user_studies

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.surveys.Survey


@Entity(
    tableName = "user_studies",
    foreignKeys = [ForeignKey(
        entity = Study::class,
        parentColumns = ["id"],
        childColumns = ["study_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserStudies(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var user_id: String? = "",
    var study_id: Long? = 0,
)