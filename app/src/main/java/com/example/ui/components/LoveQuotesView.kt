package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlowingPink

@Composable
fun LoveQuotesView(
    modifier: Modifier = Modifier,
    onQuoteTap: (Float, Float) -> Unit
) {
    val quotes = remember {
        listOf(
            "Encontré mi hogar en ti, y de ahí no quiero salir jamás. ❤️",
            "Desde aquel 10 de noviembre, mi corazón late al ritmo de tu risa. 💫",
            "Amarte es respirar aire puro en medio de la tormenta. 🌟",
            "Tú y yo: la combinación perfecta, el código que nunca falla. 💻💖",
            "Cada segundo contigo es un regalo del universo. ⏳",
            "Eres mi presente favorito y mi destino más hermoso. 🗺️",
            "Ni mil años serían suficientes para demostrarte cuánto te amo. ♾️",
            "Tu amor es la melodía que me hace sonreír cada mañana. 🎵",
            "La felicidad se escribe con las letras de tu nombre. ✍️",
            "Navegar la vida junto a ti es mi aventura preferida. ⛵🌌"
        )
    }

    var currentIndex by remember { mutableStateOf(0) }

    GlassmorphicCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                currentIndex = (currentIndex + 1) % quotes.size
                // Trigger romantic hearts explosion near center of card
                onQuoteTap(0.5f, 0.45f)
            },
        borderColor = GlowingPink.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💌 FRASE DE AMOR ANIMADA",
                color = GlowingPink,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Animated slide-in-fade content transition
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut())
                        .using(SizeTransform(clip = false))
                },
                label = "quoteTransition"
            ) { targetIndex ->
                Text(
                    text = quotes[targetIndex],
                    color = Color.White,
                    fontSize = 17.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "(Toca para cambiar de frase y liberar amor ✨)",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
