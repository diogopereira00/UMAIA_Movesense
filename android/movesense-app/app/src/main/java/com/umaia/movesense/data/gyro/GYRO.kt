package com.umaia.movesense.data.gyro

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
@Entity(tableName = "gyro_table")
@Parcelize
data class GYRO(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var x : String,
    var y : String,
    var z : String,
    var timestamp : Long? = 0,
    val userID : Int = 1,
    val created  : Long = System.currentTimeMillis(),
) : Parcelable{
    val createdDateFormated : String
        get() = DateFormat.getDateTimeInstance().format(created)
}