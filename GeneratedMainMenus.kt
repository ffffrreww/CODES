package com.appsdevs.popit

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.runtime. Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*

// ==================== GENERATED MAIN MENUS (CODE-BASED) ====================

// ==================== MAIN MENU 11:  NEON PULSE (ANIMATED) ====================
@Composable
fun NeonPulseMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val pulse by infiniteTransition. animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "pulse"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Dark background
        drawRect(color = Color(0xFF0A0A0A))

        val centerX = size. width / 2
        val centerY = size.height / 2

        // Rotating neon rings
        for (ring in 1..5) {
            val ringRadius = size.minDimension * (0.1f + ring * 0.08f)
            val ringRotation = rotation * (if (ring % 2 == 0) 1f else -1f)
            val neonColor = when (ring % 3) {
                0 -> Color(0xFFFF00FF)
                1 -> Color(0xFF00FFFF)
                else -> Color(0xFFFFFF00)
            }

            // Ring glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color. Transparent,
                        neonColor. copy(alpha = 0.2f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = ringRadius + 20f
                ),
                radius = ringRadius + 20f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 15f)
            )

            // Main ring with dashes
            val dashCount = 8 + ring * 4
            for (dash in 0 until dashCount) {
                val dashAngle = ((dash * 360f / dashCount) + ringRotation) * PI. toFloat() / 180f
                val dashLength = ringRadius * 0.15f
                val startRadius = ringRadius - dashLength / 2
                val endRadius = ringRadius + dashLength / 2

                drawLine(
                    color = neonColor. copy(alpha = 0.8f * pulse),
                    start = Offset(
                        centerX + cos(dashAngle) * startRadius,
                        centerY + sin(dashAngle) * startRadius
                    ),
                    end = Offset(
                        centerX + cos(dashAngle) * endRadius,
                        centerY + sin(dashAngle) * endRadius
                    ),
                    strokeWidth = 3f
                )
            }
        }

        // Center glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.8f * pulse),
                    Color(0xFFFF00FF).copy(alpha = 0.4f * pulse),
                    Color. Transparent
                ),
                center = Offset(centerX, centerY),
                radius = size.minDimension * 0.15f
            ),
            radius = size. minDimension * 0.15f,
            center = Offset(centerX, centerY)
        )

        // Corner decorations
        val cornerSize = size.minDimension * 0.15f
        val corners = listOf(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            Offset(0f, size.height),
            Offset(size.width, size. height)
        )

        corners.forEachIndexed { index, corner ->
            val cornerColor = if (index % 2 == 0) Color(0xFFFF00FF) else Color(0xFF00FFFF)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        cornerColor.copy(alpha = 0.4f * pulse),
                        Color.Transparent
                    ),
                    center = corner,
                    radius = cornerSize
                ),
                radius = cornerSize,
                center = corner
            )
        }
    }
}

