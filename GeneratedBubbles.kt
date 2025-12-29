package com.appsdevs.popit

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*

// ==================== GENERATED BUBBLES (CODE-BASED) ====================
// All bubbles use uniform sizing: radius = min(size.width, size.height) / 2.5f
// All bubbles are centered at: size.width / 2, size.height / 2

// ==================== BUBBLE 11: FIRE BUBBLE ====================
@Composable
fun FireBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")
    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val particleRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rise"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base dark core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A0000),
                    Color(0xFF8B0000)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Flame layers with flicker
        for (layer in 0..3) {
            val layerRadius = radius * (0.9f - layer * 0.15f)
            val flickerOffset = flameFlicker * 0.05f * layerRadius
            val layerColor = when (layer) {
                0 -> Color(0xFFFF4500)
                1 -> Color(0xFFFF6347)
                2 -> Color(0xFFFF8C00)
                else -> Color(0xFFFFA500)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        layerColor.copy(alpha = 0.8f),
                        layerColor.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY - flickerOffset),
                    radius = layerRadius
                ),
                radius = layerRadius,
                center = Offset(centerX, centerY - flickerOffset)
            )
        }

        // Rising flame particles
        for (i in 0 until 20) {
            val angle = (i * 18f + rotation) * PI.toFloat() / 180f
            val particleProgress = (particleRise + i * 0.05f) % 1f
            val particleX = centerX + cos(angle) * radius * 0.3f
            val particleY = centerY - particleProgress * radius * 1.2f
            val particleAlpha = (1f - particleProgress) * 0.8f

            drawCircle(
                color = Color(0xFFFFA500).copy(alpha = particleAlpha),
                radius = radius * 0.05f * (1f - particleProgress),
                center = Offset(particleX, particleY)
            )
        }

        // Heat distortion effect (outer glow)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFFF0000).copy(alpha = 0.2f * flameFlicker),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 1.3f
            ),
            radius = radius * 1.3f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 12: ICE BUBBLE ====================
@Composable
fun IceBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice")
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )
    val crystalRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "crystalRotate"
    )
    val frostPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "frostPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base ice gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE0FFFF),
                    Color(0xFF87CEEB),
                    Color(0xFF4682B4)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Frost layer
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.6f * frostPulse),
                    Color(0xFFB0E0E6).copy(alpha = 0.3f * frostPulse),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.9f
            ),
            radius = radius * 0.9f,
            center = Offset(centerX, centerY)
        )

        // Ice crystals pattern
        for (i in 0 until 6) {
            val angle = (i * 60f + crystalRotate) * PI.toFloat() / 180f
            val crystalLength = radius * 0.8f

            // Main crystal ray
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(angle) * crystalLength,
                    centerY + sin(angle) * crystalLength
                ),
                strokeWidth = 2f
            )

            // Side branches
            for (branch in 1..2) {
                val branchStart = 0.3f * branch
                val branchAngle = if (branch % 2 == 0) 30f else -30f
                val branchAngleRad = (angle + branchAngle * PI.toFloat() / 180f)
                val startX = centerX + cos(angle) * crystalLength * branchStart
                val startY = centerY + sin(angle) * crystalLength * branchStart

                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(startX, startY),
                    end = Offset(
                        startX + cos(branchAngleRad) * radius * 0.2f,
                        startY + sin(branchAngleRad) * radius * 0.2f
                    ),
                    strokeWidth = 1.5f
                )
            }
        }

        // Sparkles
        for (i in 0 until 12) {
            val angle = (i * 30f) * PI.toFloat() / 180f
            val sparkleDistance = radius * (0.6f + (i % 3) * 0.1f)
            val sparkleAlpha = if (i % 3 == 0) sparkle else 1f - sparkle

            drawCircle(
                color = Color.White.copy(alpha = sparkleAlpha * 0.9f),
                radius = radius * 0.04f,
                center = Offset(
                    centerX + cos(angle) * sparkleDistance,
                    centerY + sin(angle) * sparkleDistance
                )
            )
        }
    }
}

// ==================== BUBBLE 13: ELECTRIC BUBBLE ====================
@Composable
fun ElectricBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "electric")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val boltPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "boltPhase"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base electric core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1E90FF),
                    Color(0xFF0047AB),
                    Color(0xFF000080)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Energy pulse rings
        for (ring in 0..2) {
            val ringRadius = radius * (0.7f - ring * 0.2f) * (1f + pulse * 0.1f)
            drawCircle(
                color = Color(0xFF00FFFF).copy(alpha = 0.6f * (1f - pulse)),
                radius = ringRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }

        // Electric arcs/bolts
        for (i in 0 until 8) {
            if ((i + (boltPhase * 8).toInt()) % 3 == 0) {
                val angle = (i * 45f + rotation) * PI.toFloat() / 180f
                val boltLength = radius * 0.9f
                val segments = 5

                val path = Path()
                path.moveTo(centerX, centerY)

                var currentX = centerX
                var currentY = centerY

                for (seg in 0 until segments) {
                    val progress = (seg + 1f) / segments
                    val targetX = centerX + cos(angle) * boltLength * progress
                    val targetY = centerY + sin(angle) * boltLength * progress

                    // Add zigzag
                    val zigzag = (if (seg % 2 == 0) 1f else -1f) * radius * 0.1f
                    val perpAngle = angle + PI.toFloat() / 2f
                    val zigzagX = (currentX + targetX) / 2 + cos(perpAngle) * zigzag
                    val zigzagY = (currentY + targetY) / 2 + sin(perpAngle) * zigzag

                    path.lineTo(zigzagX, zigzagY)
                    path.lineTo(targetX, targetY)

                    currentX = targetX
                    currentY = targetY
                }

                drawPath(
                    path = path,
                    color = Color(0xFFFFFF00).copy(alpha = 0.9f),
                    style = Stroke(width = 3f)
                )

                // Bolt glow
                drawPath(
                    path = path,
                    color = Color(0xFF00FFFF).copy(alpha = 0.4f),
                    style = Stroke(width = 6f)
                )
            }
        }

        // Sparks
        for (i in 0 until 15) {
            val sparkAngle = (i * 24f + rotation * 2f + boltPhase * 360f) * PI.toFloat() / 180f
            val sparkDistance = radius * (0.8f + sin(boltPhase * PI.toFloat() * 2f + i) * 0.2f)

            drawCircle(
                color = Color(0xFFFFFF00).copy(alpha = 0.8f),
                radius = radius * 0.03f,
                center = Offset(
                    centerX + cos(sparkAngle) * sparkDistance,
                    centerY + sin(sparkAngle) * sparkDistance
                )
            )
        }

        // Core glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.8f * pulse),
                    Color(0xFF00FFFF).copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 14: NATURE BUBBLE ====================
@Composable
fun NatureBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "nature")
    val leafFall by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leafFall"
    )
    val vineGrow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vineGrow"
    )
    val particleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleFloat"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base nature gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF90EE90),
                    Color(0xFF228B22),
                    Color(0xFF006400)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Growth rings
        for (ring in 0..3) {
            drawCircle(
                color = Color(0xFF00FF00).copy(alpha = 0.2f * vineGrow),
                radius = radius * (0.3f + ring * 0.2f) * vineGrow,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
        }

        // Vine patterns
        for (vine in 0 until 4) {
            val vineAngle = (vine * 90f) * PI.toFloat() / 180f
            val path = Path()
            path.moveTo(centerX, centerY)

            for (seg in 0..8) {
                val progress = seg / 8f * vineGrow
                val distance = radius * progress
                val wave = sin(progress * PI.toFloat() * 3f) * radius * 0.15f
                val perpAngle = vineAngle + PI.toFloat() / 2f

                val x = centerX + cos(vineAngle) * distance + cos(perpAngle) * wave
                val y = centerY + sin(vineAngle) * distance + sin(perpAngle) * wave

                if (seg == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = Color(0xFF228B22).copy(alpha = 0.7f),
                style = Stroke(width = 3f)
            )
        }

        // Falling leaves
        for (i in 0 until 12) {
            val leafAngle = (i * 30f) * PI.toFloat() / 180f
            val fallProgress = (leafFall + i * 0.08f) % 1f
            val leafX = centerX + cos(leafAngle + sin(fallProgress * PI.toFloat()) * 0.5f) * radius * 0.7f
            val leafY = centerY - radius * 0.8f + fallProgress * radius * 1.8f
            val leafAlpha = sin(fallProgress * PI.toFloat()) * 0.8f

            // Leaf shape (simple oval)
            drawOval(
                color = Color(0xFF32CD32).copy(alpha = leafAlpha),
                topLeft = Offset(leafX - radius * 0.06f, leafY - radius * 0.08f),
                size = androidx.compose.ui.geometry.Size(radius * 0.12f, radius * 0.16f)
            )
        }

        // Green particle effects
        for (i in 0 until 20) {
            val angle = (i * 18f) * PI.toFloat() / 180f
            val floatDistance = radius * (0.5f + sin(particleFloat + i) * 0.3f)
            val particleAlpha = 0.3f + sin(particleFloat + i) * 0.3f

            drawCircle(
                color = Color(0xFF7FFF00).copy(alpha = particleAlpha),
                radius = radius * 0.03f,
                center = Offset(
                    centerX + cos(angle) * floatDistance,
                    centerY + sin(angle) * floatDistance
                )
            )
        }
    }
}

