package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RomanticLetterDao {
    @Query("SELECT * FROM romantic_letters ORDER BY timestamp DESC")
    fun getAllLetters(): Flow<List<RomanticLetter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetter(letter: RomanticLetter)

    @Query("DELETE FROM romantic_letters WHERE id = :idDirect")
    suspend fun deleteLetterById(idDirect: Int)

    @Query("SELECT COUNT(*) FROM romantic_letters")
    suspend fun getLettersCount(): Int
}
