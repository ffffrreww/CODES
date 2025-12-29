package com.appsdevs.popit

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose. foundation.layout. fillMaxSize
import androidx.compose.runtime. Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry. Offset
import androidx.compose.ui. graphics.*
import androidx.compose.ui.graphics.drawscope.*
import kotlin.math.*

// ==================== GENERATED BACKGROUNDS (CODE-BASED) ====================

// ==================== BACKGROUND 12:  STARFIELD (ANIMATED) ====================
@Composable
fun StarfieldBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "starfield")
    val starMove by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode. Restart
        ),
        label = "move"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep space gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF000011),
                    Color(0xFF0D0D2B),
                    Color(0xFF1A0A2E),
                    Color(0xFF0D0D2B),
                    Color(0xFF000011)
                )
            )
        )

        // Stars layers
        val starCount = 150
        for (i in 0 until starCount) {
            val seed = i * 1337
            val baseX = ((seed * 7) % 1000) / 1000f
            val baseY = ((seed * 13) % 1000) / 1000f
            val layer = i % 3

            val parallax = when (layer) {
                0 -> 0.1f
                1 -> 0.3f
                else -> 0.6f
            }

            val starX = ((baseX + starMove * parallax) % 1f) * size.width
            val starY = baseY * size.height
            val starSize = when (layer) {
                0 -> 1f
                1 -> 1.5f
                else -> 2.5f
            }

            val starAlpha = if (i % 5 == 0) twinkle else 0.7f + (i % 3) * 0.1f
            val starColor = when (i % 7) {
                0 -> Color(0xFFFFFFFF)
                1 -> Color(0xFFFFE4C4)
                2 -> Color(0xFFADD8E6)
                3 -> Color(0xFFFFB6C1)
                else -> Color(0xFFFFFFFF)
            }

            drawCircle(
                color = starColor. copy(alpha = starAlpha),
                radius = starSize,
                center = Offset(starX, starY)
            )

            // Glow for bigger stars
            if (layer == 2 && i % 3 == 0) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            starColor.copy(alpha = 0.3f),
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

        // Nebula overlay
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF9400D3).copy(alpha = 0.1f),
                    Color. Transparent
                ),
                center = Offset(size.width * 0.3f, size.height * 0.4f),
                radius = size.width * 0.4f
            ),
            radius = size.width * 0.4f,
            center = Offset(size.width * 0.3f, size.height * 0.4f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00CED1).copy(alpha = 0.08f),
                    Color. Transparent
                ),
                center = Offset(size.width * 0.7f, size.height * 0.6f),
                radius = size.width * 0.35f
            ),
            radius = size. width * 0.35f,
            center = Offset(size.width * 0.7f, size.height * 0.6f)
        )
    }
}

// ==================== BACKGROUND 13: OCEAN WAVES (ANIMATED) ====================
@Composable
fun OceanWavesBackground(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ocean")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI. toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB),
                    Color(0xFF00BFFF),
                    Color(0xFF1E90FF)
                ),
                startY = 0f,
                endY = size.height * 0.4f
            )
        )

        // Ocean gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1E90FF),
                    Color(0xFF0066CC),
                    Color(0xFF000080),
                    Color(0xFF00004D)
                ),
                startY = size.height * 0.35f,
                endY = size.height
            )
        )

        // Sun
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFF99),
                    Color(0xFFFFD700),
                    Color(0xFFFFA500).copy(alpha = 0.5f),
                    Color. Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.15f),
                radius = size.width * 0.15f
            ),
            radius = size. width * 0.15f,
            center = Offset(size.width * 0.8f, size.height * 0.15f)
        )

        // Wave layers
        for (layer in 0 until 5) {
            val layerY = size.height * (0.4f + layer * 0.12f)
            val waveAmplitude = size.height * (0.03f - layer * 0.004f)
            val waveFrequency = 3f + layer * 0.5f
            val phaseOffset = layer * 0.5f
            val layerAlpha = 0.6f + layer * 0.08f

            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, layerY)

                for (x in 0.. size.width. toInt() step 5) {
                    val xf = x. toFloat()
                    val waveY = layerY + sin(wavePhase + phaseOffset + xf / size.width * waveFrequency * PI.toFloat()) * waveAmplitude
                    lineTo(xf, waveY)
                }

                lineTo(size.width, size.height)
                close()
            }

            val waveColor = when (layer) {
                0 -> Color(0xFF1E90FF)
                1 -> Color(0xFF0066CC)
                2 -> Color(0xFF0055AA)
                3 -> Color(0xFF004488)
                else -> Color(0xFF003366)
            }

            drawPath(
                path = path,
                color = waveColor. copy(alpha = layerAlpha)
            )

            // Wave foam
            if (layer < 2) {
                for (x in 0.. size.width.toInt() step 20) {
                    val xf = x.toFloat()
                    val waveY = layerY + sin(wavePhase + phaseOffset + xf / size.width * waveFrequency * PI.toFloat()) * waveAmplitude
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f - layer * 0.1f),
                        radius = 3f,
                        center = Offset(xf, waveY)
                    )
                }
            }
        }
    }
}

