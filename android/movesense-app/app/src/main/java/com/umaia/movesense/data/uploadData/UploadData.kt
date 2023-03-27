package com.umaia.movesense.data.uploadData

import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.ecg.ECG
import com.umaia.movesense.data.gyro.GYRO
import com.umaia.movesense.data.hr.Hr
import com.umaia.movesense.data.magn.MAGN
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.surveys.QuestionOptionWithOption
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.data.temp.TEMP

data class UploadData(
    val accList : List<ACC>,
    val gyroList : List<GYRO>,
    val magnList : List<MAGN>,
    val ecgList : List<ECG>,
    val hrList : List<Hr>,
    val tempList : List<TEMP>,
    val usersSurveysWithAnswersList: List<UserSurveysA>
)
data class UserSurveysA(
    val id: Long = 0,
    var user_id: String? = "",
    var survey_id: Long? = 0,
    var start_time: Long? = 0,
    var end_time: Long? = System.currentTimeMillis(),
    var isCompleted: Boolean? = true,
    var answers: List<Answer>


    )

data class UsersSurveysWithAnswers(
    @Embedded
    var userSurvey: UserSurveys,

    @Relation(
        parentColumn = "id",
        entityColumn = "user_survey_id",
        entity = Answer::class,

        )
    var answers: List<Answer>
)