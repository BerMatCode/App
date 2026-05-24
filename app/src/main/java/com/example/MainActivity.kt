package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.CustomBlack
import com.example.ui.theme.DarkPurpleBg
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonPurple

class MainActivity : ComponentActivity() {
    private val loveViewModel: LoveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(
                darkTheme = true, // We force our gorgeous cyberpunk neon dark theme as standard
                dynamicColor = false
            ) {
                // Background & Layout setup handling system safe drawing automatically
                val systemInsets = WindowInsets.safeDrawing.asPaddingValues()

                var heartsTrigger by remember { mutableStateOf(0) }
                var heartSpawnX by remember { mutableStateOf(0.5f) }
                var heartSpawnY by remember { mutableStateOf(0.8f) }

                val letters by loveViewModel.allLetters.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    DarkPurpleBg,
                                    CustomBlack
                                )
                            )
                        )
                ) {
                    // Ambient flying hearts drawing in background
                    HeartParticleSystem(
                        modifier = Modifier.fillMaxSize(),
                        spawnTrigger = heartsTrigger,
                        spawnX = heartSpawnX,
                        spawnY = heartSpawnY
                    )

                    // Unified Scrollable Dashboard
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = systemInsets.calculateTopPadding())
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Cyber Head Block
                        HeaderLogoView()

                        // Anniversary Message
                        AnniversaryBanner()

                        // Realtime detailed counter
                        TimeCounter()

                        // Rotating animated quotes
                        LoveQuotesView(
                            onQuoteTap = { x, y ->
                                heartSpawnX = x
                                heartSpawnY = y
                                heartsTrigger++
                            }
                        )

                        // Voucher/gifts cards row
                        InteractiveCardsView(
                            onCardFlip = { x, y ->
                                heartSpawnX = x
                                heartSpawnY = y
                                heartsTrigger++
                            }
                        )

                        // Diary notebook backed by Room Database
                        RomanticLettersList(
                            letters = letters,
                            onAddLetter = { title, content, author ->
                                loveViewModel.addLetter(title, content, author)
                                heartsTrigger++
                                heartSpawnX = 0.5f
                                heartSpawnY = 0.8f
                            },
                            onDeleteLetter = { id ->
                                loveViewModel.deleteLetter(id)
                            },
                            onLetterSelected = { x, y ->
                                heartSpawnX = x
                                heartSpawnY = y
                                heartsTrigger++
                            }
                        )

                        // Safe padding at bottom to avoid overlaps with screen handle
                        Spacer(modifier = Modifier.height(systemInsets.calculateBottomPadding() + 20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderLogoView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App logo displayed elegantly
        Image(
            painter = painterResource(id = R.drawable.launcher_logo),
            contentDescription = "BerMatCode Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = "BerMatCode",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp
            )
            Text(
                text = "CÓDIGO DE AMOR ETERNO • DESDE 10/11/2023",
                color = GlowingPink,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
    }
}
