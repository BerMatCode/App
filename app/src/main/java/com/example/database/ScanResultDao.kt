package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanResult)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteScanById(id: Int)

    @Query("DELETE FROM scan_history")
    suspend fun clearAllHistory()
}
