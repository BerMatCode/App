package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.RomanticLetter
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.NeonPurple

@Composable
fun RomanticLettersList(
    letters: List<RomanticLetter>,
    onAddLetter: (String, String, String) -> Unit,
    onDeleteLetter: (Int) -> Unit,
    onLetterSelected: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLetter by remember { mutableStateOf<RomanticLetter?>(null) }
    var showWriteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📖 NUESTRAS CARTAS ROMÁNTICAS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            // Compose letter button
            Button(
                onClick = { showWriteDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple.copy(alpha = 0.2f),
                    contentColor = NeonPurple
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Escribir",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Escribir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (letters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay cartas... escribe la primera 💕",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            // Displays a collection of beautiful rows
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                letters.forEach { letter ->
                    LetterCompactRow(
                        letter = letter,
                        onOpen = {
                            selectedLetter = letter
                            onLetterSelected(0.5f, 0.7f)
                        },
                        onDelete = {
                            onDeleteLetter(letter.id)
                        }
                    )
                }
            }
        }
    }

    // Dialog for Reading a Letter
    selectedLetter?.let { letter ->
        AlertDialog(
            onDismissRequest = { selectedLetter = null },
            confirmButton = {
                TextButton(
                    onClick = { selectedLetter = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = GlowingPink)
                ) {
                    Text("Cerrar con Amor ❤️", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Sobre",
                        tint = GlowingPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = letter.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Para: Ber y Mat 🌟",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = letter.content,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "— Firma: ${letter.author}",
                            color = NeonPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            },
            containerColor = Color(0xFF140D1F),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
        )
    }

    // Dialog for Writing a Letter
    if (showWriteDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("Mat & Ber") }
        var isAuthorMenuExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showWriteDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onAddLetter(title.trim(), content.trim(), author)
                            showWriteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlowingPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Inspirar y Guardar 💖", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWriteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.5f))
                ) {
                    Text("Cancelar")
                }
            },
            title = {
                Text(
                    text = "✍️ REDACTAR NUEVA CARTA",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la carta") },
                        placeholder = { Text("Ej: Mis mejores recuerdos... 🌟") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = NeonPurple,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Mensaje de amor") },
                        placeholder = { Text("Escribe con sinceridad y ternura lo que sientes...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = NeonPurple,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 8
                    )

                    // Compact selection block for Author
                    Column {
                        Text(
                            text = "Firma del Autor:",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Mat", "Ber", "Mat & Ber").forEach { signature ->
                                val isSelected = author == signature
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonPurple else GlassWhite)
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonPurple else GlassBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { author = signature }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = signature,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF140D1F),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
        )
    }
}

@Composable
fun LetterCompactRow(
    letter: RomanticLetter,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onOpen() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Carta",
                    tint = NeonPurple.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = letter.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = letter.content,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // If it is preset, show a pretty badge, otherwise let them delete it
                if (letter.isPreset) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Preset",
                            color = NeonPurple,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
