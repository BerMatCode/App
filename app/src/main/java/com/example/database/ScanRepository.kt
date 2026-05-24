package com.example.database

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanResultDao: ScanResultDao) {
    val allScans: Flow<List<ScanResult>> = scanResultDao.getAllScans()

    suspend fun insert(scan: ScanResult) {
        scanResultDao.insertScan(scan)
    }

    suspend fun deleteById(id: Int) {
        scanResultDao.deleteScanById(id)
    }

    suspend fun clearHistory() {
        scanResultDao.clearAllHistory()
    }
}