// ==================== MAIN MENU 12: GEOMETRIC WAVES (ANIMATED) ====================
@Composable
fun GeometricWavesMainMenu(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "geoWaves")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI. toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF16213E),
                    Color(0xFF0F3460)
                )
            )
        )

        // Geometric wave layers
        val waveColors = listOf(
            Color(0xFFE94560),
            Color(0xFFFF6B6B),
            Color(0xFFFFA07A),
            Color(0xFFFFD93D)
        )

        for (layer in 0 until 4) {
            val layerPhase = wavePhase + layer * 0.5f
            val baseY = size.height * (0.3f + layer * 0.15f)

            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, baseY)

                // Create geometric wave pattern
                val segments = 20
                for (i in 0.. segments) {
                    val x = i * size.width / segments
                    val waveY = baseY + sin(layerPhase + i * 0.5f) * size.height * 0.05f

                    if (i % 2 == 0) {
                        lineTo(x, waveY - size.height * 0.02f)
                    } else {
                        lineTo(x, waveY + size.height * 0.02f)
                    }
                }

                lineTo(size.width, size.height)
                close()
            }

            drawPath(
                path = path,
                color = waveColors[layer]. copy(alpha = 0.7f - layer * 0.1f)
            )
        }

        // Floating geometric shapes
        for (i in 0 until 15) {
            val shapeX = ((i * 137) % 100) / 100f * size.width
            val shapeBaseY = ((i * 73) % 100) / 100f * size.height * 0.5f
            val shapeY = shapeBaseY + sin(wavePhase + i * 0.3f) * 20f
            val shapeSize = 10f + (i % 5) * 5f

            when (i % 3) {
                0 -> {
                    // Triangle
                    val trianglePath = Path().apply {
                        moveTo(shapeX, shapeY - shapeSize)
                        lineTo(shapeX - shapeSize, shapeY + shapeSize)
                        lineTo(shapeX + shapeSize, shapeY + shapeSize)
                        close()
                    }
                    drawPath(
                        path = trianglePath,
                        color = Color. White.copy(alpha = 0.3f)
                    )
                }
                1 -> {
                    // Square
                    drawRect(
                        color = Color.White. copy(alpha = 0.25f),
                        topLeft = Offset(shapeX - shapeSize / 2, shapeY - shapeSize / 2),
                        size = androidx.compose.ui. geometry.Size(shapeSize, shapeSize)
                    )
                }
                else -> {
                    // Circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = shapeSize / 2,
                        center = Offset(shapeX, shapeY)
                    )
                }
            }
        }
    }
}

