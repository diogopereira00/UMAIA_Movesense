package com.umaia.movesense.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
@Entity(tableName = "hr_table")
@Parcelize
data class Hr(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val average : Float,
    val rrData : Int,
    val userID : Int = 1,
    val created  : Long = System.currentTimeMillis(),

) : Parcelable{
    val createdDateFormated : String
        get() = DateFormat.getDateTimeInstance().format(created)
}