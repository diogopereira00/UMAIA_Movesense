package com.umaia.movesense.data.responses

import com.google.gson.annotations.SerializedName
import java.util.*


class ImuResponse(@field:SerializedName("Body") val body: Body) {

    override fun toString(): String {
        return "ImuModel{" +
                "mBody=" + body +
                '}'
    }

     inner class Body {
        @field:SerializedName("Timestamp")
        val timestamp: Long = 0

        @SerializedName("ArrayAcc")
        lateinit var arrayAcc: Array<ArrayAcc>

        @SerializedName("ArrayGyro")
        lateinit var arrayGyro: Array<ArrayGyro>

        @SerializedName("ArrayMagn")
        lateinit var arrayMagnl: Array<ArrayMagn>

        override fun toString(): String {
            return "Body{" +
                    "timestamp=" + timestamp +
                    ", mArrayAcc=" + Arrays.toString(arrayAcc) +
                    ", mArrayGyro=" + Arrays.toString(arrayGyro) +
                    ", mArrayMagnl=" + Arrays.toString(arrayMagnl) +
                    '}'
        }
    }

    inner class ArrayAcc(
        @field:SerializedName("x") val x: Double, @field:SerializedName(
            "y"
        ) val y: Double, @field:SerializedName("z") val z: Double
    ) {

        override fun toString(): String {
            return "ArrayAcc{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}'
        }
    }

    inner class ArrayGyro(
        @field:SerializedName("x") val x: Double, @field:SerializedName(
            "y"
        ) val y: Double, @field:SerializedName("z") val z: Double
    ) {

        override fun toString(): String {
            return "ArrayGyro{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}'
        }
    }

    inner class ArrayMagn(
        @field:SerializedName("x") val x: Double, @field:SerializedName(
            "y"
        ) val y: Double, @field:SerializedName("z") val z: Double
    ) {

        override fun toString(): String {
            return "ArrayMagn{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}'
        }
    }
}