// ==================== BACKGROUND 14: FOREST (STATIC) ====================
@Composable
fun ForestBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier. fillMaxSize()) {
        // Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB),
                    Color(0xFFB0E0E6),
                    Color(0xFFADD8E6)
                ),
                startY = 0f,
                endY = size.height * 0.5f
            )
        )

        // Distant mountains/hills
        val hillPath = Path().apply {
            moveTo(0f, size.height * 0.5f)
            quadraticBezierTo(size.width * 0.2f, size.height * 0.35f, size.width * 0.4f, size.height * 0.45f)
            quadraticBezierTo(size.width * 0.6f, size.height * 0.55f, size.width * 0.8f, size.height * 0.4f)
            quadraticBezierTo(size.width * 0.9f, size.height * 0.35f, size.width, size.height * 0.42f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = hillPath, color = Color(0xFF228B22).copy(alpha = 0.4f))

        // Middle forest layer
        val midForestPath = Path().apply {
            moveTo(0f, size.height * 0.55f)
            for (i in 0..20) {
                val x = i * size.width / 20
                val peakHeight = size.height * (0.45f + (i % 3) * 0.05f)
                val valleyHeight = size. height * 0.55f
                if (i % 2 == 0) {
                    lineTo(x, peakHeight)
                } else {
                    lineTo(x, valleyHeight)
                }
            }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = midForestPath, color = Color(0xFF228B22).copy(alpha = 0.6f))

        // Front forest layer
        val frontForestPath = Path().apply {
            moveTo(0f, size.height * 0.65f)
            for (i in 0..30) {
                val x = i * size.width / 30
                val peakHeight = size.height * (0.55f + (i % 4) * 0.03f)
                val valleyHeight = size.height * 0.65f
                if (i % 2 == 0) {
                    lineTo(x, peakHeight)
                } else {
                    lineTo(x, valleyHeight)
                }
            }
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = frontForestPath, color = Color(0xFF006400))

        // Ground
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF228B22),
                    Color(0xFF006400),
                    Color(0xFF004D00)
                ),
                startY = size.height * 0.7f,
                endY = size.height
            )
        )

        // Tree silhouettes
        for (i in 0 until 8) {
            val treeX = size.width * (0.1f + i * 0.11f)
            val treeHeight = size.height * (0.15f + (i % 3) * 0.08f)
            val treeBaseY = size.height * 0.7f

            // Tree trunk
            drawRect(
                color = Color(0xFF4A3728),
                topLeft = Offset(treeX - 5f, treeBaseY - treeHeight * 0.3f),
                size = androidx.compose.ui. geometry.Size(10f, treeHeight * 0.4f)
            )

            // Tree foliage (triangle)
            val treePath = Path().apply {
                moveTo(treeX, treeBaseY - treeHeight)
                lineTo(treeX - treeHeight * 0.4f, treeBaseY - treeHeight * 0.2f)
                lineTo(treeX + treeHeight * 0.4f, treeBaseY - treeHeight * 0.2f)
                close()
            }
            drawPath(path = treePath, color = Color(0xFF004D00))
        }

        // Sunlight rays
        for (i in 0 until 5) {
            val rayX = size. width * (0.2f + i * 0.15f)
            drawLine(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.1f),
                        Color. Transparent
                    )
                ),
                start = Offset(rayX, 0f),
                end = Offset(rayX + 20f, size.height * 0.5f),
                strokeWidth = 30f
            )
        }
    }
}

