package com.example.database

import kotlinx.coroutines.flow.Flow

class LetterRepository(private val romanticLetterDao: RomanticLetterDao) {
    val allLetters: Flow<List<RomanticLetter>> = romanticLetterDao.getAllLetters()

    suspend fun insert(letter: RomanticLetter) {
        romanticLetterDao.insertLetter(letter)
    }

    suspend fun deleteById(id: Int) {
        romanticLetterDao.deleteLetterById(id)
    }

    suspend fun getCount(): Int {
        return romanticLetterDao.getLettersCount()
    }
}
