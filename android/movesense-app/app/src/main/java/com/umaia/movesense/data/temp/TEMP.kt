package com.umaia.movesense.data.temp

import android.location.GnssMeasurement
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "temp_table")
@Parcelize
data class TEMP(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var measurement: Double? = 0.0,
    var timestamp : Long? = 0,
    val userID : String = "1",
    val created  : Long = System.currentTimeMillis(),
) : Parcelable {
    val createdDateFormated : String
        get() = DateFormat.getDateTimeInstance().format(created)
}