package com.umaia.movesense.data.ecg

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
@Entity(tableName = "ecg_table")
@Parcelize
data class ECG(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var data : String,
    var timestamp : Long? = 0,
    val userID : String = "1",
    val created  : Long = System.currentTimeMillis(),
    ) : Parcelable{
    val createdDateFormated : String
        get() = DateFormat.getDateTimeInstance().format(created)
}