// ==================== BACKGROUND 15: AURORA BOREALIS (ANIMATED) ====================
@Composable
fun AuroraBorealisBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val auroraWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )
    val colorShift by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Night sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF000022),
                    Color(0xFF0D0D2B),
                    Color(0xFF1A1A3E)
                )
            )
        )

        // Stars
        for (i in 0 until 100) {
            val starX = ((i * 1337) % 1000) / 1000f * size.width
            val starY = ((i * 7919) % 1000) / 1000f * size.height * 0.7f
            drawCircle(
                color = Color.White.copy(alpha = 0.6f + (i % 4) * 0.1f),
                radius = if (i % 5 == 0) 2f else 1f,
                center = Offset(starX, starY)
            )
        }

        // Aurora bands
        val auroraColors = listOf(
            Color(0xFF00FF7F),
            Color(0xFF00FFFF),
            Color(0xFF9400D3),
            Color(0xFFFF00FF)
        )

        for (band in 0 until 6) {
            val bandBaseY = size.height * (0.15f + band * 0.08f)
            val bandPhase = auroraWave + band * 0.3f

            for (x in 0..size.width.toInt() step 8) {
                val xf = x. toFloat()
                val waveY = bandBaseY + sin(bandPhase + xf / size.width * 4 * PI.toFloat()) * size.height * 0.08f
                val waveY2 = bandBaseY + size.height * 0.1f + sin(bandPhase + 0.5f + xf / size. width * 4 * PI.toFloat()) * size.height * 0.05f

                val colorIndex = ((colorShift * 4 + band * 0.5f).toInt() % 4)
                val auroraColor = auroraColors[colorIndex]

                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            auroraColor.copy(alpha = 0.4f - band * 0.05f),
                            auroraColor. copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        startY = waveY,
                        endY = waveY2 + size.height * 0.15f
                    ),
                    start = Offset(xf, waveY),
                    end = Offset(xf, waveY2 + size.height * 0.15f),
                    strokeWidth = 8f
                )
            }
        }

        // Snow-covered ground
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1A3E),
                    Color(0xFF2F4F4F),
                    Color(0xFFE8E8E8)
                ),
                startY = size.height * 0.75f,
                endY = size.height
            )
        )

        // Snow hills
        val snowPath = Path().apply {
            moveTo(0f, size. height * 0.85f)
            quadraticBezierTo(size.width * 0.25f, size.height * 0.8f, size.width * 0.5f, size.height * 0.87f)
            quadraticBezierTo(size.width * 0.75f, size.height * 0.82f, size.width, size.height * 0.85f)
            lineTo(size.width, size.height)
            lineTo(0f, size. height)
            close()
        }
        drawPath(path = snowPath, color = Color(0xFFF0F8FF))
    }
}

// ==================== BACKGROUND 16: VOLCANIC (ANIMATED) ====================
@Composable
fun VolcanicBackground(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "volcanic")
    val lavaFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lava"
    )
    val emberRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ember"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Dark smoky sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A0000),
                    Color(0xFF330000),
                    Color(0xFF4D0000),
                    Color(0xFF660000)
                )
            )
        )

        // Smoke clouds
        for (i in 0 until 8) {
            val cloudX = size.width * (0.1f + i * 0.12f)
            val cloudY = size. height * (0.1f + (i % 3) * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2F2F2F).copy(alpha = 0.4f),
                        Color. Transparent
                    ),
                    center = Offset(cloudX, cloudY),
                    radius = size.width * 0.15f
                ),
                radius = size. width * 0.15f,
                center = Offset(cloudX, cloudY)
            )
        }

        // Volcanic mountain
        val mountainPath = Path().apply {
            moveTo(size.width * 0.2f, size.height)
            lineTo(size.width * 0.45f, size.height * 0.35f)
            lineTo(size.width * 0.5f, size.height * 0.4f) // Crater dip
            lineTo(size.width * 0.55f, size.height * 0.35f)
            lineTo(size.width * 0.8f, size.height)
            close()
        }
        drawPath(
            path = mountainPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2F1F1F),
                    Color(0xFF1A0A0A)
                )
            )
        )

        // Lava glow from crater
        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFFFF4500).copy(alpha = 0.8f),
                    Color(0xFFFF0000).copy(alpha = 0.4f),
                    Color. Transparent
                ),
                center = Offset(size.width * 0.5f, size.height * 0.38f),
                radius = size.width * 0.15f
            ),
            radius = size. width * 0.15f,
            center = Offset(size.width * 0.5f, size.height * 0.38f)
        )

        // Lava streams
        for (stream in 0 until 3) {
            val streamX = size. width * (0.42f + stream * 0.08f)
            val streamPhase = (lavaFlow + stream * 0.3f) % 1f

            for (drop in 0 until 8) {
                val dropPhase = (streamPhase + drop * 0.12f) % 1f
                val dropY = size.height * 0.4f + dropPhase * size.height * 0.5f
                val dropX = streamX + sin(dropPhase * PI.toFloat() * 2) * 10f
                val dropAlpha = (1f - dropPhase * 0.8f).coerceIn(0f, 1f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = dropAlpha),
                            Color(0xFFFF4500).copy(alpha = dropAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(dropX, dropY),
                        radius = 15f
                    ),
                    radius = 15f,
                    center = Offset(dropX, dropY)
                )
            }
        }

        // Rising embers
        for (i in 0 until 20) {
            val emberPhase = (emberRise + i * 0.05f) % 1f
            val emberX = size.width * (0.35f + (i % 6) * 0.05f) + sin(emberPhase * PI.toFloat() * 3) * 20f
            val emberY = size.height * 0.4f - emberPhase * size. height * 0.35f
            val emberAlpha = (1f - emberPhase).coerceIn(0f, 1f)

            if (emberY > 0) {
                drawCircle(
                    color = Color(0xFFFF6600).copy(alpha = emberAlpha * 0.8f),
                    radius = 3f - emberPhase * 2f,
                    center = Offset(emberX, emberY)
                )
            }
        }

        // Lava pool at bottom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF4500),
                    Color(0xFFFF0000),
                    Color(0xFF8B0000)
                ),
                startY = size.height * 0.9f,
                endY = size.height
            )
        )
    }
}

