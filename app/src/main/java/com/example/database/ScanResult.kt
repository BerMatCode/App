package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rawText: String,
    val title: String,
    val formatType: String, // "WIFI", "URL", "PHONE", "EMAIL", "TEXT", "WHATSAPP", "YAPE"
    val timestamp: Long = System.currentTimeMillis()
)