// ==================== MAIN MENU 13: PARTICLE FIELD (ANIMATED) ====================
@Composable
fun ParticleFieldMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Dark gradient background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A0A2E),
                    Color(0xFF0D0D1A),
                    Color(0xFF000000)
                ),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.maxDimension
            )
        )

        // Particle system
        val particleCount = 100
        for (i in 0 until particleCount) {
            val seed = i * 1337
            val baseX = ((seed * 7) % 1000) / 1000f
            val baseY = ((seed * 13) % 1000) / 1000f
            val speed = 0.0001f + ((seed * 17) % 100) / 100000f
            val particleSize = 2f + ((seed * 23) % 100) / 50f

            // Calculate position with time
            val particleX = ((baseX + time * speed) % 1f) * size.width
            val particleY = ((baseY + time * speed * 0.5f) % 1f) * size.height

            // Particle color based on position
            val hue = (baseX + baseY) * 0.5f
            val particleColor = when ((hue * 4).toInt() % 4) {
                0 -> Color(0xFFFF00FF)
                1 -> Color(0xFF00FFFF)
                2 -> Color(0xFFFFFF00)
                else -> Color(0xFF00FF00)
            }

            // Draw particle with glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        particleColor.copy(alpha = 0.8f),
                        particleColor.copy(alpha = 0.3f),
                        Color. Transparent
                    ),
                    center = Offset(particleX, particleY),
                    radius = particleSize * 3
                ),
                radius = particleSize * 3,
                center = Offset(particleX, particleY)
            )

            drawCircle(
                color = Color. White.copy(alpha = 0.9f),
                radius = particleSize * 0.5f,
                center = Offset(particleX, particleY)
            )

            // Connection lines to nearby particles
            if (i < particleCount - 1) {
                val nextSeed = (i + 1) * 1337
                val nextBaseX = ((nextSeed * 7) % 1000) / 1000f
                val nextBaseY = ((nextSeed * 13) % 1000) / 1000f
                val nextX = ((nextBaseX + time * speed) % 1f) * size.width
                val nextY = ((nextBaseY + time * speed * 0.5f) % 1f) * size.height

                val distance = sqrt((particleX - nextX).pow(2) + (particleY - nextY).pow(2))
                if (distance < 100f) {
                    drawLine(
                        color = particleColor.copy(alpha = (1f - distance / 100f) * 0.3f),
                        start = Offset(particleX, particleY),
                        end = Offset(nextX, nextY),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

// ==================== MAIN MENU 14: GRADIENT MESH (ANIMATED) ====================
@Composable
fun GradientMeshMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val colorShift by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color"
    )
    val blobMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blob"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Base gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F0F23),
                    Color(0xFF1A1A3E)
                )
            )
        )

        // Animated color blobs
        val blobs = listOf(
            Triple(0.3f, 0.3f, Color(0xFFFF6B6B)),
            Triple(0.7f, 0.4f, Color(0xFF4ECDC4)),
            Triple(0.5f, 0.7f, Color(0xFFFFE66D)),
            Triple(0.2f, 0.6f, Color(0xFF9B59B6)),
            Triple(0.8f, 0.8f, Color(0xFF3498DB))
        )

        blobs.forEachIndexed { index, (baseX, baseY, color) ->
            val phaseOffset = index * 0.8f
            val moveX = sin(blobMove + phaseOffset) * 0.1f
            val moveY = cos(blobMove + phaseOffset * 0.7f) * 0.1f

            val blobX = (baseX + moveX) * size.width
            val blobY = (baseY + moveY) * size.height
            val blobRadius = size.minDimension * (0.3f + sin(colorShift * 2 * PI. toFloat() + index) * 0.1f)

            // Hue shift
            val shiftedColor = when (((colorShift + index * 0.2f) * 5).toInt() % 5) {
                0 -> color
                1 -> Color(color.red, color.blue, color.green, color.alpha)
                2 -> Color(color.blue, color.red, color.green, color.alpha)
                3 -> Color(color.green, color.red, color. blue, color.alpha)
                else -> Color(color.blue, color.green, color.red, color.alpha)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        shiftedColor.copy(alpha = 0.6f),
                        shiftedColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(blobX, blobY),
                    radius = blobRadius
                ),
                radius = blobRadius,
                center = Offset(blobX, blobY)
            )
        }

        // Overlay noise/grain effect (simplified)
        for (i in 0 until 200) {
            val noiseX = ((i * 97) % 1000) / 1000f * size.width
            val noiseY = ((i * 131) % 1000) / 1000f * size.height
            drawCircle(
                color = Color. White.copy(alpha = 0.02f),
                radius = 1f,
                center = Offset(noiseX, noiseY)
            )
        }
    }
}