// ==================== BACKGROUND 17: CYBERPUNK CITY (ANIMATED) ====================
@Composable
fun CyberpunkCityBackground(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyberpunk")
    val neonPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rainDrop by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain"
    )

    Canvas(modifier = modifier. fillMaxSize()) {
        // Dark sky with purple haze
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A1A),
                    Color(0xFF1A0A2E),
                    Color(0xFF2D1B4E)
                )
            )
        )

        // City buildings silhouette
        val buildingHeights = listOf(0.4f, 0.6f, 0.5f, 0.75f, 0.55f, 0.8f, 0.45f, 0.7f, 0.5f, 0.65f)
        val buildingWidth = size.width / buildingHeights. size

        buildingHeights.forEachIndexed { index, heightRatio ->
            val buildingX = index * buildingWidth
            val buildingHeight = size.height * heightRatio
            val buildingTop = size.height - buildingHeight

            // Building body
            drawRect(
                color = Color(0xFF0D0D1A),
                topLeft = Offset(buildingX + 2f, buildingTop),
                size = androidx.compose.ui. geometry.Size(buildingWidth - 4f, buildingHeight)
            )

            // Windows with neon glow
            val windowRows = (buildingHeight / 30).toInt()
            val windowCols = 3
            for (row in 0 until windowRows) {
                for (col in 0 until windowCols) {
                    val windowX = buildingX + 8f + col * (buildingWidth - 16f) / windowCols
                    val windowY = buildingTop + 15f + row * 28f
                    val windowOn = (index + row + col) % 3 != 0

                    if (windowOn) {
                        val windowColor = when ((index + row) % 4) {
                            0 -> Color(0xFFFF00FF)
                            1 -> Color(0xFF00FFFF)
                            2 -> Color(0xFFFFFF00)
                            else -> Color(0xFFFF6600)
                        }

                        // Window glow
                        drawRect(
                            color = windowColor. copy(alpha = 0.3f * neonPulse),
                            topLeft = Offset(windowX - 2f, windowY - 2f),
                            size = androidx.compose. ui.geometry.Size(12f, 18f)
                        )
                        // Window
                        drawRect(
                            color = windowColor. copy(alpha = 0.8f),
                            topLeft = Offset(windowX, windowY),
                            size = androidx. compose.ui.geometry.Size(8f, 14f)
                        )
                    }
                }
            }

            // Neon signs on some buildings
            if (index % 3 == 0) {
                val signColor = if (index % 2 == 0) Color(0xFFFF00FF) else Color(0xFF00FFFF)
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            signColor.copy(alpha = 0.8f * neonPulse),
                            signColor.copy(alpha = 0.4f * neonPulse)
                        )
                    ),
                    topLeft = Offset(buildingX + 5f, buildingTop + buildingHeight * 0.3f),
                    size = androidx.compose.ui.geometry. Size(buildingWidth - 10f, 8f)
                )
            }
        }

        // Rain effect
        for (i in 0 until 50) {
            val rainX = ((i * 73) % 100) / 100f * size.width
            val rainBaseY = ((i * 137) % 100) / 100f
            val rainY = ((rainBaseY + rainDrop) % 1f) * size.height

            drawLine(
                color = Color(0xFF87CEEB).copy(alpha = 0.3f),
                start = Offset(rainX, rainY),
                end = Offset(rainX + 1f, rainY + 15f),
                strokeWidth = 1f
            )
        }

        // Ground reflection
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A0A2E).copy(alpha = 0.8f),
                    Color(0xFF0A0A1A)
                ),
                startY = size.height * 0.9f,
                endY = size.height
            )
        )

        // Neon reflection lines on ground
        for (i in 0 until 5) {
            val lineY = size.height * (0.92f + i * 0.02f)
            val lineColor = if (i % 2 == 0) Color(0xFFFF00FF) else Color(0xFF00FFFF)
            drawLine(
                color = lineColor. copy(alpha = 0.2f * neonPulse),
                start = Offset(0f, lineY),
                end = Offset(size.width, lineY),
                strokeWidth = 2f
            )
        }
    }
}

