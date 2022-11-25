package com.umaia.movesense.data.responses

/**
 * Created by lipponep on 22.11.2017.
 */
import com.google.gson.annotations.SerializedName

class AccDataResponse(@field:SerializedName("Body") val body: Body) {
    class Body(
        @field:SerializedName("Timestamp") val timestamp: Long,
        array: kotlin.Array<Array>,
        header: Headers
    ) {
        @SerializedName("ArrayAcc")
        val array: kotlin.Array<Array>

        @SerializedName("Headers")
        val header: Headers

        init {
            this.array = array
            this.header = header
        }
    }

    class Array(
        @field:SerializedName("x") val x: Double, @field:SerializedName(
            "y"
        ) val y: Double, @field:SerializedName("z") val z: Double
    )

    class Headers(@field:SerializedName("Param0") val param0: Int)
}