// ==================== BUBBLE 15: GALAXY BUBBLE ====================
@Composable
fun GalaxyBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy")
    val spiralRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spiralRotation"
    )
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starTwinkle"
    )
    val nebulaFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebulaFlow"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base space background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A0033),
                    Color(0xFF0D001A),
                    Color(0xFF000000)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Nebula clouds
        for (nebula in 0..2) {
            val nebulaAngle = (nebula * 120f + nebulaFlow * 60f) * PI.toFloat() / 180f
            val nebulaDistance = radius * 0.4f
            val nebulaSize = radius * 0.5f
            val nebulaColors = listOf(
                Color(0xFFFF00FF),
                Color(0xFF00FFFF),
                Color(0xFFFF1493)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        nebulaColors[nebula].copy(alpha = 0.3f * nebulaFlow),
                        Color.Transparent
                    ),
                    center = Offset(
                        centerX + cos(nebulaAngle) * nebulaDistance,
                        centerY + sin(nebulaAngle) * nebulaDistance
                    ),
                    radius = nebulaSize
                ),
                radius = nebulaSize,
                center = Offset(
                    centerX + cos(nebulaAngle) * nebulaDistance,
                    centerY + sin(nebulaAngle) * nebulaDistance
                )
            )
        }

        // Spiral galaxy arms
        for (arm in 0 until 3) {
            val armAngleOffset = arm * 120f

            for (point in 0 until 30) {
                val spiralProgress = point / 30f
                val spiralAngle = (armAngleOffset + spiralRotation + spiralProgress * 360f) * PI.toFloat() / 180f
                val spiralDistance = radius * spiralProgress * 0.9f
                val starSize = radius * 0.02f * (1f - spiralProgress * 0.5f)
                val starAlpha = 0.6f + (1f - spiralProgress) * 0.4f

                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = starSize,
                    center = Offset(
                        centerX + cos(spiralAngle) * spiralDistance,
                        centerY + sin(spiralAngle) * spiralDistance
                    )
                )
            }
        }

        // Twinkling stars
        for (i in 0 until 50) {
            val angle = (i * 7.2f) * PI.toFloat() / 180f
            val distance = radius * ((i % 10) / 10f) * 0.95f
            val twinklePhase = (starTwinkle + i * 0.1f) % 1f
            val starAlpha = 0.4f + sin(twinklePhase * PI.toFloat() * 2f) * 0.6f
            val starColor = when (i % 5) {
                0 -> Color.White
                1 -> Color(0xFFADD8E6)
                2 -> Color(0xFFFFE4C4)
                3 -> Color(0xFFFFB6C1)
                else -> Color(0xFFF0F8FF)
            }

            drawCircle(
                color = starColor.copy(alpha = starAlpha),
                radius = radius * 0.015f,
                center = Offset(
                    centerX + cos(angle) * distance,
                    centerY + sin(angle) * distance
                )
            )
        }

        // Central bright core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color(0xFFFFD700).copy(alpha = 0.6f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.3f
            ),
            radius = radius * 0.3f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 16: LAVA BUBBLE ====================
@Composable
fun LavaBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "lava")
    val lavaFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lavaFlow"
    )
    val bubbleRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleRise"
    )
    val heatPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heatPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base lava core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF4500),
                    Color(0xFFFF6347),
                    Color(0xFF8B0000),
                    Color(0xFF4A0000)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Flowing lava streams
        for (stream in 0 until 8) {
            val streamAngle = (stream * 45f) * PI.toFloat() / 180f
            val flowPhase = (lavaFlow + stream * 0.125f) % 1f

            for (segment in 0 until 10) {
                val segmentProgress = segment / 10f
                val distance = radius * segmentProgress * 0.9f
                val wave = sin((flowPhase + segmentProgress) * PI.toFloat() * 4f) * radius * 0.1f
                val perpAngle = streamAngle + PI.toFloat() / 2f

                val x = centerX + cos(streamAngle) * distance + cos(perpAngle) * wave
                val y = centerY + sin(streamAngle) * distance + sin(perpAngle) * wave

                val segmentAlpha = (1f - segmentProgress) * 0.6f
                drawCircle(
                    color = Color(0xFFFFA500).copy(alpha = segmentAlpha),
                    radius = radius * 0.08f * (1f - segmentProgress * 0.5f),
                    center = Offset(x, y)
                )
            }
        }

        // Rising lava bubbles
        for (i in 0 until 15) {
            val bubbleAngle = (i * 24f) * PI.toFloat() / 180f
            val riseProgress = (bubbleRise + i * 0.067f) % 1f
            val bubbleDistance = radius * 0.7f * (1f - riseProgress * 0.5f)
            val bubbleX = centerX + cos(bubbleAngle) * bubbleDistance
            val bubbleY = centerY + sin(bubbleAngle) * bubbleDistance - riseProgress * radius * 0.5f
            val bubbleAlpha = (1f - riseProgress) * 0.8f
            val bubbleSize = radius * 0.1f * (1f - riseProgress * 0.7f)

            drawCircle(
                color = Color(0xFFFF8C00).copy(alpha = bubbleAlpha),
                radius = bubbleSize,
                center = Offset(bubbleX, bubbleY)
            )

            // Bubble highlight
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = bubbleAlpha * 0.5f),
                radius = bubbleSize * 0.4f,
                center = Offset(bubbleX - bubbleSize * 0.3f, bubbleY - bubbleSize * 0.3f)
            )
        }

        // Glowing rocks/crust
        for (rock in 0 until 12) {
            val rockAngle = (rock * 30f + lavaFlow * 30f) * PI.toFloat() / 180f
            val rockDistance = radius * 0.85f

            drawCircle(
                color = Color(0xFF8B0000).copy(alpha = 0.9f),
                radius = radius * 0.08f,
                center = Offset(
                    centerX + cos(rockAngle) * rockDistance,
                    centerY + sin(rockAngle) * rockDistance
                )
            )

            // Rock glow
            drawCircle(
                color = Color(0xFFFF4500).copy(alpha = 0.5f * heatPulse),
                radius = radius * 0.1f,
                center = Offset(
                    centerX + cos(rockAngle) * rockDistance,
                    centerY + sin(rockAngle) * rockDistance
                )
            )
        }

        // Central heat glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFF00).copy(alpha = 0.8f * heatPulse),
                    Color(0xFFFF4500).copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.5f
            ),
            radius = radius * 0.5f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 17: CRYSTAL BUBBLE ====================
