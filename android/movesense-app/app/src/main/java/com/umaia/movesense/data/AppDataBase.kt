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

@Database(entities = [Hr::class, ECG::class, ACC::class, GYRO::class, MAGN::class], version = 7, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun hrDao(): HrDao
    abstract fun ecgDao(): ECGDao
    abstract fun accDao() : ACCDao
    abstract fun gyroDao(): GYRODao
    abstract fun magnDao(): MAGNDao

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