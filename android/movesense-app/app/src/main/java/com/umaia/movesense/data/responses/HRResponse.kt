package com.umaia.movesense.data.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

/**
 * Created by lipponep on 22.11.2017.
 */
class HRResponse(body: Body) {
    @SerializedName("Body")
    val body: Body

    val created : Long
    init {
        this.body = body
        this.created = System.currentTimeMillis()
    }

    @Parcelize
    class Body(
        @field:SerializedName("average")
        val average: Float,

        @field:SerializedName("rrData")
        val rrData: IntArray,

        ) : Parcelable {
        var created: Long = System.currentTimeMillis()

        init {
            this.created = System.currentTimeMillis()
        }

        val createdDateFormatted: String
            get() = DateFormat.getDateTimeInstance().format(created)

    }

}