@Composable
fun CrystalBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "crystal")
    val prismRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "prismRotation"
    )
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorShift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base crystal body
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE8F5F5),
                    Color(0xFFB8E6E6),
                    Color(0xFF8BCDCD),
                    Color(0xFF5FB4B4)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Crystal facets
        for (facet in 0 until 12) {
            val facetAngle = (facet * 30f + prismRotation) * PI.toFloat() / 180f
            val facetSize = radius * 0.8f

            val path = Path()
            path.moveTo(centerX, centerY)

            val angle1 = facetAngle - 0.15f
            val angle2 = facetAngle + 0.15f

            path.lineTo(
                centerX + cos(angle1) * facetSize,
                centerY + sin(angle1) * facetSize
            )
            path.lineTo(
                centerX + cos(angle2) * facetSize,
                centerY + sin(angle2) * facetSize
            )
            path.close()

            // Rainbow refraction color
            val hue = (colorShift + facet * 30f) % 360f
            val facetColor = Color.hsv(hue, 0.6f, 1f, 0.3f)

            drawPath(
                path = path,
                color = facetColor
            )

            // Facet edge highlight
            drawLine(
                color = Color.White.copy(alpha = 0.7f),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(facetAngle) * facetSize,
                    centerY + sin(facetAngle) * facetSize
                ),
                strokeWidth = 1.5f
            )
        }

        // Prismatic sparkles
        for (i in 0 until 20) {
            val angle = (i * 18f + prismRotation * 2f) * PI.toFloat() / 180f
            val sparkleDistance = radius * (0.4f + (i % 4) * 0.15f)
            val hue = (colorShift + i * 18f) % 360f
            val sparkleColor = Color.hsv(hue, 0.8f, 1f)
            val sparkleAlpha = if (i % 2 == 0) sparkle else 1f - sparkle

            drawCircle(
                color = sparkleColor.copy(alpha = sparkleAlpha * 0.8f),
                radius = radius * 0.05f,
                center = Offset(
                    centerX + cos(angle) * sparkleDistance,
                    centerY + sin(angle) * sparkleDistance
                )
            )

            // Sparkle rays
            for (ray in 0 until 4) {
                val rayAngle = angle + (ray * 90f) * PI.toFloat() / 180f
                drawLine(
                    color = sparkleColor.copy(alpha = sparkleAlpha * 0.5f),
                    start = Offset(
                        centerX + cos(angle) * sparkleDistance,
                        centerY + sin(angle) * sparkleDistance
                    ),
                    end = Offset(
                        centerX + cos(angle) * sparkleDistance + cos(rayAngle) * radius * 0.08f,
                        centerY + sin(angle) * sparkleDistance + sin(rayAngle) * radius * 0.08f
                    ),
                    strokeWidth = 1f
                )
            }
        }

        // Central bright reflection
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color.White.copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX - radius * 0.2f, centerY - radius * 0.2f),
                radius = radius * 0.3f
            ),
            radius = radius * 0.3f,
            center = Offset(centerX - radius * 0.2f, centerY - radius * 0.2f)
        )
    }
}

// ==================== BUBBLE 18: SUNSET BUBBLE ====================
@Composable
fun SunsetBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunset")
    val sunDescend by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunDescend"
    )
    val cloudMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudMove"
    )
    val colorPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Dynamic sunset gradient
        val gradientColors = listOf(
            Color(0xFFFFA07A).copy(alpha = 0.8f + colorPulse * 0.2f),
            Color(0xFFFF6347).copy(alpha = 0.9f),
            Color(0xFFFF4500).copy(alpha = 0.8f),
            Color(0xFFFF1493).copy(alpha = 0.7f),
            Color(0xFF9370DB).copy(alpha = 0.6f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = gradientColors,
                center = Offset(centerX, centerY + radius * 0.3f * sunDescend),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Sun
        val sunY = centerY - radius * 0.3f + sunDescend * radius * 0.6f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFACD),
                    Color(0xFFFFD700),
                    Color(0xFFFFA500).copy(alpha = 0.8f),
                    Color.Transparent
                ),
                center = Offset(centerX, sunY),
                radius = radius * 0.35f
            ),
            radius = radius * 0.35f,
            center = Offset(centerX, sunY)
        )

        // Sun rays
        for (ray in 0 until 12) {
            val rayAngle = (ray * 30f) * PI.toFloat() / 180f
            val rayLength = radius * (0.5f + colorPulse * 0.1f)

            drawLine(
                color = Color(0xFFFFD700).copy(alpha = 0.6f * (1f - sunDescend * 0.5f)),
                start = Offset(
                    centerX + cos(rayAngle) * radius * 0.35f,
                    sunY + sin(rayAngle) * radius * 0.35f
                ),
                end = Offset(
                    centerX + cos(rayAngle) * rayLength,
                    sunY + sin(rayAngle) * rayLength
                ),
                strokeWidth = 2f
            )
        }

        // Floating clouds
        for (cloud in 0 until 5) {
            val cloudProgress = (cloudMove + cloud * 0.2f) % 1f
            val cloudX = -radius * 0.3f + cloudProgress * (size.width + radius * 0.6f)
            val cloudY = centerY + (cloud - 2) * radius * 0.3f
            val cloudAlpha = 0.3f + sin(cloudProgress * PI.toFloat()) * 0.2f

            // Cloud shape (3 overlapping circles)
            for (puff in 0..2) {
                val puffX = cloudX + (puff - 1) * radius * 0.15f
                drawCircle(
                    color = Color.White.copy(alpha = cloudAlpha),
                    radius = radius * 0.12f,
                    center = Offset(puffX, cloudY)
                )
            }
        }

        // Horizon glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFFF4500).copy(alpha = 0.3f * (1f - sunDescend)),
                    Color(0xFFFF6347).copy(alpha = 0.5f * (1f - sunDescend))
                ),
                startY = centerY + radius * 0.5f,
                endY = centerY + radius
            ),
            topLeft = Offset(centerX - radius, centerY + radius * 0.5f),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 0.5f)
        )
    }
}

