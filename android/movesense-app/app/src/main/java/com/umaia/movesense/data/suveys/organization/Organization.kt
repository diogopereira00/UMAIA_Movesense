package com.umaia.movesense.data.suveys.organization

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
@Entity(tableName = "organization")
@Parcelize
data class Organization(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var x : String,
    var y : String,
    var z : String,
    var timestamp : Long? = 0,
    val userID : String = "1",
    val created  : Long = System.currentTimeMillis(),
) : Parcelable{
    val createdDateFormated : String
        get() = DateFormat.getDateTimeInstance().format(created)
}