// ==================== BACKGROUND 18: UNDERWATER (ANIMATED) ====================
@Composable
fun UnderwaterBackground(modifier:  Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "underwater")
    val bubbleRise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble"
    )
    val seaweedSway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "sway"
    )
    val lightRay by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "light"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep water gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00CED1),
                    Color(0xFF008B8B),
                    Color(0xFF006666),
                    Color(0xFF004D4D),
                    Color(0xFF003333)
                )
            )
        )

        // Light rays from surface
        for (i in 0 until 5) {
            val rayX = size. width * (0.15f + i * 0.18f)
            val rayWidth = size.width * 0.08f

            val rayPath = Path().apply {
                moveTo(rayX, 0f)
                lineTo(rayX + rayWidth, 0f)
                lineTo(rayX + rayWidth * 1.5f, size.height * 0.7f)
                lineTo(rayX - rayWidth * 0.5f, size.height * 0.7f)
                close()
            }

            drawPath(
                path = rayPath,
                brush = Brush. verticalGradient(
                    colors = listOf(
                        Color(0xFFADD8E6).copy(alpha = lightRay),
                        Color. Transparent
                    )
                )
            )
        }

        // Seaweed
        for (i in 0 until 12) {
            val seaweedX = size.width * (0.05f + i * 0.08f)
            val seaweedHeight = size.height * (0.2f + (i % 3) * 0.1f)
            val swayOffset = seaweedSway * 15f * sin(i. toFloat())

            val seaweedPath = Path().apply {
                moveTo(seaweedX, size.height)
                quadraticBezierTo(
                    seaweedX + swayOffset,
                    size.height - seaweedHeight * 0.5f,
                    seaweedX + swayOffset * 0.5f,
                    size.height - seaweedHeight
                )
            }

            drawPath(
                path = seaweedPath,
                color = Color(0xFF006400).copy(alpha = 0.7f),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }

        // Rising bubbles
        for (i in 0 until 15) {
            val bubblePhase = (bubbleRise + i * 0.07f) % 1f
            val bubbleX = size.width * (0.1f + (i % 8) * 0.1f) + sin(bubblePhase * PI. toFloat() * 2) * 10f
            val bubbleY = size.height * (1f - bubblePhase)
            val bubbleSize = 5f + (i % 4) * 3f
            val bubbleAlpha = 0.3f + (1f - bubblePhase) * 0.3f

            // Bubble
            drawCircle(
                color = Color. White.copy(alpha = bubbleAlpha),
                radius = bubbleSize,
                center = Offset(bubbleX, bubbleY),
                style = Stroke(width = 1.5f)
            )

            // Bubble highlight
            drawCircle(
                color = Color.White.copy(alpha = bubbleAlpha * 0.8f),
                radius = bubbleSize * 0.3f,
                center = Offset(bubbleX - bubbleSize * 0.3f, bubbleY - bubbleSize * 0.3f)
            )
        }

        // Sandy bottom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF003333),
                    Color(0xFFD2B48C),
                    Color(0xFFC4A35A)
                ),
                startY = size.height * 0.85f,
                endY = size.height
            )
        )

        // Small fish silhouettes
        for (i in 0 until 5) {
            val fishX = (size.width * (0.2f + i * 0.15f) + bubbleRise * size.width * 0.3f) % size.width
            val fishY = size.height * (0.3f + (i % 3) * 0.15f)
            val fishSize = 15f + (i % 2) * 10f

            // Simple fish shape
            val fishPath = Path().apply {
                moveTo(fishX, fishY)
                lineTo(fishX + fishSize, fishY - fishSize * 0.3f)
                lineTo(fishX + fishSize, fishY + fishSize * 0.3f)
                close()
                // Tail
                moveTo(fishX, fishY)
                lineTo(fishX - fishSize * 0.5f, fishY - fishSize * 0.2f)
                lineTo(fishX - fishSize * 0.5f, fishY + fishSize * 0.2f)
                close()
            }

            drawPath(
                path = fishPath,
                color = Color(0xFFFF6347).copy(alpha = 0.6f)
            )
        }
    }
}