// ==================== BUBBLE 19: MIDNIGHT BUBBLE ====================
@Composable
fun MidnightBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "midnight")
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starTwinkle"
    )
    val moonGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "moonGlow"
    )
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudDrift"
    )
    val auroraWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraWave"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Deep night gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0A0A2E),
                    Color(0xFF050514),
                    Color(0xFF000000)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Subtle aurora effect
        for (wave in 0 until 3) {
            val path = Path()
            val waveY = centerY - radius * 0.6f + wave * radius * 0.15f
            path.moveTo(centerX - radius, waveY)

            for (i in 0..20) {
                val x = centerX - radius + (i / 20f) * radius * 2
                val y = waveY + sin(auroraWave + i * 0.3f + wave) * radius * 0.1f
                path.lineTo(x, y)
            }

            val auroraColor = when (wave) {
                0 -> Color(0xFF00FF88)
                1 -> Color(0xFF0088FF)
                else -> Color(0xFFAA00FF)
            }

            drawPath(
                path = path,
                color = auroraColor.copy(alpha = 0.2f),
                style = Stroke(width = radius * 0.08f)
            )
        }

        // Moon
        val moonX = centerX + radius * 0.4f
        val moonY = centerY - radius * 0.4f
        val moonRadius = radius * 0.3f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFAF0).copy(alpha = moonGlow),
                    Color(0xFFF0E68C).copy(alpha = 0.9f * moonGlow),
                    Color(0xFFDEB887).copy(alpha = 0.7f * moonGlow)
                ),
                center = Offset(moonX, moonY),
                radius = moonRadius
            ),
            radius = moonRadius,
            center = Offset(moonX, moonY)
        )

        // Moon craters
        for (crater in 0 until 5) {
            val craterAngle = (crater * 72f) * PI.toFloat() / 180f
            val craterDist = moonRadius * 0.4f
            drawCircle(
                color = Color(0xFFDEB887).copy(alpha = 0.4f * moonGlow),
                radius = moonRadius * 0.2f,
                center = Offset(
                    moonX + cos(craterAngle) * craterDist,
                    moonY + sin(craterAngle) * craterDist
                )
            )
        }

        // Moon glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFFFFFAF0).copy(alpha = 0.15f * moonGlow),
                    Color.Transparent
                ),
                center = Offset(moonX, moonY),
                radius = moonRadius * 2f
            ),
            radius = moonRadius * 2f,
            center = Offset(moonX, moonY)
        )

        // Twinkling stars
        for (i in 0 until 40) {
            val angle = (i * 9f) * PI.toFloat() / 180f
            val distance = radius * ((i % 10) / 10f) * 0.95f
            val twinklePhase = (starTwinkle + i * 0.1f) % 1f
            val starAlpha = 0.3f + sin(twinklePhase * PI.toFloat() * 2f) * 0.7f
            val starSize = radius * (0.015f + (i % 3) * 0.01f)

            drawCircle(
                color = Color.White.copy(alpha = starAlpha),
                radius = starSize,
                center = Offset(
                    centerX + cos(angle) * distance,
                    centerY + sin(angle) * distance
                )
            )
        }

        // Drifting night clouds
        for (cloud in 0 until 3) {
            val cloudProgress = (cloudDrift + cloud * 0.33f) % 1f
            val cloudX = -radius * 0.4f + cloudProgress * (size.width + radius * 0.8f)
            val cloudY = centerY + radius * 0.6f + cloud * radius * 0.15f

            for (puff in 0..2) {
                drawCircle(
                    color = Color(0xFF1A1A3E).copy(alpha = 0.6f),
                    radius = radius * 0.15f,
                    center = Offset(cloudX + (puff - 1) * radius * 0.2f, cloudY)
                )
            }
        }
    }
}

// ==================== BUBBLE 20: CHERRY BLOSSOM BUBBLE ====================
@Composable
fun CherryBlossomBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "cherryBlossom")
    val petalFall by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petalFall"
    )
    val windSway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "windSway"
    )
    val bloomPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bloomPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Soft pink gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFE4E1),
                    Color(0xFFFFB6C1),
                    Color(0xFFFF69B4).copy(alpha = 0.7f)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Cherry branch
        val branchPath = Path()
        branchPath.moveTo(centerX - radius * 0.8f, centerY + radius * 0.6f)
        branchPath.cubicTo(
            centerX - radius * 0.4f, centerY + radius * 0.3f,
            centerX - radius * 0.2f, centerY - radius * 0.2f,
            centerX + radius * 0.3f, centerY - radius * 0.5f
        )

        drawPath(
            path = branchPath,
            color = Color(0xFF8B4513).copy(alpha = 0.8f),
            style = Stroke(width = radius * 0.06f)
        )

        // Cherry blossoms on branch
        for (blossom in 0 until 8) {
            val t = blossom / 8f
            val blossomX = centerX - radius * 0.8f + (centerX + radius * 0.3f - (centerX - radius * 0.8f)) * t +
                    sin(t * PI.toFloat() * 2) * radius * 0.2f
            val blossomY = centerY + radius * 0.6f - radius * 1.1f * t + cos(t * PI.toFloat()) * radius * 0.2f

            // Draw flower petals
            for (petal in 0 until 5) {
                val petalAngle = (petal * 72f) * PI.toFloat() / 180f
                val petalSize = radius * 0.08f * bloomPulse

                drawCircle(
                    color = Color(0xFFFFB6C1).copy(alpha = 0.9f),
                    radius = petalSize,
                    center = Offset(
                        blossomX + cos(petalAngle) * petalSize * 0.7f,
                        blossomY + sin(petalAngle) * petalSize * 0.7f
                    )
                )
            }

            // Flower center
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = 0.8f),
                radius = radius * 0.03f,
                center = Offset(blossomX, blossomY)
            )
        }

        // Falling petals
        for (i in 0 until 20) {
            val fallProgress = (petalFall + i * 0.05f) % 1f
            val startAngle = (i * 18f) * PI.toFloat() / 180f
            val swayAmount = sin(fallProgress * PI.toFloat() * 3f + i) * windSway * radius * 0.3f

            val petalX = centerX + cos(startAngle) * radius * 0.5f + swayAmount
            val petalY = centerY - radius + fallProgress * radius * 2f
            val petalRotation = fallProgress * 360f + i * 20f
            val petalAlpha = sin(fallProgress * PI.toFloat()) * 0.8f

            // Petal shape (oval)
            drawOval(
                color = Color(0xFFFFB6C1).copy(alpha = petalAlpha),
                topLeft = Offset(
                    petalX - radius * 0.04f,
                    petalY - radius * 0.06f
                ),
                size = androidx.compose.ui.geometry.Size(radius * 0.08f, radius * 0.12f)
            )
        }
    }
}

