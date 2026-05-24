package com.example.utils

import android.net.Uri

data class ParsedQR(
    val title: String,
    val type: String, // "WIFI", "URL", "PHONE", "EMAIL", "TEXT", "WHATSAPP", "YAPE"
    val displayDetails: String,
    val actionLabel: String,
    val iconEmoji: String
)

object QRParser {
    fun parse(rawText: String): ParsedQR {
        val trimmed = rawText.trim()

        // 1. WhatsApp check
        if (trimmed.contains("wa.me", ignoreCase = true) || 
            trimmed.contains("api.whatsapp.com/send", ignoreCase = true) ||
            trimmed.startsWith("whatsapp://", ignoreCase = true)) {
            
            // Extract phone if possible
            val phone = extractPhoneFromWhatsApp(trimmed)
            return ParsedQR(
                title = "Chat de WhatsApp",
                type = "WHATSAPP",
                displayDetails = if (phone.isNotEmpty()) "Enviar mensaje al número: $phone" else trimmed,
                actionLabel = "Abrir WhatsApp 💬",
                iconEmoji = "💬"
            )
        }

        // 2. Yape/Plin payment check (Specific Peruvian payment apps often embed details like 'yape.pe', custom transfer strings or plain phone numbers with amount)
        if (trimmed.startsWith("yape:", ignoreCase = true) ||
            trimmed.contains("yape.pe", ignoreCase = true) ||
            trimmed.contains("plin://", ignoreCase = true) ||
            (trimmed.contains("cobro", ignoreCase = true) && trimmed.contains("monto", ignoreCase = true))) {
            return ParsedQR(
                title = "Pago Móvil (Yape/Plin)",
                type = "YAPE",
                displayDetails = "Detalle de cobro/transferencia:\n$trimmed",
                actionLabel = "Ir a Pagar 💸",
                iconEmoji = "💸"
            )
        }

        // 3. WiFi check
        // Standard format: WIFI:S:MyNetwork;T:WPA;P:somepassword;;
        if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
            val ssid = extractParameter(trimmed, "S:")
            val password = extractParameter(trimmed, "P:")
            val security = extractParameter(trimmed, "T:")
            val details = buildString {
                append("Red (SSID): $ssid")
                if (security.isNotEmpty()) append("\nSeguridad: $security")
                if (password.isNotEmpty()) append("\nContraseña: $password")
            }
            return ParsedQR(
                title = "Configuración WiFi",
                type = "WIFI",
                displayDetails = details,
                actionLabel = "Conectar a Red 📶",
                iconEmoji = "📶"
            )
        }

        // 4. URL/Link check
        if (trimmed.startsWith("http://", ignoreCase = true) || 
            trimmed.startsWith("https://", ignoreCase = true) ||
            (trimmed.contains(".") && trimmed.length > 5 && !trimmed.contains(" ") && trimmed.split(".")[1].length >= 2)) {
            
            val cleanUrl = if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                "https://$trimmed"
            } else {
                trimmed
            }
            return ParsedQR(
                title = "Enlace Web",
                type = "URL",
                displayDetails = cleanUrl,
                actionLabel = "Visitar Sitio Web 🌐",
                iconEmoji = "🌐"
            )
        }

        // 5. Phone check
        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val number = trimmed.substring(4)
            return ParsedQR(
                title = "Número de Teléfono",
                type = "PHONE",
                displayDetails = "Llamar al número: $number",
                actionLabel = "Marcar Número 📞",
                iconEmoji = "📞"
            )
        }
        val numericOnly = trimmed.filter { it.isDigit() }
        if (numericOnly.length >= 7 && numericOnly.length <= 15 && trimmed.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }) {
            return ParsedQR(
                title = "Número de Teléfono",
                type = "PHONE",
                displayDetails = "Llamar al número: $trimmed",
                actionLabel = "Marcar Número 📞",
                iconEmoji = "📞"
            )
        }

        // 6. Email check
        if (trimmed.startsWith("mailto:", ignoreCase = true)) {
            val address = trimmed.substring(7).split("?")[0]
            return ParsedQR(
                title = "Correo Electrónico",
                type = "EMAIL",
                displayDetails = "Enviar correo a: $address",
                actionLabel = "Redactar Correo ✉️",
                iconEmoji = "✉️"
            )
        }
        if (trimmed.contains("@") && trimmed.contains(".") && !trimmed.contains(" ")) {
            return ParsedQR(
                title = "Correo Electrónico",
                type = "EMAIL",
                displayDetails = "Enviar correo a: $trimmed",
                actionLabel = "Redactar Correo ✉️",
                iconEmoji = "✉️"
            )
        }

        // 7. General Custom Plain Text
        return ParsedQR(
            title = "Texto Libre",
            type = "TEXT",
            displayDetails = trimmed,
            actionLabel = "Copiar Texto 📋",
            iconEmoji = "📝"
        )
    }

    private fun extractParameter(wifiString: String, prefix: String): String {
        val index = wifiString.indexOf(prefix, ignoreCase = true)
        if (index == -1) return ""
        val start = index + prefix.length
        
        var end = start
        while (end < wifiString.length) {
            val char = wifiString[end]
            // Break at backslash followed by semicolon, or standalone semicolon
            if (char == ';') {
                // If it's escaped \; continue unless it's the end representation
                if (end > start && wifiString[end - 1] == '\\') {
                    end++
                    continue
                }
                break
            }
            end++
        }
        return wifiString.substring(start, end).replace("\\;", ";").replace("\\:", ":")
    }

    private fun extractPhoneFromWhatsApp(waUrl: String): String {
        try {
            val uri = Uri.parse(waUrl)
            val path = uri.path?.replace("/", "") ?: ""
            if (path.isNotEmpty() && path.all { it.isDigit() }) {
                return path
            }
            val phoneParam = uri.getQueryParameter("phone")
            if (!phoneParam.isNullOrEmpty()) {
                return phoneParam
            }
        } catch (_: Exception) {}
        return ""
    }
}
