package com.example

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.database.ScanRepository
import com.example.database.ScanResult
import com.example.utils.QRParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScanRepository

    val allScans: StateFlow<List<ScanResult>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ScanRepository(database.scanResultDao())
        
        allScans = repository.allScans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    /**
     * Add scanned result. Applies anti-vibration & parse structure
     */
    fun addScan(rawText: String) {
        if (rawText.isBlank()) return

        viewModelScope.launch {
            // Retrieve recent scans to prevent duplicate spam within 3 seconds for the exact same text
            val lastScan = allScans.value.firstOrNull()
            if (lastScan != null && lastScan.rawText == rawText && 
                System.currentTimeMillis() - lastScan.timestamp < 3000) {
                return@launch
            }

            val parsed = QRParser.parse(rawText)
            
            repository.insert(
                ScanResult(
                    rawText = rawText,
                    title = parsed.title,
                    formatType = parsed.type
                )
            )

            // Trigger physical feedback
            triggerVibration()
        }
    }

    fun deleteScan(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun triggerVibration() {
        val context = getApplication<Application>()
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(120)
                }
            }
        } catch (_: Exception) {}
    }
}