// ==================== MAIN MENU 15: MATRIX RAIN (ANIMATED) ====================
@Composable
fun MatrixRainMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")
    val rainFall by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Black background
        drawRect(color = Color(0xFF0D0D0D))

        // Matrix rain columns
        val columnCount = 30
        val columnWidth = size. width / columnCount

        for (col in 0 until columnCount) {
            val seed = col * 1337
            val speed = 0.5f + ((seed * 7) % 100) / 200f
            val startOffset = ((seed * 13) % 100) / 100f

            val dropCount = 15
            for (drop in 0 until dropCount) {
                val dropSeed = seed + drop * 73
                val dropPhase = ((rainFall * speed + startOffset + drop * 0.08f) % 1f)
                val dropY = dropPhase * size.height * 1.2f - size.height * 0.1f
                val dropX = col * columnWidth + columnWidth / 2

                if (dropY >= 0 && dropY <= size.height) {
                    // Brightness based on position in trail
                    val brightness = 1f - drop * 0.06f
                    val green = (brightness * 255).toInt().coerceIn(100, 255)

                    // Character simulation (rectangles)
                    val charHeight = 15f
                    val charWidth = 10f

                    drawRect(
                        color = Color(0, green, 0, (brightness * 255).toInt()),
                        topLeft = Offset(dropX - charWidth / 2, dropY),
                        size = androidx.compose.ui.geometry.Size(charWidth, charHeight)
                    )

                    // Head glow (brightest)
                    if (drop == 0) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFF00FF00).copy(alpha = 0.5f),
                                    Color. Transparent
                                ),
                                center = Offset(dropX, dropY + charHeight / 2),
                                radius = 15f
                            ),
                            radius = 15f,
                            center = Offset(dropX, dropY + charHeight / 2)
                        )
                    }
                }
            }
        }

        // Scanline effect
        for (y in 0.. size.height. toInt() step 4) {
            drawLine(
                color = Color.Black.copy(alpha = 0.1f),
                start = Offset(0f, y. toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}

// ==================== MAIN MENU 16: COSMIC NEBULA (ANIMATED) ====================
@Composable
fun CosmicNebulaMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "nebula")
    val nebulaFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow"
    )
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep space background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0D0221),
                    Color(0xFF050510),
                    Color(0xFF000000)
                ),
                center = Offset(size.width / 2, size.height / 2),
                radius = size.maxDimension
            )
        )

        // Nebula clouds
        val nebulaColors = listOf(
            Color(0xFF9400D3),
            Color(0xFFFF1493),
            Color(0xFF00CED1),
            Color(0xFF4B0082)
        )

        for (cloud in 0 until 8) {
            val cloudSeed = cloud * 1337
            val baseX = ((cloudSeed * 7) % 100) / 100f
            val baseY = ((cloudSeed * 13) % 100) / 100f

            val moveX = sin(nebulaFlow + cloud * 0.5f) * 0.05f
            val moveY = cos(nebulaFlow + cloud * 0.3f) * 0.05f

            val cloudX = (baseX + moveX) * size.width
            val cloudY = (baseY + moveY) * size.height
            val cloudRadius = size.minDimension * (0.2f + (cloud % 3) * 0.1f)

            val cloudColor = nebulaColors[cloud % nebulaColors.size]

            drawCircle(
                brush = Brush. radialGradient(
                    colors = listOf(
                        cloudColor. copy(alpha = 0.3f),
                        cloudColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(cloudX, cloudY),
                    radius = cloudRadius
                ),
                radius = cloudRadius,
                center = Offset(cloudX, cloudY)
            )
        }

        // Stars
        for (i in 0 until 80) {
            val starX = ((i * 1337) % 1000) / 1000f * size. width
            val starY = ((i * 7919) % 1000) / 1000f * size.height
            val starSize = if (i % 5 == 0) 2.5f else 1.5f
            val starAlpha = if (i % 3 == 0) starTwinkle else 0.7f

            drawCircle(
                color = Color.White. copy(alpha = starAlpha),
                radius = starSize,
                center = Offset(starX, starY)
            )

            // Star glow for larger stars
            if (i % 5 == 0) {
                drawCircle(
                    brush = Brush. radialGradient(
                        colors = listOf(
                            Color. White.copy(alpha = 0.3f * starAlpha),
                            Color. Transparent
                        ),
                        center = Offset(starX, starY),
                        radius = starSize * 4
                    ),
                    radius = starSize * 4,
                    center = Offset(starX, starY)
                )
            }
        }

        // Central bright star/galaxy core
        val centerX = size.width * 0.5f
        val centerY = size.height * 0.4f

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.9f),
                    Color(0xFFFFD700).copy(alpha = 0.5f),
                    Color(0xFF9400D3).copy(alpha = 0.2f),
                    Color. Transparent
                ),
                center = Offset(centerX, centerY),
                radius = size.minDimension * 0.15f
            ),
            radius = size. minDimension * 0.15f,
            center = Offset(centerX, centerY)
        )
    }
}

