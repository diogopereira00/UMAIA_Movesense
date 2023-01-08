package com.umaia.movesense.data.suveys.user_surveys

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.studies.Study

@Entity(tableName = "user_surveys", foreignKeys = [ForeignKey(entity = Study::class, parentColumns = ["id"], childColumns = ["survey_id"], onDelete = ForeignKey.CASCADE)])
data class UserSurveys(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var user_id: String? = "",
    var survey_id: Long? = 0,
    var start_time : Long? =  0,
    var end_time : Long? = System.currentTimeMillis(),
    var isCompleted : Boolean? = true
    )