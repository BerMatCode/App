package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.NeonPurple

data class RomanticVoucher(
    val id: Int,
    val titleFront: String,
    val subtitleFront: String,
    val descriptionBack: String,
    val noteBack: String,
    val badge: String
)

@Composable
fun InteractiveCardsView(
    modifier: Modifier = Modifier,
    onCardFlip: (Float, Float) -> Unit
) {
    val vouchers = remember {
        listOf(
            RomanticVoucher(
                id = 1,
                titleFront = "Cupón de Abrazo\nInfinito 🤗",
                subtitleFront = "Vale de Amor Mat & Ber",
                descriptionBack = "Válido para un abrazo súper cálido que reconstruya cualquier día difícil.",
                noteBack = "Código: ABRAZO-B&M",
                badge = "VALOR: INFINITO"
            ),
            RomanticVoucher(
                id = 2,
                titleFront = "Vale por Cita\nSorpresa 🍿",
                subtitleFront = "Planeación Romántica",
                descriptionBack = "Un plan misterioso y divertido organizado al 100% solo para ti de sorpresa.",
                noteBack = "Código: SPREE-B&M",
                badge = "VALOR: ULTRA"
            ),
            RomanticVoucher(
                id = 3,
                titleFront = "Cupón de Besos\ny Café ☕",
                subtitleFront = "Relajamiento Juntos",
                descriptionBack = "Una tarde completa de café aromático, risas libres y mimos interminables.",
                noteBack = "Código: CAFE-B&M",
                badge = "VALOR: DULCE"
            ),
            RomanticVoucher(
                id = 4,
                titleFront = "Cupón de\nReconciliación 🕯️",
                subtitleFront = "Calma Instantánea",
                descriptionBack = "Disculpa inmediata acompañada de tu comida preferida. Uso exclusivo de emergencia.",
                noteBack = "Código: SOLVE-B&M",
                badge = "SOCORRO 24/7"
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "🎟️ TARJETAS Y VALES INTERACTIVOS",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 12.dp)
        ) {
            items(vouchers) { voucher ->
                FlipVoucherCard(
                    voucher = voucher,
                    onFlipTriggered = onCardFlip
                )
            }
        }
    }
}

@Composable
fun FlipVoucherCard(
    voucher: RomanticVoucher,
    onFlipTriggered: (Float, Float) -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    // Rotate angle animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .width(170.dp)
            .height(210.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isFlipped) {
                        listOf(Color(0xFF1E1128), Color(0xFF0F0712))
                    } else {
                        listOf(GlassWhite, Color(0x06FFFFFF))
                    }
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = if (isFlipped) {
                        listOf(GlowingPink.copy(alpha = 0.7f), NeonPurple.copy(alpha = 0.3f))
                    } else {
                        listOf(GlassBorder, GlassBorder.copy(alpha = 0.1f))
                    }
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isFlipped = !isFlipped
                onFlipTriggered(0.5f, 0.6f)
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Front Card Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = voucher.badge,
                        color = NeonPurple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = voucher.titleFront,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Text(
                    text = voucher.subtitleFront,
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Back Card Content (must flip horizontally so text is not reversed)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CUPÓN DE REGALO",
                    color = GlowingPink,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = voucher.descriptionBack,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )

                Text(
                    text = voucher.noteBack,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