// ==================== MAIN MENU 17: LIQUID METAL (ANIMATED) ====================
@Composable
fun LiquidMetalMainMenu(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode. Restart
        ),
        label = "shimmer"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Dark metallic background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1A1A),
                    Color(0xFF2D2D2D),
                    Color(0xFF1A1A1A)
                )
            )
        )

        // Liquid metal waves
        for (layer in 0 until 5) {
            val layerPhase = wavePhase + layer * 0.5f
            val baseY = size. height * (0.3f + layer * 0.12f)

            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, baseY)

                for (x in 0..size.width. toInt() step 5) {
                    val xf = x. toFloat()
                    val wave1 = sin(layerPhase + xf / size.width * 4 * PI.toFloat()) * size.height * 0.02f
                    val wave2 = sin(layerPhase * 1.5f + xf / size.width * 6 * PI.toFloat()) * size.height * 0.01f
                    lineTo(xf, baseY + wave1 + wave2)
                }

                lineTo(size.width, size.height)
                close()
            }

            // Metallic gradient
            val metalColors = listOf(
                Color(0xFF3D3D3D),
                Color(0xFF6B6B6B),
                Color(0xFF8B8B8B),
                Color(0xFF6B6B6B),
                Color(0xFF3D3D3D)
            )

            drawPath(
                path = path,
                brush = Brush. verticalGradient(
                    colors = metalColors,
                    startY = baseY - size.height * 0.05f,
                    endY = size.height
                )
            )
        }

        // Shimmer effect
        val shimmerX = shimmer * size.width * 1.5f - size.width * 0.25f
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color.White.copy(alpha = 0.1f),
                    Color. White.copy(alpha = 0.2f),
                    Color. White.copy(alpha = 0.1f),
                    Color. Transparent
                ),
                startX = shimmerX - 100f,
                endX = shimmerX + 100f
            )
        )

        // Chrome highlights
        for (i in 0 until 8) {
            val highlightY = size.height * (0.35f + i * 0.08f)
            val highlightWidth = size.width * (0.1f + (i % 3) * 0.05f)
            val highlightX = ((i * 137) % 100) / 100f * (size.width - highlightWidth)

            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color. Transparent,
                        Color.White.copy(alpha = 0.3f),
                        Color. Transparent
                    ),
                    startX = highlightX,
                    endX = highlightX + highlightWidth
                ),
                start = Offset(highlightX, highlightY),
                end = Offset(highlightX + highlightWidth, highlightY),
                strokeWidth = 2f
            )
        }
    }
}

