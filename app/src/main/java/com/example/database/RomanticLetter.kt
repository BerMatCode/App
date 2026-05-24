package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "romantic_letters")
data class RomanticLetter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val author: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPreset: Boolean = false
)
