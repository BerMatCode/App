package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.database.LetterRepository
import com.example.database.RomanticLetter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LetterRepository

    val allLetters: StateFlow<List<RomanticLetter>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LetterRepository(database.romanticLetterDao())
        
        allLetters = repository.allLetters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Populate preset letters if DB is empty
        viewModelScope.launch {
            if (repository.getCount() == 0) {
                insertPresetLetters()
            }
        }
    }

    private suspend fun insertPresetLetters() {
        val preset1 = RomanticLetter(
            title = "Nuestros Inicios 🌟",
            content = "Ber y Mat, desde el 10/11/2023, nuestras vidas cambiaron por completo. Recuerdo aquel primer día como si fuera ayer. Cada conversación, cada risa tímida y esos nervios hermosos que sentíamos. Desde el primer instante supe que eras alguien sumamente especial, una luz que iluminaría mi camino de formas inexplicables. ¡Hoy celebramos cada segundo de esa hermosa casualidad!",
            author = "Mat & Ber",
            isPreset = true
        )
        val preset2 = RomanticLetter(
            title = "Un Camino Juntos 🛤️",
            content = "Caminar a tu lado es la aventura más dulce de mi vida. Hemos compartido momentos mágicos, risas incontrolables, abrazos cálidos y también lecciones que nos han unido más. Me encanta cómo nos apoyamos en todo y cómo crecemos juntos día tras día. Gracias por ser mi confidente, mi refugio y el motivo constante de mis sonrisas.",
            author = "Amor Verdadero",
            isPreset = true
        )
        val preset3 = RomanticLetter(
            title = "Promesas del Mañana 💫",
            content = "Prometo amarte con la misma fuerza que hoy, cuidarte en los momentos difíciles y celebrar con locura tus alegrías. Ber y Mat no es solo el presente, es un universo entero por descubrir juntos. Sueño con el futuro a tu lado, tomados de la mano, superando cualquier reto y construyendo un hogar lleno de paz, risas y mucho cariño.",
            author = "Siempre Juntos",
            isPreset = true
        )
        val preset4 = RomanticLetter(
            title = "Agradecimiento Infinito 💖",
            content = "Gracias por ser exactamente como eres. Tu paciencia, tu ternura, tu inteligencia y tu dulce locura llenan mi alma de una felicidad indescriptible. No hay palabras suficientes en este mundo para expresar lo mucho que significas para mí. BerMatCode es mi recordatorio diario de que el amor de verdad se construye todos los días.",
            author = "Tu Amor Eterno",
            isPreset = true
        )

        repository.insert(preset1)
        repository.insert(preset2)
        repository.insert(preset3)
        repository.insert(preset4)
    }

    fun addLetter(title: String, content: String, author: String) {
        viewModelScope.launch {
            repository.insert(
                RomanticLetter(
                    title = title,
                    content = content,
                    author = author,
                    isPreset = false
                )
            )
        }
    }

    fun deleteLetter(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}
