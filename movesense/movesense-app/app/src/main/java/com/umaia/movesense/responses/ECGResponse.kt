package com.movesense.showcaseapp.model

import com.google.gson.annotations.SerializedName

class ECGResponse(@field:SerializedName("Body") val body: Body) {

    inner class Body(

        @field:SerializedName("Samples")
        val data: IntArray,

        @field:SerializedName("Timestamp")
        val timestamp: Long

    )
}