// ==================== BACKGROUND 19: DESERT DUNES (STATIC) ====================
@Composable
fun DesertDunesBackground(modifier:  Modifier = Modifier) {
    Canvas(modifier = modifier. fillMaxSize()) {
        // Sky gradient (hot desert sky)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF87CEEB),
                    Color(0xFFFFE4B5),
                    Color(0xFFFFD700)
                ),
                startY = 0f,
                endY = size. height * 0.5f
            )
        )

        // Sun
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFE0),
                    Color(0xFFFFD700),
                    Color(0xFFFFA500).copy(alpha = 0.3f),
                    Color. Transparent
                ),
                center = Offset(size.width * 0.75f, size.height * 0.15f),
                radius = size.width * 0.12f
            ),
            radius = size.width * 0.12f,
            center = Offset(size.width * 0.75f, size.height * 0.15f)
        )

        // Heat haze effect
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color(0xFFFFD700).copy(alpha = 0.1f),
                    Color. Transparent
                ),
                startY = size.height * 0.4f,
                endY = size.height * 0.55f
            )
        )

        // Distant dunes (background)
        val distantDunePath = Path().apply {
            moveTo(0f, size.height * 0.55f)
            quadraticBezierTo(size.width * 0.2f, size.height * 0.45f, size.width * 0.35f, size.height * 0.52f)
            quadraticBezierTo(size.width * 0.5f, size.height * 0.48f, size.width * 0.7f, size.height * 0.53f)
            quadraticBezierTo(size.width * 0.85f, size.height * 0.46f, size.width, size.height * 0.5f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = distantDunePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFDEB887),
                    Color(0xFFD2B48C)
                )
            )
        )

        // Middle dunes
        val middleDunePath = Path().apply {
            moveTo(0f, size.height * 0.65f)
            quadraticBezierTo(size.width * 0.15f, size.height * 0.55f, size.width * 0.3f, size.height * 0.62f)
            quadraticBezierTo(size.width * 0.45f, size.height * 0.58f, size.width * 0.6f, size.height * 0.65f)
            quadraticBezierTo(size.width * 0.8f, size.height * 0.56f, size.width, size.height * 0.6f)
            lineTo(size.width, size.height)
            lineTo(0f, size. height)
            close()
        }
        drawPath(
            path = middleDunePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE3C16F),
                    Color(0xFFCDAA6D)
                )
            )
        )

        // Front dunes
        val frontDunePath = Path().apply {
            moveTo(0f, size.height * 0.75f)
            quadraticBezierTo(size.width * 0.25f, size.height * 0.65f, size.width * 0.4f, size.height * 0.72f)
            quadraticBezierTo(size.width * 0.55f, size.height * 0.68f, size.width * 0.75f, size.height * 0.75f)
            quadraticBezierTo(size.width * 0.9f, size.height * 0.66f, size.width, size.height * 0.7f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path = frontDunePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF4A460),
                    Color(0xFFCD853F)
                )
            )
        )

        // Sand texture dots
        for (i in 0 until 100) {
            val dotX = ((i * 97) % 1000) / 1000f * size. width
            val dotY = size.height * (0.6f + ((i * 31) % 400) / 1000f)
            drawCircle(
                color = Color(0xFF8B7355).copy(alpha = 0.3f),
                radius = 1f,
                center = Offset(dotX, dotY)
            )
        }

        // Cactus silhouettes
        for (i in 0 until 3) {
            val cactusX = size.width * (0.15f + i * 0.35f)
            val cactusBaseY = size.height * (0.72f - i * 0.02f)
            val cactusHeight = size.height * (0.08f + (i % 2) * 0.04f)

            // Main stem
            drawRect(
                color = Color(0xFF2F4F2F),
                topLeft = Offset(cactusX - 8f, cactusBaseY - cactusHeight),
                size = androidx.compose.ui. geometry.Size(16f, cactusHeight)
            )

            // Arms
            if (i % 2 == 0) {
                drawRect(
                    color = Color(0xFF2F4F2F),
                    topLeft = Offset(cactusX + 8f, cactusBaseY - cactusHeight * 0.6f),
                    size = androidx.compose.ui.geometry. Size(15f, 8f)
                )
                drawRect(
                    color = Color(0xFF2F4F2F),
                    topLeft = Offset(cactusX + 18f, cactusBaseY - cactusHeight * 0.8f),
                    size = androidx.compose.ui.geometry. Size(8f, cactusHeight * 0.25f)
                )
            }
        }
    }
}

