package com.umaia.movesense.data.suveys.surveys

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.umaia.movesense.data.suveys.studies.Study
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.*

@Entity(tableName = "surveys_table", foreignKeys = [ForeignKey(entity = Study::class, parentColumns = ["id"], childColumns = ["study_id"], onDelete = ForeignKey.CASCADE)])
data class Survey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var study_id: Long? = 0,
    var title: String? = "",
    var description: String?,
    var expected_time: Int? = 0,
)