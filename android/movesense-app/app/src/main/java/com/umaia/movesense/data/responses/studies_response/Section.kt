package com.umaia.movesense.data.responses.studies_response

data class Section(
    val questions: MutableList<Question>,
    val section_id: Int,
    val section_name: String
)