// ==================== BUBBLE 21: TOXIC BUBBLE ====================
@Composable
fun ToxicBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "toxic")
    val radiationPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radiationPulse"
    )
    val bubbleRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleRise"
    )
    val neonFlicker by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonFlicker"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Toxic base gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00FF00).copy(alpha = 0.8f),
                    Color(0xFF32CD32),
                    Color(0xFF228B22),
                    Color(0xFF006400)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Radiation symbol rings
        for (ring in 0 until 3) {
            val ringRadius = radius * (0.3f + ring * 0.2f) * (1f + radiationPulse * 0.05f)
            drawCircle(
                color = Color(0xFF00FF00).copy(alpha = (0.6f - ring * 0.15f) * neonFlicker),
                radius = ringRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3f)
            )
        }

        // Radiation symbol blades
        for (blade in 0 until 3) {
            val bladeAngle = (blade * 120f) * PI.toFloat() / 180f
            val path = Path()

            val innerRadius = radius * 0.15f
            val outerRadius = radius * 0.65f
            val bladeWidth = 0.4f

            path.moveTo(centerX, centerY)
            path.lineTo(
                centerX + cos(bladeAngle - bladeWidth) * outerRadius,
                centerY + sin(bladeAngle - bladeWidth) * outerRadius
            )
            path.lineTo(
                centerX + cos(bladeAngle) * (outerRadius * 1.1f),
                centerY + sin(bladeAngle) * (outerRadius * 1.1f)
            )
            path.lineTo(
                centerX + cos(bladeAngle + bladeWidth) * outerRadius,
                centerY + sin(bladeAngle + bladeWidth) * outerRadius
            )
            path.close()

            drawPath(
                path = path,
                color = Color(0xFF000000).copy(alpha = 0.7f)
            )

            // Blade glow
            drawPath(
                path = path,
                color = Color(0xFF00FF00).copy(alpha = 0.3f * neonFlicker),
                style = Stroke(width = 2f)
            )
        }

        // Rising toxic bubbles
        for (i in 0 until 12) {
            val bubbleAngle = (i * 30f) * PI.toFloat() / 180f
            val riseProgress = (bubbleRise + i * 0.083f) % 1f
            val bubbleX = centerX + cos(bubbleAngle) * radius * 0.6f * (1f - riseProgress * 0.3f)
            val bubbleY = centerY + radius * 0.8f - riseProgress * radius * 1.6f
            val bubbleSize = radius * 0.08f * (1f - riseProgress * 0.5f)
            val bubbleAlpha = (1f - riseProgress) * 0.7f

            drawCircle(
                color = Color(0xFF7FFF00).copy(alpha = bubbleAlpha * neonFlicker),
                radius = bubbleSize,
                center = Offset(bubbleX, bubbleY)
            )

            // Bubble highlight
            drawCircle(
                color = Color.White.copy(alpha = bubbleAlpha * 0.4f),
                radius = bubbleSize * 0.3f,
                center = Offset(bubbleX - bubbleSize * 0.3f, bubbleY - bubbleSize * 0.3f)
            )
        }

        // Radioactive glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00FF00).copy(alpha = 0.3f * radiationPulse * neonFlicker),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 1.3f
            ),
            radius = radius * 1.3f,
            center = Offset(centerX, centerY)
        )

        // Central dark core
        drawCircle(
            color = Color(0xFF001100).copy(alpha = 0.7f),
            radius = radius * 0.15f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 22: WATER BUBBLE ====================
@Composable
fun WaterBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "water")
    val ripplePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripplePhase"
    )
    val dropFall by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropFall"
    )
    val waveFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveFlow"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base water gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE0F7FA),
                    Color(0xFF80DEEA),
                    Color(0xFF00BCD4),
                    Color(0xFF0097A7)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Concentric ripples
        for (ripple in 0 until 5) {
            val rippleProgress = (ripplePhase + ripple * 0.2f) % 1f
            val rippleRadius = radius * rippleProgress
            val rippleAlpha = (1f - rippleProgress) * 0.6f

            drawCircle(
                color = Color.White.copy(alpha = rippleAlpha),
                radius = rippleRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3f)
            )
        }

        // Water waves
        for (wave in 0 until 3) {
            val path = Path()
            val waveY = centerY + (wave - 1) * radius * 0.4f
            path.moveTo(centerX - radius, waveY)

            for (i in 0..20) {
                val x = centerX - radius + (i / 20f) * radius * 2
                val y = waveY + sin(waveFlow + i * 0.5f + wave) * radius * 0.08f
                path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.3f),
                style = Stroke(width = 2f)
            )
        }

        // Falling water drops
        for (i in 0 until 8) {
            val dropAngle = (i * 45f) * PI.toFloat() / 180f
            val dropProgress = (dropFall + i * 0.125f) % 1f

            if (dropProgress < 0.7f) {
                val dropX = centerX + cos(dropAngle) * radius * 0.3f
                val dropY = centerY - radius * 0.9f + dropProgress * radius * 1.3f
                val dropAlpha = 1f - dropProgress / 0.7f

                // Drop shape
                drawCircle(
                    color = Color(0xFF4DD0E1).copy(alpha = dropAlpha),
                    radius = radius * 0.06f,
                    center = Offset(dropX, dropY)
                )

                // Drop highlight
                drawCircle(
                    color = Color.White.copy(alpha = dropAlpha * 0.7f),
                    radius = radius * 0.02f,
                    center = Offset(dropX - radius * 0.02f, dropY - radius * 0.02f)
                )
            } else {
                // Splash ripple
                val splashProgress = (dropProgress - 0.7f) / 0.3f
                val splashX = centerX + cos(dropAngle) * radius * 0.3f
                val splashY = centerY + radius * 0.4f
                val splashRadius = radius * 0.2f * splashProgress
                val splashAlpha = (1f - splashProgress) * 0.5f

                drawCircle(
                    color = Color.White.copy(alpha = splashAlpha),
                    radius = splashRadius,
                    center = Offset(splashX, splashY),
                    style = Stroke(width = 2f)
                )
            }
        }

        // Light reflection
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f)
        )
    }
}

