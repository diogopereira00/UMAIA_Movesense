package com.umaia.movesense.data.suveys.options


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.*

@Entity(tableName = "options_table")
data class Option(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text : String? ="",
    val isLikert : Boolean? = false,
    val likertScale : Int?=0
)