// ==================== BACKGROUND 20: CANDY LAND (STATIC) ====================
@Composable
fun CandyLandBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        // Pastel sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFB6C1),
                    Color(0xFFFFE4E1),
                    Color(0xFFE6E6FA)
                )
            )
        )

        // Candy clouds
        val cloudPositions = listOf(
            Offset(0.15f, 0.12f), Offset(0.45f, 0.08f), Offset(0.75f, 0.15f),
            Offset(0.25f, 0.22f), Offset(0.6f, 0.2f)
        )
        cloudPositions.forEachIndexed { index, pos ->
            val cloudX = pos.x * size.width
            val cloudY = pos.y * size. height
            val cloudColor = when (index % 3) {
                0 -> Color(0xFFFFFFFF)
                1 -> Color(0xFFFFE4E1)
                else -> Color(0xFFE6E6FA)
            }

            // Cloud circles
            drawCircle(color = cloudColor, radius = size.width * 0.06f, center = Offset(cloudX, cloudY))
            drawCircle(color = cloudColor, radius = size.width * 0.05f, center = Offset(cloudX - size.width * 0.04f, cloudY))
            drawCircle(color = cloudColor, radius = size. width * 0.045f, center = Offset(cloudX + size.width * 0.045f, cloudY))
        }

        // Candy cane striped hills
        val hillPath = Path().apply {
            moveTo(0f, size. height * 0.6f)
            quadraticBezierTo(size.width * 0.25f, size.height * 0.45f, size.width * 0.5f, size.height * 0.55f)
            quadraticBezierTo(size.width * 0.75f, size.height * 0.48f, size.width, size.height * 0.58f)
            lineTo(size.width, size.height)
            lineTo(0f, size. height)
            close()
        }
        drawPath(path = hillPath, color = Color(0xFF98FB98))

        // Candy stripe pattern on hills
        for (i in 0 until 20) {
            val stripeX = i * size.width / 10
            val stripeColor = if (i % 2 == 0) Color(0xFFFF69B4).copy(alpha = 0.3f) else Color(0xFFFFFFFF).copy(alpha = 0.2f)
            drawLine(
                color = stripeColor,
                start = Offset(stripeX, size.height * 0.5f),
                end = Offset(stripeX + size. width * 0.05f, size.height),
                strokeWidth = size.width * 0.03f
            )
        }

        // Lollipop trees
        val lollipopColors = listOf(Color(0xFFFF69B4), Color(0xFF00CED1), Color(0xFFFFD700), Color(0xFF9370DB))
        for (i in 0 until 6) {
            val lolliX = size.width * (0.1f + i * 0.15f)
            val lolliBaseY = size.height * (0.65f - (i % 2) * 0.05f)
            val lolliHeight = size.height * 0.2f
            val lolliColor = lollipopColors[i % lollipopColors. size]

            // Stick
            drawRect(
                color = Color.White,
                topLeft = Offset(lolliX - 4f, lolliBaseY - lolliHeight),
                size = androidx.compose.ui.geometry. Size(8f, lolliHeight)
            )

            // Candy top
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(lolliColor, Color. White, lolliColor, Color.White, lolliColor),
                    center = Offset(lolliX, lolliBaseY - lolliHeight - size.width * 0.05f)
                ),
                radius = size.width * 0.06f,
                center = Offset(lolliX, lolliBaseY - lolliHeight - size.width * 0.05f)
            )
        }

        // Gumdrop path
        val gumdropColors = listOf(Color(0xFFFF6347), Color(0xFF32CD32), Color(0xFFFFD700), Color(0xFF9370DB), Color(0xFFFF69B4))
        for (i in 0 until 8) {
            val gumdropX = size.width * (0.08f + i * 0.12f)
            val gumdropY = size. height * 0.88f
            val gumdropColor = gumdropColors[i % gumdropColors.size]

            // Gumdrop shape
            val gumdropPath = Path().apply {
                moveTo(gumdropX - 15f, gumdropY)
                quadraticBezierTo(gumdropX - 18f, gumdropY - 25f, gumdropX, gumdropY - 30f)
                quadraticBezierTo(gumdropX + 18f, gumdropY - 25f, gumdropX + 15f, gumdropY)
                close()
            }
            drawPath(path = gumdropPath, color = gumdropColor)

            // Highlight
            drawCircle(
                color = Color.White. copy(alpha = 0.5f),
                radius = 5f,
                center = Offset(gumdropX - 5f, gumdropY - 20f)
            )
        }

        // Ground (chocolate path)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF8B4513),
                    Color(0xFF5D3A1A)
                ),
                startY = size.height * 0.92f,
                endY = size.height
            )
        )
    }
}