// ==================== BUBBLE 23: DIAMOND BUBBLE ====================
@Composable
fun DiamondBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "diamond")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )
    val refraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refraction"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base diamond gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFE0F7FF),
                    Color(0xFFB0E0E6),
                    Color(0xFF87CEEB)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Diamond facets
        val facetCount = 8
        for (facet in 0 until facetCount) {
            val facetAngle = (facet * (360f / facetCount) + rotation) * PI.toFloat() / 180f

            val path = Path()
            path.moveTo(centerX, centerY)

            val angle1 = facetAngle - (PI.toFloat() / facetCount)
            val angle2 = facetAngle + (PI.toFloat() / facetCount)

            path.lineTo(
                centerX + cos(angle1) * radius,
                centerY + sin(angle1) * radius
            )
            path.lineTo(
                centerX + cos(angle2) * radius,
                centerY + sin(angle2) * radius
            )
            path.close()

            // Rainbow refraction
            val hue = (refraction + facet * (360f / facetCount)) % 360f
            val facetColor = Color.hsv(hue, 0.3f, 1f, 0.4f)

            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        facetColor,
                        facetColor.copy(alpha = 0.2f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius
                )
            )

            // Facet edge
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(facetAngle) * radius,
                    centerY + sin(facetAngle) * radius
                ),
                strokeWidth = 2f
            )
        }

        // Rotating light beams
        for (beam in 0 until 4) {
            val beamAngle = (beam * 90f + rotation * 2f) * PI.toFloat() / 180f

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f * sparkle),
                        Color.White.copy(alpha = 0.3f * sparkle),
                        Color.Transparent
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(
                        centerX + cos(beamAngle) * radius * 1.2f,
                        centerY + sin(beamAngle) * radius * 1.2f
                    )
                ),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(beamAngle) * radius * 1.2f,
                    centerY + sin(beamAngle) * radius * 1.2f
                ),
                strokeWidth = radius * 0.15f
            )
        }

        // Sparkle points
        for (i in 0 until 12) {
            val sparkleAngle = (i * 30f + rotation * 1.5f) * PI.toFloat() / 180f
            val sparkleDistance = radius * (0.6f + (i % 3) * 0.15f)
            val sparkleAlpha = if (i % 2 == 0) sparkle else 1f - sparkle

            // Star sparkle
            for (ray in 0 until 4) {
                val rayAngle = sparkleAngle + (ray * 90f) * PI.toFloat() / 180f
                val rayLength = radius * 0.12f

                drawLine(
                    color = Color.White.copy(alpha = sparkleAlpha),
                    start = Offset(
                        centerX + cos(sparkleAngle) * sparkleDistance,
                        centerY + sin(sparkleAngle) * sparkleDistance
                    ),
                    end = Offset(
                        centerX + cos(sparkleAngle) * sparkleDistance + cos(rayAngle) * rayLength,
                        centerY + sin(sparkleAngle) * sparkleDistance + sin(rayAngle) * rayLength
                    ),
                    strokeWidth = 2f
                )
            }
        }

        // Central brilliant shine
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color.White.copy(alpha = 0.5f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.35f
            ),
            radius = radius * 0.35f,
            center = Offset(centerX, centerY)
        )
    }
}


// ==================== BUBBLE 24: NEON BUBBLE ====================
@Composable
fun NeonBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val colorCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorCycle"
    )
    val lineFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lineFlow"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Dark cyberpunk background
        drawCircle(
            color = Color(0xFF0A0A0A),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Neon circles
        for (circle in 0 until 5) {
            val circleRadius = radius * (0.3f + circle * 0.15f)
            val hue = (colorCycle + circle * 72f) % 360f
            val neonColor = Color.hsv(hue, 1f, 1f)

            // Glow
            drawCircle(
                color = neonColor.copy(alpha = 0.3f * pulse),
                radius = circleRadius + 5f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 10f)
            )

            // Main line
            drawCircle(
                color = neonColor.copy(alpha = 0.9f * pulse),
                radius = circleRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3f)
            )
        }

        // Flowing neon lines
        for (line in 0 until 8) {
            val lineAngle = (line * 45f) * PI.toFloat() / 180f
            val flowProgress = (lineFlow + line * 0.125f) % 1f
            val lineLength = radius * flowProgress

            val hue = (colorCycle + line * 45f) % 360f
            val lineColor = Color.hsv(hue, 1f, 1f)

            // Glow trail
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.6f * pulse),
                        lineColor.copy(alpha = 0.2f * pulse),
                        Color.Transparent
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(
                        centerX + cos(lineAngle) * lineLength,
                        centerY + sin(lineAngle) * lineLength
                    )
                ),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(lineAngle) * lineLength,
                    centerY + sin(lineAngle) * lineLength
                ),
                strokeWidth = 8f
            )

            // Bright line
            drawLine(
                color = lineColor.copy(alpha = 0.9f * pulse),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(lineAngle) * lineLength,
                    centerY + sin(lineAngle) * lineLength
                ),
                strokeWidth = 2f
            )
        }

        // Central neon glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f * pulse),
                    Color.hsv(colorCycle % 360f, 1f, 1f, 0.6f * pulse),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.3f
            ),
            radius = radius * 0.3f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 25: AURORA BUBBLE ====================
@Composable
fun AuroraBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorShift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Dark polar night background
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0D1F2D),
                    Color(0xFF0A1828),
                    Color(0xFF000814)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Aurora waves
        for (layer in 0 until 4) {
            val path = Path()
            val layerY = centerY - radius * 0.4f + layer * radius * 0.2f

            path.moveTo(centerX - radius, layerY)

            for (i in 0..30) {
                val progress = i / 30f
                val x = centerX - radius + progress * radius * 2
                val waveOffset1 = sin(wave1 + progress * PI.toFloat() * 3f + layer) * radius * 0.15f
                val waveOffset2 = sin(wave2 + progress * PI.toFloat() * 2f - layer) * radius * 0.1f
                val y = layerY + waveOffset1 + waveOffset2

                path.lineTo(x, y)
            }

            // Aurora colors
            val hue = (colorShift + layer * 40f) % 360f
            val auroraColors = when (layer % 3) {
                0 -> listOf(Color(0xFF00FF88), Color(0xFF00FFAA))
                1 -> listOf(Color(0xFF0088FF), Color(0xFF00AAFF))
                else -> listOf(Color(0xFFAA00FF), Color(0xFFCC00FF))
            }

            // Aurora glow
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        auroraColors[0].copy(alpha = 0.6f),
                        auroraColors[1].copy(alpha = 0.3f)
                    )
                ),
                style = Stroke(width = radius * 0.2f)
            )

            // Bright edge
            drawPath(
                path = path,
                color = auroraColors[0].copy(alpha = 0.9f),
                style = Stroke(width = 2f)
            )
        }

        // Flowing particles
        for (i in 0 until 30) {
            val angle = (i * 12f) * PI.toFloat() / 180f
            val distance = radius * (0.5f + (i % 5) * 0.1f)
            val flowPhase = (wave1 + i * 0.2f) % (2 * PI.toFloat())
            val particleAlpha = 0.3f + sin(flowPhase) * 0.5f

            val hue = (colorShift + i * 12f) % 360f
            val particleColor = Color.hsv(hue, 0.8f, 1f)

            drawCircle(
                color = particleColor.copy(alpha = particleAlpha),
                radius = radius * 0.02f,
                center = Offset(
                    centerX + cos(angle) * distance,
                    centerY + sin(angle) * distance + sin(flowPhase) * radius * 0.2f
                )
            )
        }
    }
}

