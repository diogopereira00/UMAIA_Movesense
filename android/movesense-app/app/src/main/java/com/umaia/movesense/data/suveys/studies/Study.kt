package com.umaia.movesense.data.suveys.studies

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.*

@Entity(tableName = "studies_table")
data class Study(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = "",
    val description: String? = "",
    val adminPassword: String? = "",
    val start_date: Long? = null ,
    val end_date: Long? = null,
    val version : Double? = 0.0
)