// ==================== BACKGROUND 21: RETRO GRID (ANIMATED) ====================
@Composable
fun RetroGridBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "retro")
    val gridMove by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid"
    )
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Gradient sky (synthwave colors)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0D0221),
                    Color(0xFF2D1B4E),
                    Color(0xFF562B6E),
                    Color(0xFFFF6B6B),
                    Color(0xFFFFE66D)
                )
            )
        )

        // Retro sun
        val sunCenter = Offset(size.width * 0.5f, size.height * 0.4f)
        val sunRadius = size. width * 0.2f * sunPulse

        // Sun glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFE66D).copy(alpha = 0.5f),
                    Color(0xFFFF6B6B).copy(alpha = 0.3f),
                    Color. Transparent
                ),
                center = sunCenter,
                radius = sunRadius * 1.5f
            ),
            radius = sunRadius * 1.5f,
            center = sunCenter
        )

        // Sun body with horizontal lines
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE66D),
                    Color(0xFFFF6B6B)
                ),
                startY = sunCenter.y - sunRadius,
                endY = sunCenter.y + sunRadius
            ),
            radius = sunRadius,
            center = sunCenter
        )

        // Sun stripes
        for (i in 0 until 8) {
            val stripeY = sunCenter.y - sunRadius + i * sunRadius * 0.28f
            if (stripeY > sunCenter.y - sunRadius && stripeY < sunCenter. y + sunRadius) {
                val halfWidth = sqrt(sunRadius * sunRadius - (stripeY - sunCenter.y) * (stripeY - sunCenter.y))
                if (i % 2 == 0) {
                    drawLine(
                        color = Color(0xFF0D0221),
                        start = Offset(sunCenter.x - halfWidth, stripeY),
                        end = Offset(sunCenter.x + halfWidth, stripeY),
                        strokeWidth = sunRadius * 0.08f
                    )
                }
            }
        }

        // Grid floor
        val horizonY = size.height * 0.55f

        // Floor gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF562B6E),
                    Color(0xFF0D0221)
                ),
                startY = horizonY,
                endY = size.height
            )
        )

        // Horizontal grid lines (with perspective)
        for (i in 0 until 15) {
            val lineProgress = i / 14f
            val perspectiveY = horizonY + (size.height - horizonY) * lineProgress * lineProgress
            val lineAlpha = 0.3f + lineProgress * 0.4f

            // Animate lines moving towards viewer
            val animatedProgress = ((lineProgress + gridMove) % 1f)
            val animatedY = horizonY + (size.height - horizonY) * animatedProgress * animatedProgress

            drawLine(
                color = Color(0xFFFF00FF).copy(alpha = lineAlpha),
                start = Offset(0f, animatedY),
                end = Offset(size.width, animatedY),
                strokeWidth = 2f
            )
        }

        // Vertical grid lines (with perspective)
        val numVertLines = 20
        for (i in 0.. numVertLines) {
            val xRatio = i. toFloat() / numVertLines
            val topX = size.width * xRatio
            val bottomSpread = 2f // How much lines spread at bottom
            val bottomX = size.width * 0.5f + (xRatio - 0.5f) * size.width * bottomSpread

            drawLine(
                color = Color(0xFF00FFFF).copy(alpha = 0.5f),
                start = Offset(topX, horizonY),
                end = Offset(bottomX, size.height),
                strokeWidth = 2f
            )
        }

        // Mountains silhouette
        val mountainPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(size.width * 0.15f, horizonY - size.height * 0.08f)
            lineTo(size.width * 0.25f, horizonY - size.height * 0.02f)
            lineTo(size.width * 0.4f, horizonY - size.height * 0.12f)
            lineTo(size.width * 0.55f, horizonY - size.height * 0.05f)
            lineTo(size.width * 0.7f, horizonY - size.height * 0.15f)
            lineTo(size.width * 0.85f, horizonY - size.height * 0.06f)
            lineTo(size.width, horizonY - size.height * 0.1f)
            lineTo(size.width, horizonY)
            close()
        }
        drawPath(path = mountainPath, color = Color(0xFF1A0A2E))
    }
}