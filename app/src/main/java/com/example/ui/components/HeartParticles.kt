package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.GlowingPink
import com.example.ui.theme.NeonPurple
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

data class HeartParticle(
    val id: Long,
    var x: Float,          // fraction (0..1)
    var y: Float,          // fraction (0..1)
    var size: Float,       // dp size
    var speed: Float,      // dy change per frame
    var driftSpeed: Float, // dx wave multiplier
    var angle: Float,      // wave offset Angle
    var alpha: Float,
    val color: Color
)

@Composable
fun HeartParticleSystem(
    modifier: Modifier = Modifier,
    spawnTrigger: Int = 0,
    spawnX: Float = 0.5f,
    spawnY: Float = 0.8f
) {
    val particles = remember { mutableStateListOf<HeartParticle>() }
    var particleIdByCounter by remember { mutableStateOf(0L) }

    // Tick updates
    LaunchedEffect(Unit) {
        while (true) {
            // Update existings
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.y -= p.speed
                p.angle += p.driftSpeed
                p.x += sin(p.angle) * 0.003f
                p.alpha -= 0.004f

                if (p.y < -0.1f || p.alpha <= 0f || p.x < -0.1f || p.x > 1.1f) {
                    iterator.remove()
                }
            }

            // Spawn ambient background hearts occasionally
            if (particles.size < 25 && Random.nextFloat() < 0.15f) {
                particles.add(
                    HeartParticle(
                        id = particleIdByCounter++,
                        x = Random.nextFloat(),
                        y = 1.05f,
                        size = Random.nextFloat() * 14f + 8f,
                        speed = Random.nextFloat() * 0.003f + 0.001f,
                        driftSpeed = Random.nextFloat() * 0.05f + 0.01f,
                        angle = Random.nextFloat() * 6.28f,
                        alpha = Random.nextFloat() * 0.6f + 0.3f,
                        color = if (Random.nextBoolean()) GlowingPink else NeonPurple
                    )
                )
            }

            delay(16) // ~60fps smooth loop
        }
    }

    // Spawn a burst when spawnTrigger changes
    LaunchedEffect(spawnTrigger) {
        if (spawnTrigger > 0) {
            repeat(10) {
                particles.add(
                    HeartParticle(
                        id = particleIdByCounter++,
                        x = (spawnX + (Random.nextFloat() - 0.5f) * 0.2f).coerceIn(0f, 1f),
                        y = spawnY + (Random.nextFloat() - 0.5f) * 0.1f,
                        size = Random.nextFloat() * 20f + 10f,
                        speed = Random.nextFloat() * 0.008f + 0.004f,
                        driftSpeed = Random.nextFloat() * 0.1f + 0.02f,
                        angle = Random.nextFloat() * 6.28f,
                        alpha = 1.0f,
                        color = if (Random.nextBoolean()) GlowingPink else NeonPurple
                    )
                )
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val px = p.x * size.width
            val py = p.y * size.height
            val radius = p.size

            // Draw a beautiful romantic vector heart
            val path = Path().apply {
                val startX = px
                val startY = py + radius * 0.35f
                
                moveTo(startX, startY)
                
                // Left hump
                cubicTo(
                    px - radius * 0.9f, py - radius * 0.5f,
                    px - radius * 0.9f, py + radius * 0.6f,
                    px, py + radius * 1.1f
                )
                
                // Right hump
                cubicTo(
                    px + radius * 0.9f, py + radius * 0.6f,
                    px + radius * 0.9f, py - radius * 0.5f,
                    startX, startY
                )
            }

            // Fill
            drawPath(
                path = path,
                color = p.color.copy(alpha = p.alpha)
            )

            // Outline for a shiny glow
            drawPath(
                path = path,
                color = Color.White.copy(alpha = p.alpha * 0.3f),
                style = Stroke(width = 1.5f)
            )
        }
    }
}
