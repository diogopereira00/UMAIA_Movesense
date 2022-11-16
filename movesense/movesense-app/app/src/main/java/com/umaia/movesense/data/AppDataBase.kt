package com.umaia.movesense.data

import Converters
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Hr::class, ECG::class], version = 3, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun hrDao(): HrDao
    abstract fun ecgDao(): ECGDao

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