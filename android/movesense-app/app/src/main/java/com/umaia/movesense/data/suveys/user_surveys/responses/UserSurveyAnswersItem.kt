package com.umaia.movesense.data.suveys.user_surveys.responses

data class UserSurveyAnswersItem(
    val answers: List<Answer>,
    val end_time: Long,
    val id: Long,
    val isCompleted: Boolean,
    val start_time: Long,
    val survey_id: Long,
    val user_id: String
)