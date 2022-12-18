package com.umaia.movesense.data.responses

import com.google.gson.annotations.SerializedName

class TempResponse(
    @field:SerializedName("Body") val body: Body, @field:SerializedName(
        "Uri"
    ) val uri: String
) {

    inner class Body(
        @field:SerializedName("Timestamp")
        val timestamp: Long,
        @field:SerializedName("Measurement")
        val measurement: Double
    )
}