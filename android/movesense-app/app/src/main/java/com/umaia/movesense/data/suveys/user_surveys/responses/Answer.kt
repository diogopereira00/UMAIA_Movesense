package com.umaia.movesense.data.suveys.user_surveys.responses

data class Answer(
    val created_at: Long,
    val id: Long,
    val question_id: Long,
    val text: String,
    val user_survey_id: Long
)