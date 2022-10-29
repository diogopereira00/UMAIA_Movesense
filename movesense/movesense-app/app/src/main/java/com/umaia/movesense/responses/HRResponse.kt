package com.umaia.movesense.responses
import com.google.gson.annotations.SerializedName

/**
 * Created by lipponep on 22.11.2017.
 */
class HRResponse(body: Body) {
    @SerializedName("Body")
    val body: Body

    init {
        this.body = body
    }

    class Body(
        @field:SerializedName("average") val average: Float, @field:SerializedName(
            "rrData"
        ) val rrData: IntArray
    )
}