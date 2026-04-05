package com.achtsamkeit.tagebuch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.achtsamkeit.tagebuch.data.local.entities.GuidedQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuidedQuestionDao {
    @Query("SELECT * FROM guided_questions")
    fun getAllQuestions(): Flow<List<GuidedQuestionEntity>>

    @Query("SELECT * FROM guided_questions WHERE isSelected = 1")
    fun getSelectedQuestions(): Flow<List<GuidedQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: GuidedQuestionEntity)

    @Query("SELECT * FROM guided_questions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): GuidedQuestionEntity?
}
