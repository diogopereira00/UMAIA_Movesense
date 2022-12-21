//package com.umaia.movesense.data.suveys
//
//import android.os.Parcelable
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//import kotlinx.parcelize.Parcelize
//import java.text.DateFormat
//import java.util.*
//
//@Entity(tableName = "studies_table")
//data class Studies(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val name: String? = "",
//    val description: String? = "",
//    val start_date: Date,
//    val end_date: Date
//)
//
//
//@Entity(tableName = "surveys_table")
//data class Surveys(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    var study_id: Long? = 0,
//    var title: String? = "",
//    var description: String?,
//    var expected_time: Int? = 0,
//)
//
//@Entity(tableName = "sections_table")
//data class Sections(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    var survey_id: Long? = 0,
//    var name: String? = "",
//)
//
//@Entity(tableName = "questions_table")
//data class Questions(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val text : String? ="",
//    val question_type_id: Long? = 0,
//    var section_id: Long? = 0,
//)
//
//@Entity(tableName = "options_table")
//data class Options(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val text : String? ="",
//    val isLikert : Boolean? = false,
//    val likertScale : Int?=0
//)
//@Entity(tableName = "questions_options_table")
//data class QuestionOptions(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    var question_id: Long? = 0,
//    var option_id: Long? = 0
//)
//@Entity(tableName = "questions_types")
//data class QuestionTypes(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    var name: String? = "",
//)
//
