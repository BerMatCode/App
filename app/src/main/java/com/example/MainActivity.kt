package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.ScanResult
import com.example.ui.components.CameraScannerView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.QRParser
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val scannerViewModel: ScannerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark = isSystemInDarkTheme()
            
            // Centralized styling colours depending on light modes
            val mainBgColor = if (isDark) Color(0xFF07040C) else Color(0xFFF6F3F9)
            val headerColor = if (isDark) Color(0xFFBA00FF) else Color(0xFF8E24AA)
            val secondaryColor = if (isDark) Color(0xFFFF007F) else Color(0xFFD81B60)
            val textColor = if (isDark) Color.White else Color(0xFF1E1724)
            val subextColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)

            MyApplicationTheme(
                darkTheme = isDark,
                dynamicColor = false
            ) {
                val context = LocalContext.current
                val systemInsets = WindowInsets.safeDrawing.asPaddingValues()

                val scans by scannerViewModel.allScans.collectAsState()

                // State controls
                var searchQuery by remember { mutableStateOf("") }
                var selectedScanDetail by remember { mutableStateOf<ScanResult?>(null) }
                var fileErrorMessage by remember { mutableStateOf<String?>(null) }

                // Picker for local QR code image processing from Gallery
                val galleryPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    if (uri != null) {
                        try {
                            val inputImg = InputImage.fromFilePath(context, uri)
                            val scanner = BarcodeScanning.getClient()
                            
                            scanner.process(inputImg)
                                .addOnSuccessListener { barcodes ->
                                    if (barcodes.isNotEmpty()) {
                                        val rawText = barcodes[0].rawValue
                                        if (!rawText.isNullOrBlank()) {
                                            scannerViewModel.addScan(rawText)
                                            Toast.makeText(context, "Código QR reconocido con éxito! 🎉", Toast.LENGTH_SHORT).show()
                                        } else {
                                            fileErrorMessage = "No se pudo extraer el texto de este código QR."
                                        }
                                    } else {
                                        fileErrorMessage = "No se detectó ningún código QR en la imagen seleccionada."
                                    }
                                }
                                .addOnFailureListener {
                                    fileErrorMessage = "Error al procesar la imagen seleccionada."
                                }
                        } catch (e: IOException) {
                            fileErrorMessage = "Error al abrir el archivo de imagen."
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(mainBgColor)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = systemInsets.calculateTopPadding(), bottom = systemInsets.calculateBottomPadding())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Header segment
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            HeaderScannerBlock(headerColor, secondaryColor, isDark)
                        }

                        // 2. Realtime camera finder widget
                        item {
                            CameraScannerView(
                                onQrDetected = { qrCode ->
                                    scannerViewModel.addScan(qrCode)
                                    // Trigger detail modal on fresh scan
                                    val recent = scans.firstOrNull { it.rawText == qrCode }
                                    if (recent != null) {
                                        selectedScanDetail = recent
                                    } else {
                                        // Fake representation for instant detail modal response if Room didn't update yet
                                        val parsed = QRParser.parse(qrCode)
                                        selectedScanDetail = ScanResult(rawText = qrCode, title = parsed.title, formatType = parsed.type)
                                    }
                                },
                                onOpenGallery = {
                                    galleryPicker.launch("image/*")
                                }
                            )
                        }

                        // 3. Search and Action segment
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("Buscar en el historial") },
                                    placeholder = { Text("Palabra clave, URL, etc...") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar")
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = headerColor,
                                        unfocusedBorderColor = headerColor.copy(alpha = 0.3f),
                                        focusedLabelColor = headerColor,
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📋 HISTORIAL DE ESCANEOS",
                                        color = subextColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp
                                    )

                                    if (scans.isNotEmpty()) {
                                        TextButton(
                                            onClick = { scannerViewModel.clearHistory() },
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.8f))
                                        ) {
                                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Limpiar historial", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Borrar todo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Scans List history block
                        val filteredList = scans.filter {
                            it.rawText.contains(searchQuery, ignoreCase = true) ||
                            it.title.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = "No hay escaneos",
                                            tint = subextColor,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (searchQuery.isEmpty()) "No hay elementos en el historial" else "No se encontraron coincidencias",
                                            color = subextColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredList) { scan ->
                                ScanHistoryRow(
                                    scan = scan,
                                    isDark = isDark,
                                    textColor = textColor,
                                    subtextCol = subextColor,
                                    onItemClick = {
                                        selectedScanDetail = scan
                                    },
                                    onDeleteClick = {
                                        scannerViewModel.deleteScan(scan.id)
                                    }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Scan results details interactive dialog
                selectedScanDetail?.let { scan ->
                    ScanDetailDialog(
                        scan = scan,
                        onDismiss = { selectedScanDetail = null },
                        context = context,
                        headerColor = headerColor
                    )
                }

                // Gallery error dialogue
                fileErrorMessage?.let { msg ->
                    AlertDialog(
                        onDismissRequest = { fileErrorMessage = null },
                        title = { Text("Aviso de Escaneo", fontWeight = FontWeight.Bold) },
                        text = { Text(msg) },
                        confirmButton = {
                            TextButton(onClick = { fileErrorMessage = null }) {
                                Text("Aceptar", fontWeight = FontWeight.Bold, color = headerColor)
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderScannerBlock(primaryColor: Color, secondaryColor: Color, isDark: Boolean) {
    val subtitleTagLine = "ESCÁNER NATIVO PREMIUM CON INTELIGENCIA COGNITIVA"
    
    val tagBg = if (isDark) Color(0x3BBA00FF) else Color(0x1A8E24AA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.launcher_logo),
                contentDescription = "BerMatScanner Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "BerMatScanner",
                color = if (isDark) Color.White else Color(0xFF130F1A),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(tagBg)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = subtitleTagLine,
                color = if (isDark) secondaryColor else primaryColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ScanHistoryRow(
    scan: ScanResult,
    isDark: Boolean,
    textColor: Color,
    subtextCol: Color,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val parsed = remember(scan.rawText) { QRParser.parse(scan.rawText) }

    val formattedDate = remember(scan.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        sdf.format(Date(scan.timestamp))
    }

    // Modern styled row
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large styled emoji preview box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color(0x24FFFFFF) else Color(0x3BBA00FF)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = parsed.iconEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scan.title,
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = scan.rawText,
                        color = subtextCol,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📅 $formattedDate",
                        color = subtextCol.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Borrar",
                    tint = subtextCol.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ScanDetailDialog(
    scan: ScanResult,
    onDismiss: () -> Unit,
    context: Context,
    headerColor: Color
) {
    val parsed = remember(scan.rawText) { QRParser.parse(scan.rawText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    executeParsedAction(parsed, scan.rawText, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = headerColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(parsed.actionLabel, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.5f))
            ) {
                Text("Cerrar")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = parsed.iconEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = scan.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Extracted detailed information
                Column {
                    Text(
                        text = "INFORMACIÓN RECONOCIDA",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = parsed.displayDetails,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Raw information
                Column {
                    Text(
                        text = "CONTENIDO ORIGINAL DE QR",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = scan.rawText,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Floating actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy action button
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("BerMatScanner QR", scan.rawText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Texto copiado al portapapeles! 📋", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Share action button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, scan.rawText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir con..."))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compartir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFF140D22),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
    )
}

/**
 * Handles actions appropriately based on the detected format
 */
private fun executeParsedAction(parsed: com.example.utils.ParsedQR, rawText: String, context: Context) {
    try {
        when (parsed.type) {
            "URL" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsed.displayDetails))
                context.startActivity(intent)
            }
            "PHONE" -> {
                val cleanedNumber = rawText.replace("tel:", "").trim()
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanedNumber"))
                context.startActivity(intent)
            }
            "EMAIL" -> {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(if (rawText.startsWith("mailto:")) rawText else "mailto:$rawText"))
                context.startActivity(intent)
            }
            "WHATSAPP" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawText))
                context.startActivity(intent)
            }
            "WIFI" -> {
                // Since modifying WiFi configuration system structures can require system privileges on newer Android frameworks,
                // we copy the password to user and notify them for seamless integration.
                val password = parsed.displayDetails.substringAfter("Contraseña: ", "")
                if (password.isNotEmpty()) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Wifi Password", password)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Contraseña copiada! Conéctate pegándola en la configuración Wifi 📶", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Esta red no parece requerir contraseña.", Toast.LENGTH_SHORT).show()
                }
            }
            "YAPE" -> {
                // If it's a URL we open it, otherwise query online / notify user nicely
                if (rawText.startsWith("http://") || rawText.startsWith("https://")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawText))
                    context.startActivity(intent)
                } else {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Yape details", rawText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Datos de Pago copiados! Ábrelos en tu app banca o Yape. 💸", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                // Simply copy to clipboard by standard behavior
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Parsed QR", rawText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Información copiada al portapapeles! 📋", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir ninguna aplicación para esta acción.", Toast.LENGTH_SHORT).show()
    }
}