// ==================== BUBBLE 26: RAINBOW SWIRL BUBBLE ====================
@Composable
fun RainbowSwirlBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbowSwirl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val colorFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorFlow"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // White base
        drawCircle(
            color = Color.White,
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Rainbow spiral arms
        for (arm in 0 until 6) {
            val armAngleOffset = arm * 60f + rotation
            val path = Path()

            var firstPoint = true
            for (point in 0..40) {
                val spiralProgress = point / 40f
                val spiralAngle = (armAngleOffset + spiralProgress * 360f) * PI.toFloat() / 180f
                val spiralDistance = radius * spiralProgress * pulse

                val x = centerX + cos(spiralAngle) * spiralDistance
                val y = centerY + sin(spiralAngle) * spiralDistance

                if (firstPoint) {
                    path.moveTo(x, y)
                    firstPoint = false
                } else {
                    path.lineTo(x, y)
                }
            }

            // Rainbow color for each arm
            val hue = (colorFlow + arm * 60f) % 360f
            val armColor = Color.hsv(hue, 0.9f, 1f)

            // Arm glow
            drawPath(
                path = path,
                color = armColor.copy(alpha = 0.4f),
                style = Stroke(width = radius * 0.15f)
            )

            // Arm solid
            drawPath(
                path = path,
                color = armColor.copy(alpha = 0.8f),
                style = Stroke(width = radius * 0.08f)
            )
        }

        // Rainbow particles
        for (i in 0 until 30) {
            val angle = (i * 12f + rotation * 2f) * PI.toFloat() / 180f
            val distance = radius * ((i % 10) / 10f) * 0.85f
            val hue = (colorFlow + i * 12f) % 360f
            val particleColor = Color.hsv(hue, 1f, 1f)

            drawCircle(
                color = particleColor.copy(alpha = 0.8f),
                radius = radius * 0.04f,
                center = Offset(
                    centerX + cos(angle) * distance,
                    centerY + sin(angle) * distance
                )
            )
        }

        // Central bright spot
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color.hsv(colorFlow % 360f, 0.6f, 1f, 0.6f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.3f
            ),
            radius = radius * 0.3f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 27: SMOKE BUBBLE ====================
@Composable
fun SmokeBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "smoke")
    val smokeRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smokeRise"
    )
    val wispFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "wispFlow"
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacity"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Dark smoky gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A4A4A),
                    Color(0xFF2A2A2A),
                    Color(0xFF1A1A1A)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Rising smoke wisps
        for (wisp in 0 until 8) {
            val wispAngle = (wisp * 45f) * PI.toFloat() / 180f
            val riseProgress = (smokeRise + wisp * 0.125f) % 1f
            val baseX = centerX + cos(wispAngle) * radius * 0.3f
            val baseY = centerY + radius * 0.5f

            val path = Path()
            path.moveTo(baseX, baseY)

            for (seg in 0..10) {
                val segProgress = seg / 10f * riseProgress
                val flowOffset = sin(wispFlow + wisp + segProgress * PI.toFloat() * 2f) * radius * 0.2f
                val x = baseX + flowOffset
                val y = baseY - segProgress * radius * 1.5f
                path.lineTo(x, y)
            }

            val wispAlpha = (1f - riseProgress) * opacity

            // Smoke trail
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFAAAAAA).copy(alpha = wispAlpha * 0.6f),
                        Color(0xFF888888).copy(alpha = wispAlpha * 0.4f),
                        Color(0xFF666666).copy(alpha = wispAlpha * 0.2f),
                        Color.Transparent
                    ),
                    start = Offset(baseX, baseY),
                    end = Offset(baseX, baseY - riseProgress * radius * 1.5f)
                ),
                style = Stroke(width = radius * 0.15f * (1f - riseProgress * 0.5f))
            )
        }

        // Smoke puffs
        for (i in 0 until 15) {
            val puffAngle = (i * 24f) * PI.toFloat() / 180f
            val puffProgress = (smokeRise + i * 0.067f) % 1f
            val puffX = centerX + cos(puffAngle + wispFlow) * radius * 0.5f * (1f + puffProgress * 0.5f)
            val puffY = centerY - puffProgress * radius * 0.8f
            val puffSize = radius * 0.2f * (1f + puffProgress)
            val puffAlpha = (1f - puffProgress) * 0.5f * opacity

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF999999).copy(alpha = puffAlpha),
                        Color(0xFF666666).copy(alpha = puffAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(puffX, puffY),
                    radius = puffSize
                ),
                radius = puffSize,
                center = Offset(puffX, puffY)
            )
        }

        // Ethereal glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f * opacity),
                    Color(0xFF888888).copy(alpha = 0.05f * opacity),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.6f
            ),
            radius = radius * 0.6f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== BUBBLE 28: CANDY BUBBLE ====================
@Composable
fun CandyBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "candy")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "float"
    )
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Sweet pastel gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFE5F0),
                    Color(0xFFFFB6E1),
                    Color(0xFFFF99CC),
                    Color(0xFFFF66B2)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Candy spiral stripes
        for (stripe in 0 until 8) {
            val path = Path()
            val stripeAngleOffset = stripe * 45f + rotation

            var firstPoint = true
            for (point in 0..30) {
                val spiralProgress = point / 30f
                val spiralAngle = (stripeAngleOffset + spiralProgress * 360f) * PI.toFloat() / 180f
                val spiralDistance = radius * spiralProgress * 0.9f

                val x = centerX + cos(spiralAngle) * spiralDistance
                val y = centerY + sin(spiralAngle) * spiralDistance

                if (firstPoint) {
                    path.moveTo(x, y)
                    firstPoint = false
                } else {
                    path.lineTo(x, y)
                }
            }

            val stripeColor = if (stripe % 2 == 0) Color(0xFFFF1493) else Color(0xFFFFFFFF)

            drawPath(
                path = path,
                color = stripeColor.copy(alpha = 0.7f),
                style = Stroke(width = radius * 0.12f)
            )
        }

        // Floating candy pieces
        for (i in 0 until 12) {
            val candyAngle = (i * 30f) * PI.toFloat() / 180f
            val floatOffset = sin(floatAnimation + i) * radius * 0.1f
            val candyDistance = radius * 0.6f + floatOffset
            val candyX = centerX + cos(candyAngle) * candyDistance
            val candyY = centerY + sin(candyAngle) * candyDistance

            val candyColors = listOf(
                Color(0xFFFF6B9D),
                Color(0xFFFFC0CB),
                Color(0xFF98D8C8),
                Color(0xFFFFD93D)
            )
            val candyColor = candyColors[i % 4]

            // Candy piece
            drawCircle(
                color = candyColor.copy(alpha = 0.9f),
                radius = radius * 0.08f,
                center = Offset(candyX, candyY)
            )

            // Candy shine
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = radius * 0.03f,
                center = Offset(candyX - radius * 0.03f, candyY - radius * 0.03f)
            )
        }

        // Sparkles
        for (i in 0 until 15) {
            val sparkleAngle = (i * 24f + rotation) * PI.toFloat() / 180f
            val sparkleDistance = radius * (0.4f + (i % 4) * 0.15f)
            val sparkleAlpha = if (i % 2 == 0) sparkle else 1f - sparkle

            // Star sparkle
            for (ray in 0 until 4) {
                val rayAngle = sparkleAngle + (ray * 90f) * PI.toFloat() / 180f
                drawLine(
                    color = Color.White.copy(alpha = sparkleAlpha * 0.8f),
                    start = Offset(
                        centerX + cos(sparkleAngle) * sparkleDistance,
                        centerY + sin(sparkleAngle) * sparkleDistance
                    ),
                    end = Offset(
                        centerX + cos(sparkleAngle) * sparkleDistance + cos(rayAngle) * radius * 0.06f,
                        centerY + sin(sparkleAngle) * sparkleDistance + sin(rayAngle) * radius * 0.06f
                    ),
                    strokeWidth = 2f
                )
            }
        }
    }
}

