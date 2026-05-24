package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class DetailedTimeElapsed(
    val years: Long,
    val months: Long,
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long
)

@Composable
fun TimeCounter(modifier: Modifier = Modifier) {
    var timeElapsed by remember { mutableStateOf(calculateTimeElapsed()) }

    // Coroutine running every second to update the UI instantly
    LaunchedEffect(Unit) {
        while (true) {
            timeElapsed = calculateTimeElapsed()
            delay(1000)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⏳ TIEMPO TRANSCURRIDO",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 2 columns, 3 rows layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CounterCell(
                value = timeElapsed.years,
                label = "AÑOS",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(NeonPurple, GlowingPink))
            )
            CounterCell(
                value = timeElapsed.months,
                label = "MESES",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(GlowingPink, NeonPurple))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CounterCell(
                value = timeElapsed.days,
                label = "DÍAS",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(GlowingPink, NeonPurple))
            )
            CounterCell(
                value = timeElapsed.hours,
                label = "HORAS",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(NeonPurple, GlowingPink))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CounterCell(
                value = timeElapsed.minutes,
                label = "MINUTOS",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(NeonPurple, GlowingPink))
            )
            CounterCell(
                value = timeElapsed.seconds,
                label = "SEGUNDOS",
                modifier = Modifier.weight(1f),
                glowBrush = Brush.verticalGradient(listOf(GlowingPink, NeonPurple)),
                isHighlighted = true
            )
        }
    }
}

@Composable
fun CounterCell(
    value: Long,
    label: String,
    modifier: Modifier = Modifier,
    glowBrush: Brush,
    isHighlighted: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassWhite)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = if (isHighlighted) {
                        listOf(GlowingPink, GlowingPink.copy(alpha = 0.2f))
                    } else {
                        listOf(GlassBorder, GlassBorder.copy(alpha = 0.1f))
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%02d", value),
                color = if (isHighlighted) GlowingPink else Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Precise implementation of LocalDateTime comparison
private fun calculateTimeElapsed(): DetailedTimeElapsed {
    val startDate = LocalDateTime.of(2023, 11, 10, 0, 0, 0)
    val now = LocalDateTime.now()

    if (now.isBefore(startDate)) {
        return DetailedTimeElapsed(0, 0, 0, 0, 0, 0)
    }

    var tempDateTime = LocalDateTime.from(startDate)

    val years = tempDateTime.until(now, ChronoUnit.YEARS)
    tempDateTime = tempDateTime.plusYears(years)

    val months = tempDateTime.until(now, ChronoUnit.MONTHS)
    tempDateTime = tempDateTime.plusMonths(months)

    val days = tempDateTime.until(now, ChronoUnit.DAYS)
    tempDateTime = tempDateTime.plusDays(days)

    val hours = tempDateTime.until(now, ChronoUnit.HOURS)
    tempDateTime = tempDateTime.plusHours(hours)

    val minutes = tempDateTime.until(now, ChronoUnit.MINUTES)
    tempDateTime = tempDateTime.plusMinutes(minutes)

    val seconds = tempDateTime.until(now, ChronoUnit.SECONDS)

    return DetailedTimeElapsed(years, months, days, hours, minutes, seconds)
}
