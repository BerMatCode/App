package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    // Dynamic background blend colors
    val bgGradient = if (isDark) {
        listOf(
            Color(0x1BFFFFFF), // translucent white highlight
            Color(0x06FFFFFF)
        )
    } else {
        listOf(
            Color(0xBFFFFFFF), // opaque milk tint for great reading
            Color(0x80FFFFFF)
        )
    }

    val borderGradient = if (isDark) {
        listOf(
            Color(0x2BFFFFFF),
            Color(0x0AFFFFFF)
        )
    } else {
        listOf(
            Color(0x52000000),
            Color(0x14000000)
        )
    }

    val shadowColor = if (isDark) {
        Color(0x12BA00FF) // neon purple soft glow
    } else {
        Color(0x12000000)
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = shadowColor,
                spotColor = shadowColor,
                clip = false
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush = Brush.verticalGradient(bgGradient))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(borderGradient),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