// ==================== BUBBLE 29: METAL BUBBLE ====================
@Composable
fun MetalBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "metal")
    val reflectionMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reflectionMove"
    )
    val chromeShine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chromeShine"
    )
    val metalPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "metalPulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Base metallic gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE8E8E8),
                    Color(0xFFC0C0C0),
                    Color(0xFF909090),
                    Color(0xFF606060)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Chrome reflection bands
        for (band in 0 until 5) {
            val bandProgress = (reflectionMove + band * 0.2f) % 1f
            val bandY = centerY - radius + bandProgress * radius * 2f
            val bandHeight = radius * 0.15f

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.6f * metalPulse),
                        Color.Transparent
                    ),
                    startY = bandY - bandHeight,
                    endY = bandY + bandHeight
                ),
                topLeft = Offset(centerX - radius, bandY - bandHeight),
                size = androidx.compose.ui.geometry.Size(radius * 2f, bandHeight * 2f)
            )
        }

        // Circular chrome reflections
        for (i in 0 until 8) {
            val angle = (i * 45f) * PI.toFloat() / 180f
            val shinePhase = sin(chromeShine + i) * 0.5f + 0.5f
            val shineDistance = radius * 0.7f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f * shinePhase * metalPulse),
                        Color.White.copy(alpha = 0.3f * shinePhase * metalPulse),
                        Color.Transparent
                    ),
                    center = Offset(
                        centerX + cos(angle) * shineDistance,
                        centerY + sin(angle) * shineDistance
                    ),
                    radius = radius * 0.2f
                ),
                radius = radius * 0.2f,
                center = Offset(
                    centerX + cos(angle) * shineDistance,
                    centerY + sin(angle) * shineDistance
                )
            )
        }

        // Polished metal highlights
        for (highlight in 0 until 3) {
            val highlightAngle = (highlight * 120f + chromeShine * 180f / PI.toFloat()) * PI.toFloat() / 180f
            val highlightDist = radius * 0.5f

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.8f * metalPulse),
                        Color.Transparent
                    ),
                    start = Offset(
                        centerX + cos(highlightAngle - 0.3f) * highlightDist,
                        centerY + sin(highlightAngle - 0.3f) * highlightDist
                    ),
                    end = Offset(
                        centerX + cos(highlightAngle + 0.3f) * highlightDist,
                        centerY + sin(highlightAngle + 0.3f) * highlightDist
                    )
                ),
                start = Offset(
                    centerX + cos(highlightAngle - 0.3f) * highlightDist,
                    centerY + sin(highlightAngle - 0.3f) * highlightDist
                ),
                end = Offset(
                    centerX + cos(highlightAngle + 0.3f) * highlightDist,
                    centerY + sin(highlightAngle + 0.3f) * highlightDist
                ),
                strokeWidth = radius * 0.2f
            )
        }

        // Central brilliant reflection
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f * metalPulse),
                    Color(0xFFE0E0E0).copy(alpha = 0.6f * metalPulse),
                    Color.Transparent
                ),
                center = Offset(centerX - radius * 0.2f, centerY - radius * 0.3f),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = Offset(centerX - radius * 0.2f, centerY - radius * 0.3f)
        )
    }
}

// ==================== BUBBLE 30: PLASMA BUBBLE ====================
@Composable
fun PlasmaBubble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "plasma")
    val plasmaFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "plasmaFlow"
    )
    val energyPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "energyPulse"
    )
    val chaosRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chaosRotation"
    )
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorShift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2.5f

        // Dark energy core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A0080),
                    Color(0xFF2A0050),
                    Color(0xFF1A0030),
                    Color(0xFF000000)
                ),
                center = Offset(centerX, centerY),
                radius = radius
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Plasma tendrils
        for (tendril in 0 until 12) {
            val tendrilAngle = (tendril * 30f + chaosRotation) * PI.toFloat() / 180f
            val path = Path()
            path.moveTo(centerX, centerY)

            for (seg in 0..15) {
                val progress = seg / 15f
                val distance = radius * progress * (0.8f + energyPulse * 0.2f)
                val chaos = sin(plasmaFlow + seg * 0.5f + tendril) * radius * 0.2f
                val perpAngle = tendrilAngle + PI.toFloat() / 2f

                val x = centerX + cos(tendrilAngle) * distance + cos(perpAngle) * chaos
                val y = centerY + sin(tendrilAngle) * distance + sin(perpAngle) * chaos

                path.lineTo(x, y)
            }

            val hue = (colorShift + tendril * 30f) % 360f
            val tendrilColor = Color.hsv(hue, 1f, 1f)

            // Tendril glow
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        tendrilColor.copy(alpha = 0.8f * energyPulse),
                        tendrilColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(
                        centerX + cos(tendrilAngle) * radius,
                        centerY + sin(tendrilAngle) * radius
                    )
                ),
                style = Stroke(width = radius * 0.08f)
            )

            // Bright core
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.9f * energyPulse),
                style = Stroke(width = 2f)
            )
        }

        // Energy particles
        for (i in 0 until 40) {
            val particleAngle = (i * 9f + chaosRotation * 2f) * PI.toFloat() / 180f
            val particleDistance = radius * ((i % 10) / 10f) * (0.7f + sin(plasmaFlow + i) * 0.3f)
            val hue = (colorShift + i * 9f) % 360f
            val particleColor = Color.hsv(hue, 1f, 1f)

            drawCircle(
                color = particleColor.copy(alpha = 0.8f * energyPulse),
                radius = radius * 0.03f,
                center = Offset(
                    centerX + cos(particleAngle) * particleDistance,
                    centerY + sin(particleAngle) * particleDistance
                )
            )

            // Particle trail
            drawLine(
                color = particleColor.copy(alpha = 0.4f * energyPulse),
                start = Offset(centerX, centerY),
                end = Offset(
                    centerX + cos(particleAngle) * particleDistance,
                    centerY + sin(particleAngle) * particleDistance
                ),
                strokeWidth = 1f
            )
        }

        // Central plasma core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f * energyPulse),
                    Color(0xFFFF00FF).copy(alpha = 0.7f * energyPulse),
                    Color(0xFF8000FF).copy(alpha = 0.4f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radius * 0.4f
            ),
            radius = radius * 0.4f,
            center = Offset(centerX, centerY)
        )

        // Outer energy ring
        drawCircle(
            color = Color.White.copy(alpha = 0.6f * energyPulse),
            radius = radius * (0.95f + energyPulse * 0.05f),
            center = Offset(centerX, centerY),
            style = Stroke(width = 3f)
        )
    }
}