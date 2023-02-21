package com.umaia.movesense.data.suveys.user_surveys

import androidx.lifecycle.LiveData
import androidx.room.*
import com.umaia.movesense.data.suveys.answers.Answer
import com.umaia.movesense.data.suveys.user_studies.UserStudies
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSurveysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserSurvey(userSurveys: UserSurveys): Long

    @Query("SELECT id from user_surveys ORDER BY id DESC LIMIT 1")
    suspend fun getIdFromLastRecord(): Long


    @Transaction
    @Query("SELECT * FROM user_surveys")
    fun getAllUserSurveys(): LiveData<List<UserSurveys>>


    @Query("DELETE FROM user_surveys WHERE id <(SELECT MAX(id) FROM user_surveys)")
    fun deleteAll()

    @Query("DELETE FROM user_surveys WHERE id = :id")
    fun deleteById(id: Long)
}