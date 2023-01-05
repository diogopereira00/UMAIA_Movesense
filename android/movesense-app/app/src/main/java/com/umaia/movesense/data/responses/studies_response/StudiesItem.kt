package com.umaia.movesense.data.responses.studies_response

data class StudiesItem(
    val study_description: String,
    val study_enddate: String,
    val study_id: Int,
    val study_name: String,
    val study_adminPassword : String,
    val study_startdate: String,
    val study_version: Double,
    val surveys: List<Survey>
)