// ==================== MAIN MENU 18: FIRE & ICE (ANIMATED) ====================
@Composable
fun FireIceMainMenu(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "fireIce")
    val flameFlicker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame"
    )
    val iceShimmer by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "ice"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val midX = size.width / 2

        // Ice side (left)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF000033),
                    Color(0xFF001144),
                    Color(0xFF002255)
                ),
                startX = 0f,
                endX = midX
            ),
            topLeft = Offset. Zero,
            size = androidx.compose.ui. geometry.Size(midX, size.height)
        )

        // Fire side (right)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFF331100),
                    Color(0xFF441100),
                    Color(0xFF551100)
                ),
                startX = midX,
                endX = size.width
            ),
            topLeft = Offset(midX, 0f),
            size = androidx.compose.ui.geometry. Size(midX, size.height)
        )

        // Ice crystals
        for (i in 0 until 12) {
            val crystalX = (i % 4) * midX / 4 + midX / 8
            val crystalY = (i / 4) * size.height / 3 + size.height / 6
            val crystalSize = 20f + (i % 3) * 10f

            // Crystal shape
            for (ray in 0 until 6) {
                val rayAngle = (ray * 60f * PI / 180f).toFloat()
                drawLine(
                    color = Color(0xFF87CEEB).copy(alpha = iceShimmer * 0.8f),
                    start = Offset(crystalX, crystalY),
                    end = Offset(
                        crystalX + cos(rayAngle) * crystalSize,
                        crystalY + sin(rayAngle) * crystalSize
                    ),
                    strokeWidth = 2f
                )
            }

            // Crystal glow
            drawCircle(
                brush = Brush. radialGradient(
                    colors = listOf(
                        Color(0xFF87CEEB).copy(alpha = 0.3f * iceShimmer),
                        Color. Transparent
                    ),
                    center = Offset(crystalX, crystalY),
                    radius = crystalSize * 1.5f
                ),
                radius = crystalSize * 1.5f,
                center = Offset(crystalX, crystalY)
            )
        }

        // Fire flames
        for (i in 0 until 15) {
            val flameX = midX + (i % 5) * midX / 5 + midX / 10
            val flameBaseY = size.height - (i / 5) * size.height / 4
            val flameHeight = 40f + (i % 4) * 20f + flameFlicker * 15f
            val flickerOffset = sin(flameFlicker * PI.toFloat() * 2 + i) * 5f

            val flamePath = Path().apply {
                moveTo(flameX - 15f + flickerOffset, flameBaseY)
                quadraticBezierTo(
                    flameX - 8f + flickerOffset, flameBaseY - flameHeight * 0.5f,
                    flameX + flickerOffset * 0.5f, flameBaseY - flameHeight
                )
                quadraticBezierTo(
                    flameX + 8f - flickerOffset, flameBaseY - flameHeight * 0.5f,
                    flameX + 15f - flickerOffset, flameBaseY
                )
                close()
            }

            drawPath(
                path = flamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFF00),
                        Color(0xFFFF6600),
                        Color(0xFFFF0000).copy(alpha = 0.5f)
                    ),
                    startY = flameBaseY - flameHeight,
                    endY = flameBaseY
                )
            )
        }

        // Center clash line
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.8f),
                    Color(0xFF9370DB),
                    Color. White.copy(alpha = 0.8f)
                )
            ),
            start = Offset(midX, 0f),
            end = Offset(midX, size.height),
            strokeWidth = 4f
        )

        // Clash sparks
        for (i in 0 until 10) {
            val sparkY = (i * size.height / 10) + size.height / 20
            val sparkOffset = sin(flameFlicker * PI. toFloat() * 4 + i) * 10f

            drawCircle(
                color = Color.White. copy(alpha = 0.8f),
                radius = 3f,
                center = Offset(midX + sparkOffset, sparkY)
            )
        }
    }
}

// ==================== MAIN MENU 19: HONEYCOMB (STATIC) ====================
@Composable
fun HoneycombMainMenu(modifier:  Modifier = Modifier) {
    Canvas(modifier = modifier. fillMaxSize()) {
        // Dark amber background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A3000),
                    Color(0xFF2D1F00),
                    Color(0xFF1A1200)
                ),
                center = Offset(size.width / 2, size. height / 2),
                radius = size.maxDimension
            )
        )

        // Honeycomb pattern
        val hexRadius = size.width / 12
        val hexHeight = hexRadius * sqrt(3f)
        val hexWidth = hexRadius * 2

        val cols = (size.width / (hexWidth * 0.75f)).toInt() + 2
        val rows = (size.height / hexHeight).toInt() + 2

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val offsetX = if (row % 2 == 0) 0f else hexWidth * 0.75f
                val hexCenterX = col * hexWidth * 1.5f + offsetX
                val hexCenterY = row * hexHeight

                // Hexagon path
                val hexPath = Path().apply {
                    for (i in 0 until 6) {
                        val angle = (60 * i - 30) * PI. toFloat() / 180f
                        val x = hexCenterX + hexRadius * cos(angle)
                        val y = hexCenterY + hexRadius * sin(angle)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }

                // Random honey fill level
                val fillLevel = ((row * 7 + col * 13) % 100) / 100f

                // Hex cell
                drawPath(
                    path = hexPath,
                    color = Color(0xFF3D2800),
                    style = Stroke(width = 2f)
                )

                // Honey gradient inside
                if (fillLevel > 0.3f) {
                    drawCircle(
                        brush = Brush. radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = fillLevel * 0.8f),
                                Color(0xFFFF8C00).copy(alpha = fillLevel * 0.5f),
                                Color(0xFFB8860B).copy(alpha = fillLevel * 0.3f)
                            ),
                            center = Offset(hexCenterX, hexCenterY),
                            radius = hexRadius * 0.8f
                        ),
                        radius = hexRadius * 0.8f,
                        center = Offset(hexCenterX, hexCenterY)
                    )
                }

                // Highlight
                if (fillLevel > 0.5f) {
                    drawCircle(
                        color = Color.White. copy(alpha = 0.2f),
                        radius = hexRadius * 0.2f,
                        center = Offset(hexCenterX - hexRadius * 0.3f, hexCenterY - hexRadius * 0.3f)
                    )
                }
            }
        }
    }
}

