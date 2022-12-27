package com.umaia.movesense.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.umaia.movesense.data.acc.ACC
import com.umaia.movesense.data.acc.ACCDao
import com.umaia.movesense.data.ecg.ECG
import com.umaia.movesense.data.ecg.ECGDao
import com.umaia.movesense.data.gyro.GYRO
import com.umaia.movesense.data.gyro.GYRODao
import com.umaia.movesense.data.hr.Hr
import com.umaia.movesense.data.hr.HrDao
import com.umaia.movesense.data.magn.MAGN
import com.umaia.movesense.data.magn.MAGNDao
import com.umaia.movesense.data.magn.TEMPDao

import com.umaia.movesense.data.suveys.options.Option
import com.umaia.movesense.data.suveys.options.OptionDao
import com.umaia.movesense.data.suveys.questions.Question
import com.umaia.movesense.data.suveys.questions.QuestionDao
import com.umaia.movesense.data.suveys.questions_options.QuestionOption
import com.umaia.movesense.data.suveys.questions_options.QuestionOptionDao
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
import com.umaia.movesense.data.suveys.questions_types.QuestionTypesDao
import com.umaia.movesense.data.suveys.sections.Section
import com.umaia.movesense.data.suveys.sections.SectionDao
import com.umaia.movesense.data.suveys.studies.Study
import com.umaia.movesense.data.suveys.studies.StudyDao
import com.umaia.movesense.data.suveys.surveys.Survey
import com.umaia.movesense.data.suveys.surveys.SurveyDao
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import com.umaia.movesense.data.suveys.user_studies.UserStudiesDao
import com.umaia.movesense.data.suveys.user_surveys.UserSurveys
import com.umaia.movesense.data.suveys.user_surveys.UserSurveysDao
import com.umaia.movesense.data.temp.TEMP

@Database(
    entities = [Hr::class, ECG::class, ACC::class, GYRO::class, MAGN::class, TEMP::class, Study::class, Survey::class, Section::class, Question::class, QuestionTypes::class, QuestionOption::class, Option::class,UserSurveys::class,UserStudies::class],
    version = 17,
    exportSchema = false
)

abstract class AppDataBase : RoomDatabase() {

    abstract fun hrDao(): HrDao
    abstract fun ecgDao(): ECGDao
    abstract fun accDao(): ACCDao
    abstract fun gyroDao(): GYRODao
    abstract fun magnDao(): MAGNDao
    abstract fun tempDao(): TEMPDao

    abstract fun studyDao(): StudyDao
    abstract fun surveyDao(): SurveyDao
    abstract fun sectionDao(): SectionDao
    abstract fun questionDao(): QuestionDao
    abstract fun optionDao(): OptionDao
    abstract fun questionTypesDao(): QuestionTypesDao
    abstract fun userStudiesDao(): UserStudiesDao
    abstract fun userSurveysDao(): UserSurveysDao
    abstract fun questionOptionsDao(): QuestionOptionDao


    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}