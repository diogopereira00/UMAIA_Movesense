package com.umaia.movesense.data.responses.studies_response

data class Question(
    val options: List<Option>,
    val question_id: Int,
    val question_text: String,
    val question_type_id: Int
)