// ==================== MAIN MENU 20: AURORA DREAMS (ANIMATED) ====================
@Composable
fun AuroraDreamsMainMenu(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val auroraWave by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )
    val colorCycle by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Night sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF000011),
                    Color(0xFF0D0D2B),
                    Color(0xFF1A1A3E),
                    Color(0xFF0D0D2B)
                )
            )
        )

        // Aurora curtains
        val auroraColors = listOf(
            Color(0xFF00FF7F),
            Color(0xFF00FFFF),
            Color(0xFF9400D3),
            Color(0xFFFF00FF),
            Color(0xFF00FF7F)
        )

        for (curtain in 0 until 8) {
            val curtainBaseX = curtain * size.width / 7 - size.width / 14

            for (y in 0.. size.height.toInt() step 3) {
                val yf = y.toFloat()
                val yRatio = yf / size.height

                val wave = sin(auroraWave + yRatio * 4 + curtain * 0.5f) * size.width * 0.05f
                val x = curtainBaseX + wave

                val colorIndex = ((colorCycle + curtain * 0.1f + yRatio * 0.5f) * 4).toInt() % 4
                val auroraColor = auroraColors[colorIndex]

                val alpha = (0.4f - yRatio * 0.3f).coerceIn(0f, 0.4f) *
                        (1f - abs(x - size.width / 2) / (size.width / 2)).coerceIn(0f, 1f)

                if (x >= 0 && x <= size.width && alpha > 0.01f) {
                    drawLine(
                        color = auroraColor. copy(alpha = alpha),
                        start = Offset(x, yf),
                        end = Offset(x, yf + 5f),
                        strokeWidth = 8f
                    )
                }
            }
        }

        // Stars
        for (i in 0 until 60) {
            val starX = ((i * 1337) % 1000) / 1000f * size.width
            val starY = ((i * 7919) % 1000) / 1000f * size. height * 0.6f
            val starAlpha = 0.5f + ((i * 31) % 50) / 100f

            drawCircle(
                color = Color.White. copy(alpha = starAlpha),
                radius = if (i % 4 == 0) 2f else 1f,
                center = Offset(starX, starY)
            )
        }

        // Silhouette mountains
        val mountainPath = Path().apply {
            moveTo(0f, size. height)
            lineTo(0f, size. height * 0.75f)
            lineTo(size.width * 0.15f, size.height * 0.6f)
            lineTo(size.width * 0.3f, size.height * 0.72f)
            lineTo(size.width * 0.45f, size.height * 0.55f)
            lineTo(size.width * 0.6f, size.height * 0.68f)
            lineTo(size.width * 0.75f, size.height * 0.52f)
            lineTo(size.width * 0.9f, size.height * 0.65f)
            lineTo(size.width, size.height * 0.58f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path = mountainPath, color = Color(0xFF0A0A15))

        // Snow on peaks
        val snowPath = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.6f)
            lineTo(size.width * 0.12f, size.height * 0.65f)
            lineTo(size.width * 0.18f, size.height * 0.65f)
            close()
        }
        drawPath(path = snowPath, color = Color. White.copy(alpha = 0.3f))
    }
}