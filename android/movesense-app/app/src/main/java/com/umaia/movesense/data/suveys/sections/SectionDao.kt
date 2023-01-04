package com.umaia.movesense.data.suveys.sections

import androidx.room.*
import com.umaia.movesense.data.suveys.surveys.Survey
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSection(section: Section)


    @Transaction
    @Query("SELECT * FROM sections_table where survey_id = :id")
    fun getSectionsByID(id: String) : Flow<List<Section>>
}