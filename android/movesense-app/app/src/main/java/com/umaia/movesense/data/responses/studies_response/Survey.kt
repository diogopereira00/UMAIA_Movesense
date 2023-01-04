package com.umaia.movesense.data.responses.studies_response

data class Survey(
    val sections: MutableList<Section>,
    val survey_created_at: String? = null,
    val survey_description: String,
    val survey_expected_time: Int,
    val survey_title: String,
    val survey_updated_at: String? = null,
    val surveys_id: Int
)