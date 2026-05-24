package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.NeonPurple
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun AnniversaryBanner(modifier: Modifier = Modifier) {
    val today = remember { LocalDate.now() }
    val isAnniversaryDay = today.dayOfMonth == 10

    // Calculations of months counts
    val startDate = LocalDate.of(2023, 11, 10)
    val monthsTogether = ChronoUnit.MONTHS.between(startDate, today)

    // Calculate days remaining
    val daysRemaining = remember {
        val currentDay = today.dayOfMonth
        if (currentDay == 10) {
            0
        } else if (currentDay < 10) {
            10 - currentDay
        } else {
            // Next month's 10th
            val nextMonth10 = today.withDayOfMonth(10).plusMonths(1)
            ChronoUnit.DAYS.between(today, nextMonth10)
        }
    }

    // Glow pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val bannerScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(if (isAnniversaryDay) bannerScale else 1.0f)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (isAnniversaryDay) {
                        listOf(GlowingPink, NeonPurple)
                    } else {
                        listOf(
                            NeonPurple.copy(alpha = 0.15f),
                            GlowingPink.copy(alpha = 0.15f)
                        )
                    }
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isAnniversaryDay) {
                Text(
                    text = "Hoy cumplimos otro mes juntos ❤️",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¡Felices ${monthsTogether} meses de puro amor! 🥰",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "📅 Próximo cumplemes: ",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = "el 10 del siguiente mes",
                        color = GlowingPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Faltan $daysRemaining ${if (daysRemaining == 1L) "día" else "días"} para celebrar nuestra fecha especial ✨",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
