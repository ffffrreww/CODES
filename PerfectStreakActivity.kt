package com.appsdevs.popit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appsdevs.popit.ui.theme.PopITTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

class PerfectStreakActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat. BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } catch (_: Exception) { }

        enableEdgeToEdge()

        // Initialize music and sound
        MusicController.initIfNeeded(applicationContext)
        SoundManager.init(applicationContext)

        setContent {
            PopITTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PerfectStreakScreen(onExit = { finish() })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicController.stopGameMusic()
    }
}

// ==================== DATA CLASSES ====================

data class PSBubble(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val size:  Dp,
    val lifespanMs: Long,
    val spawnedAtMillis: Long = System.currentTimeMillis()
)

data class PSPopEffect(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val size:  Dp
)

data class PSFloatingText(
    val id: Int,
    val x: Dp,
    val y:  Dp,
    val text: String,
    val color: Color,
    val fontSize: TextUnit = 18.sp
)

data class PSParticle(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Dp
)

data class PSLuxEffect(
    val id: Int,
    val x: Dp,
    val y: Dp,
    val size:  Dp
)

data class PSStreakMilestone(
    val id: Int,
    val streak: Int
)

// ==================== UTILITY COMPOSABLES ====================

@Composable
fun PSOutlinedText(
    text:  String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color. White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign:  TextAlign?  = null,
    shadowEnabled: Boolean = true
) {
    val shadowOffset = if (shadowEnabled) Offset(2f, 2f) else Offset.Zero
    val shadowBlur = if (shadowEnabled) 4f else 0f

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black. copy(alpha = 0.7f),
                offset = shadowOffset,
                blurRadius = shadowBlur
            )
        )
    )
}

@Composable
fun PSAnimatedOutlinedText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color. White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign:  TextAlign? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "textPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "scale"
    )

    PSOutlinedText(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier. scale(scale),
        textAlign = textAlign
    )
}

@Composable
fun PSDefaultGradientBackground() {
    Box(
        modifier = Modifier
            . fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    )
}
@Composable
private fun GeneratedGameBackgroundPreview(id: Int, modifier: Modifier = Modifier) {
    when (id) {
        12 -> StarfieldBackground(modifier = modifier)
        13 -> OceanWavesBackground(modifier = modifier)
        14 -> ForestBackground(modifier = modifier)
        15 -> AuroraBorealisBackground(modifier = modifier)
        16 -> VolcanicBackground(modifier = modifier)
        17 -> CyberpunkCityBackground(modifier = modifier)
        18 -> UnderwaterBackground(modifier = modifier)
        19 -> DesertDunesBackground(modifier = modifier)
        20 -> CandyLandBackground(modifier = modifier)
        21 -> RetroGridBackground(modifier = modifier)
    }
}
// ==================== BUBBLE VIEW ====================
@Composable
fun PSBubbleView(
    bubble: PSBubble,
    bubblePainter: androidx.compose.ui.graphics.painter.Painter,
    modifier: Modifier = Modifier,
    equippedBubbleId: Int = 0
) {
    val spawnScale = remember { Animatable(0f) }
    val wobble = remember { Animatable(0f) }

    LaunchedEffect(bubble.id) {
        launch {
            spawnScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 300f))
        }
        launch {
            wobble.animateTo(
                1f,
                infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    val wobbleOffset = (wobble.value - 0.5f) * 4f

    if (isGeneratedBubble(equippedBubbleId)) {
        // Render generated bubble
        Box(
            modifier = modifier
                .size(bubble.size)
                .offset { IntOffset(bubble.x.roundToPx(), (bubble.y + wobbleOffset.dp).roundToPx()) }
                .scale(spawnScale.value)
                .graphicsLayer { rotationZ = wobbleOffset * 2f }
        ) {
            GetGeneratedBubbleForGame(
                id = equippedBubbleId,
                size = bubble.size,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        // Render drawable bubble
        Image(
            painter = bubblePainter,
            contentDescription = "bubble",
            modifier = modifier
                .size(bubble.size)
                .offset { IntOffset(bubble.x.roundToPx(), (bubble.y + wobbleOffset.dp).roundToPx()) }
                .scale(spawnScale.value)
                .graphicsLayer { rotationZ = wobbleOffset * 2f }
        )
    }
}


// ==================== HELPER FOR GENERATED BUBBLES ====================

private fun isGeneratedBubble(id: Int): Boolean = id in 11..30

@Composable
private fun GetGeneratedBubbleForGame(id: Int, size: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size)) {
        when (id) {
            11 -> FireBubble(Modifier.fillMaxSize())
            12 -> IceBubble(Modifier.fillMaxSize())
            13 -> ElectricBubble(Modifier.fillMaxSize())
            14 -> NatureBubble(Modifier.fillMaxSize())
            15 -> GalaxyBubble(Modifier.fillMaxSize())
            16 -> LavaBubble(Modifier.fillMaxSize())
            17 -> CrystalBubble(Modifier.fillMaxSize())
            18 -> SunsetBubble(Modifier.fillMaxSize())
            19 -> MidnightBubble(Modifier.fillMaxSize())
            20 -> CherryBlossomBubble(Modifier.fillMaxSize())
            21 -> ToxicBubble(Modifier.fillMaxSize())
            22 -> WaterBubble(Modifier.fillMaxSize())
            23 -> DiamondBubble(Modifier.fillMaxSize())
            24 -> NeonBubble(Modifier.fillMaxSize())
            25 -> AuroraBubble(Modifier.fillMaxSize())
            26 -> RainbowSwirlBubble(Modifier.fillMaxSize())
            27 -> SmokeBubble(Modifier.fillMaxSize())
            28 -> CandyBubble(Modifier.fillMaxSize())
            29 -> MetalBubble(Modifier.fillMaxSize())
            30 -> PlasmaBubble(Modifier.fillMaxSize())
        }
    }
}


// ==================== POP EFFECT VIEW ====================

@Composable
fun PSPopEffectView(effect: PSPopEffect, onFinished: () -> Unit) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(1f) }
    val innerScale = remember { Animatable(0.3f) }

    LaunchedEffect(effect.id) {
        launch { scale.animateTo(1.8f, tween(250, easing = EaseOutCubic)) }
        launch { innerScale.animateTo(1.2f, tween(200, easing = EaseOutQuad)) }
        launch {
            delay(100)
            alpha.animateTo(0f, tween(150, easing = EaseOutCubic))
        }
        delay(260)
        onFinished()
    }

    Box(
        modifier = Modifier
            .size(effect.size)
            .offset { IntOffset(effect.x.roundToPx(), effect.y.roundToPx()) },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                . fillMaxSize()
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 2f,
                style = Stroke(width = 6f)
            )
        }

        Canvas(
            modifier = Modifier
                . fillMaxSize()
                .scale(innerScale.value)
                .alpha(alpha. value * 0.6f)
        ) {
            drawCircle(
                color = Color(0xFFFFD700),
                radius = size.minDimension / 3f
            )
        }
    }
}

// ==================== PARTICLE VIEW ====================

@Composable
fun PSParticleView(particle: PSParticle) {
    val progress = remember { Animatable(0f) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(particle. id) {
        progress.animateTo(1f, tween(400, easing = EaseOutCubic))
        visible = false
    }

    if (visible) {
        val offsetX = (cos(particle.angle. toDouble()) * particle.speed * progress.value * 50).toFloat()
        val offsetY = (sin(particle.angle.toDouble()) * particle.speed * progress.value * 50).toFloat()

        Box(
            modifier = Modifier
                . offset { IntOffset((particle.x + offsetX. dp).roundToPx(), (particle.y + offsetY.dp).roundToPx()) }
                .size(particle.size * (1f - progress.value * 0.5f))
                .alpha(1f - progress.value)
                .background(particle.color, CircleShape)
        )
    }
}

// ==================== LUX EFFECT VIEW ====================

@Composable
fun PSLuxEffectView(effect: PSLuxEffect, onFinished:  () -> Unit) {
    val translateY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scaleAnim = remember { Animatable(0.5f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(effect. id) {
        // Play lux drop sound
        SoundManager.playLuxDrop()

        launch { scaleAnim.animateTo(1.2f, spring(dampingRatio = 0.4f, stiffness = 300f)) }
        launch { translateY.animateTo(-40f, tween(600, easing = EaseOutCubic)) }
        launch { rotation.animateTo(10f, tween(300)) }
        launch {
            delay(300)
            alpha.animateTo(0f, tween(300))
        }
        delay(620)
        onFinished()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset { IntOffset(effect.x.roundToPx(), (effect.y + translateY.value. dp).roundToPx()) }
            .alpha(alpha.value)
            .scale(scaleAnim.value)
            .graphicsLayer { rotationZ = rotation.value }
    ) {
        Image(
            painter = painterResource(id = R.drawable.gemgame),
            contentDescription = "lux",
            modifier = Modifier.size((effect.size. value * 0.55f).dp)
        )
        PSOutlinedText(
            text = "+1 LUX",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFD700),
            textAlign = TextAlign. Center
        )
    }
}

// ==================== FLOATING TEXT EFFECT ====================

@Composable
fun PSFloatingTextEffect(floatingText: PSFloatingText, onFinished: () -> Unit) {
    val alpha = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }
    val scale = remember { Animatable(1.2f) }

    LaunchedEffect(floatingText. id) {
        launch { scale.animateTo(1f, spring(dampingRatio = 0.5f)) }
        launch { offsetY.animateTo(-50f, tween(800, easing = EaseOutCubic)) }
        launch {
            delay(400)
            alpha.animateTo(0f, tween(400))
        }
        delay(820)
        onFinished()
    }

    PSOutlinedText(
        text = floatingText.text,
        fontSize = floatingText.fontSize,
        fontWeight = FontWeight.Bold,
        color = floatingText.color,
        modifier = Modifier
            .offset { IntOffset(floatingText.x.roundToPx(), (floatingText.y + offsetY.value.dp).roundToPx()) }
            .alpha(alpha.value)
            .scale(scale. value)
    )
}

// ==================== STREAK MILESTONE EFFECT ====================

@Composable
fun PSStreakMilestoneEffect(milestone: PSStreakMilestone, onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(50f) }

    LaunchedEffect(milestone.id) {
        // Play streak milestone sound
        SoundManager.playStreakMilestone(milestone.streak)

        launch { scale.animateTo(1.2f, spring(dampingRatio = 0.4f, stiffness = 300f)) }
        launch { alpha. animateTo(1f, tween(150)) }
        launch { offsetY.animateTo(0f, spring(dampingRatio = 0.5f)) }
        delay(200)
        launch { scale.animateTo(1f, tween(100)) }
        delay(1500)
        launch { alpha.animateTo(0f, tween(300)) }
        launch { offsetY.animateTo(-30f, tween(300)) }
        delay(320)
        onFinished()
    }

    val milestoneColor = when {
        milestone.streak >= 90 -> Color(0xFFFF0000)
        milestone.streak >= 75 -> Color(0xFFFF6B00)
        milestone.streak >= 50 -> Color(0xFFFFD700)
        milestone.streak >= 25 -> Color(0xFF00FF88)
        else -> Color(0xFF00BFFF)
    }

    val milestoneText = when {
        milestone.streak >= 90 -> "🔥 ${milestone.streak} STREAK!  LEGENDARY!  🔥"
        milestone.streak >= 75 -> "⚡ ${milestone.streak} STREAK! UNSTOPPABLE! ⚡"
        milestone.streak >= 50 -> "💥 ${milestone.streak} STREAK! ON FIRE! 💥"
        milestone.streak >= 25 -> "✨ ${milestone. streak} STREAK!  AMAZING! ✨"
        else -> "🎯 ${milestone.streak} Streak!"
    }

    Box(
        modifier = Modifier
            . fillMaxWidth()
            .offset { IntOffset(0, offsetY.value.dp.roundToPx()) }
            .alpha(alpha.value)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults. cardColors(
                containerColor = milestoneColor. copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults. cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier. padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                PSOutlinedText(
                    text = milestoneText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = milestoneColor
                )
            }
        }
    }
}

// ==================== TOP BAR ====================

@Composable
fun PSTopBar(
    currentStreak: Int,
    targetStreak: Int,
    elapsedSeconds: Int,
    modifier: Modifier = Modifier
) {
    val streakAnimated by animateIntAsState(
        targetValue = currentStreak,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "streakAnim"
    )

    val progress = (currentStreak. toFloat() / targetStreak.toFloat()).coerceIn(0f, 1f)

    val streakColor = when {
        currentStreak >= 90 -> Color(0xFFFF0000)
        currentStreak >= 75 -> Color(0xFFFF6B00)
        currentStreak >= 50 -> Color(0xFFFFD700)
        currentStreak >= 25 -> Color(0xFF00FF88)
        else -> Color(0xFF00BFFF)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults. cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment. CenterVertically
                    ) {
                        PSOutlinedText(
                            text = "🔥",
                            fontSize = 18.sp,
                            color = streakColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PSOutlinedText(
                            text = "$streakAnimated / $targetStreak",
                            fontSize = 18.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color. White
                        )
                    }
                }

                Card(
                    colors = CardDefaults. cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment. CenterVertically
                    ) {
                        PSOutlinedText(
                            text = "⏱️",
                            fontSize = 18.sp,
                            color = Color(0xFF00BFFF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PSOutlinedText(
                            text = formatTimePS(elapsedSeconds),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement. SpaceBetween
                    ) {
                        PSOutlinedText(
                            text = "Perfect Streak Progress",
                            fontSize = 12.sp,
                            color = Color. White. copy(alpha = 0.7f)
                        )
                        PSOutlinedText(
                            text = "${(progress * 100).roundToInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight. Bold,
                            color = streakColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = streakColor,
                        trackColor = Color. White.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

fun formatTimePS(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        String.format(Locale.US, "%d:%02d", minutes, secs)
    } else {
        "${secs}s"
    }
}

// ==================== COUNTDOWN OVERLAY ====================

@Composable
fun PSCountdownOverlay(countdownValue: Int, showPlayLabel: Boolean) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }

    LaunchedEffect(countdownValue, showPlayLabel) {
        // Play countdown or go sound
        if (showPlayLabel) {
            SoundManager. playGo()
        } else {
            SoundManager.playCountdown()
        }

        scale.snapTo(0.5f)
        alpha.snapTo(0f)
        rotation.snapTo(-30f)

        launch { scale.animateTo(1.2f, spring(dampingRatio = 0.4f, stiffness = 400f)) }
        launch { alpha.animateTo(1f, tween(150)) }
        launch { rotation.animateTo(0f, spring(dampingRatio = 0.5f)) }

        delay(700)
        launch { alpha.animateTo(0f, tween(250)) }
        launch { scale.animateTo(1.5f, tween(250)) }
    }

    Box(
        modifier = Modifier
            . fillMaxSize()
            .background(Color(0xCC000000))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val ev = awaitPointerEvent()
                        ev.changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment. Center
    ) {
        Box(
            modifier = Modifier
                . scale(scale.value)
                .alpha(alpha.value)
                .graphicsLayer { rotationZ = rotation.value }
        ) {
            if (! showPlayLabel) {
                Box(contentAlignment = Alignment. Center) {
                    PSOutlinedText(
                        text = countdownValue.toString(),
                        fontSize = 150.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        modifier = Modifier
                            .blur(24.dp)
                            .scale(1.2f)
                    )
                    PSOutlinedText(
                        text = countdownValue.toString(),
                        fontSize = 140.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color. White
                    )
                }
            } else {
                Box(contentAlignment = Alignment. Center) {
                    PSOutlinedText(
                        text = "PLAY! ",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        modifier = Modifier
                            .blur(24.dp)
                            .scale(1.2f)
                    )
                    PSOutlinedText(
                        text = "PLAY!",
                        fontSize = 90.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

// ==================== IN-GAME SETTINGS DIALOG ====================

@Composable
fun PSInGameSettingsDialog(
    onDismiss: () -> Unit,
    onResume: () -> Unit,
    onExit: () -> Unit,
    musicVolume: Float,
    onMusicVolumeChange: (Float) -> Unit,
    sfxVolume: Float,
    onSfxVolumeChange: (Float) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults. cardColors(
                containerColor = Color(0xFF1A1A2E)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2A1A4A),
                                Color(0xFF1A1A2E)
                            )
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PSOutlinedText(
                    text = "⏸️ Paused",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier. height(24.dp))

                // Music Volume
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier. fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment. CenterVertically
                        ) {
                            PSOutlinedText(
                                text = "🎵 Music",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color. White
                            )
                            PSOutlinedText(
                                text = "${(musicVolume * 100).toInt()}%",
                                fontSize = 14.sp,
                                color = Color(0xFFFF6D00)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Slider(
                            value = musicVolume,
                            onValueChange = onMusicVolumeChange,
                            valueRange = 0f..1f,
                            steps = 9,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFF6D00),
                                activeTrackColor = Color(0xFFFF6D00),
                                inactiveTrackColor = Color. White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // SFX Volume
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier. padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement. SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PSOutlinedText(
                                text = "🔊 Sound Effects",
                                fontSize = 16.sp,
                                fontWeight = FontWeight. Bold,
                                color = Color.White
                            )
                            PSOutlinedText(
                                text = "${(sfxVolume * 100).toInt()}%",
                                fontSize = 14.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Slider(
                            value = sfxVolume,
                            onValueChange = onSfxVolumeChange,
                            valueRange = 0f..1f,
                            steps = 9,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults. colors(
                                thumbColor = Color(0xFF4CAF50),
                                activeTrackColor = Color(0xFF4CAF50),
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Resume Button
                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    PSOutlinedText(
                        text = "▶ Resume",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color. White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Exit Button
                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        . height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    )
                ) {
                    PSOutlinedText(
                        text = "🚪 Exit Game",
                        fontSize = 18.sp,
                        fontWeight = FontWeight. Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==================== GAME OVER SCREEN ====================

@Composable
fun PSGameOverScreen(
    isSuccess: Boolean,
    currentStreak: Int,
    targetStreak: Int,
    elapsedSeconds: Int,
    sessionLuxEarned: Int,
    storedRecord: Int,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val finalPoints = currentStreak * 10
    val coinsEarned = finalPoints / 5
    val isNewRecord = currentStreak > storedRecord

    val overlayAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.8f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Play coin earn sound
        SoundManager.playCoinEarn()

        launch { overlayAlpha.animateTo(1f, tween(300)) }
        delay(150)
        launch { cardAlpha.animateTo(1f, tween(300)) }
        launch { cardScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 300f)) }
    }

    Box(
        modifier = Modifier
            . fillMaxSize()
            .background(Color(0xCC000000).copy(alpha = overlayAlpha.value)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(cardScale.value)
                .alpha(cardAlpha. value)
        ) {
            if (isSuccess) {
                PSAnimatedOutlinedText(
                    text = "🔥 PERFECT STREAK! 🔥",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700)
                )
            } else {
                PSOutlinedText(
                    text = "💔 STREAK BROKEN",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFF4444)
                )
            }

            Spacer(modifier = Modifier. height(8.dp))

            if (isNewRecord && currentStreak > 0) {
                PSAnimatedOutlinedText(
                    text = "🏆 NEW RECORD! 🏆",
                    fontSize = 24.sp,
                    fontWeight = FontWeight. ExtraBold,
                    color = Color(0xFF00FF88)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(0.9f),
                colors = CardDefaults. cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isSuccess) listOf(
                                        Color(0xFFFFD700).copy(alpha = 0.2f),
                                        Color(0xFF00FF88).copy(alpha = 0.2f)
                                    ) else listOf(
                                        Color(0xFFFF4444).copy(alpha = 0.2f),
                                        Color(0xFFFF6B00).copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment. Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PSOutlinedText(
                                text = "STREAK REACHED",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color. White. copy(alpha = 0.7f)
                            )
                            PSAnimatedOutlinedText(
                                text = "$currentStreak",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSuccess) Color(0xFFFFD700) else Color(0xFFFF6B00)
                            )
                            PSOutlinedText(
                                text = "/ $targetStreak",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color. White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement. SpaceBetween
                    ) {
                        PSStatItem(label = "Time", value = formatTimePS(elapsedSeconds), icon = "⏱️")
                        PSStatItem(label = "Points", value = "$finalPoints", icon = "⭐")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color. White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement. SpaceEvenly
                    ) {
                        PSRewardItem(icon = R.drawable.coin, value = "+$coinsEarned", label = "Coins")
                        PSRewardItem(icon = R.drawable.gemgame, value = "+$sessionLuxEarned", label = "Lux")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement. Center
                    ) {
                        PSOutlinedText(
                            text = "🏆 Record:  ${maxOf(currentStreak, storedRecord)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color. White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    . fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) Color(0xFF00C853) else Color(0xFFFF6D00)
                ),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 8.dp)
            ) {
                PSOutlinedText(
                    text = "🎮 TRY AGAIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onExit) {
                PSOutlinedText(
                    text = "Exit",
                    fontSize = 16.sp,
                    color = Color.White. copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun PSStatItem(label: String, value:  String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        PSOutlinedText(text = icon, fontSize = 20.sp, color = Color. White)
        PSOutlinedText(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        PSOutlinedText(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PSRewardItem(icon: Int, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            PSOutlinedText(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            PSOutlinedText(
                text = label,
                fontSize = 12.sp,
                color = Color. White.copy(alpha = 0.6f)
            )
        }
    }
}

// ==================== MAIN GAME SCREEN ====================

@Composable
fun PerfectStreakScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val equippedBubble by dataStore.equippedBubbleFlow().collectAsState(initial = 0)
    val equippedBg by dataStore.equippedBackgroundFlow().collectAsState(initial = 0)

    val bubbleRes = when (equippedBubble) {
        1 -> R.drawable.goldenbubble
        2 -> R.drawable.rainbowbubble
        3 -> R. drawable.greenbubble
        4 -> R. drawable.pinkbubble
        5 -> R.drawable.cyberpunkbubble
        6 -> R.drawable.oceanbubble
        7 -> R. drawable.animebubble1
        8 -> R.drawable.spacebubble
        10 -> R.drawable.levelbubble
        else -> R.drawable. bubble
    }
    val bubblePainter = painterResource(id = bubbleRes)

    // Volume states
    var musicVolume by remember { mutableFloatStateOf(MusicController.getMusicVolume()) }
    var sfxVolume by remember { mutableFloatStateOf(SoundManager.getSfxVolume()) }

    val bubbles = remember { mutableStateListOf<PSBubble>() }
    val popEffects = remember { mutableStateListOf<PSPopEffect>() }
    val floatingTexts = remember { mutableStateListOf<PSFloatingText>() }
    val particles = remember { mutableStateListOf<PSParticle>() }
    val luxEffects = remember { mutableStateListOf<PSLuxEffect>() }
    val streakMilestones = remember { mutableStateListOf<PSStreakMilestone>() }

    val spawnJobs = remember { mutableStateMapOf<Int, Job>() }
    val spawnStart = remember { mutableStateMapOf<Int, Long>() }
    val remainingMap = remember { mutableStateMapOf<Int, Long>() }

    var effectNextId by remember { mutableIntStateOf(0) }
    var bubbleSeq by remember { mutableIntStateOf(0) }

    val spawnMs = 900L
    val lifespanMs = 1800L
    val targetStreak = 100

    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var exitedByUser by remember { mutableStateOf(false) }

    var currentStreak by remember { mutableIntStateOf(0) }
    var sessionLuxEarned by remember { mutableIntStateOf(0) }
    var lastMilestoneReached by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val storedRecord by dataStore.highScorePerfectStreakFlow().collectAsState(initial = 0)

    val topBarHeight = 120.dp
    val topBarPadding = 12.dp
    val totalTopReserved = topBarHeight + topBarPadding

    var showCountdown by remember { mutableStateOf(true) }
    var countdownValue by remember { mutableIntStateOf(3) }
    var showPlayLabel by remember { mutableStateOf(false) }

    var showSettingsMenu by remember { mutableStateOf(false) }
    var wasRunningBeforeMenu by remember { mutableStateOf(false) }
    var showResumeCountdown by remember { mutableStateOf(false) }
    var resumeCountdownValue by remember { mutableIntStateOf(3) }

    // Start game music when entering
    DisposableEffect(Unit) {
        MusicController.startGameMusic()
        onDispose {
            MusicController.stopGameMusic()
        }
    }

    fun scheduleTimeoutForBubble(bubble: PSBubble, delayMs: Long) {
        spawnJobs. remove(bubble.id)?. cancel()
        spawnStart[bubble.id] = System.currentTimeMillis()
        val job = coroutineScope.launch {
            try {
                if (delayMs > 0) delay(delayMs)
                val stillThere = bubbles.any { it.id == bubble.id }
                if (stillThere) {
                    failed = true
                    running = false
                }
            } catch (_: CancellationException) { }
        }
        spawnJobs[bubble.id] = job
    }

    fun cancelAndStoreRemainingForAll() {
        val now = System.currentTimeMillis()
        for ((id, job) in spawnJobs. entries. toList()) {
            val start = spawnStart[id] ?: now
            val bubble = bubbles.find { it.id == id }
            val lifespan = bubble?.lifespanMs ?: lifespanMs
            val elapsed = now - start
            val remaining = (lifespan - elapsed).coerceAtLeast(0L)
            remainingMap[id] = remaining
            job.cancel()
            spawnJobs. remove(id)
            spawnStart.remove(id)
        }
    }

    fun rescheduleRemainingOnResume() {
        val copy = remainingMap. toMap()
        for ((id, remaining) in copy) {
            val bubble = bubbles.find { it.id == id }
            if (bubble != null && remaining > 0L) {
                remainingMap. remove(id)
                scheduleTimeoutForBubble(bubble, remaining)
            } else {
                remainingMap.remove(id)
            }
        }
    }

    fun cancelAndClearAllSpawnJobs() {
        for ((_, job) in spawnJobs) job.cancel()
        spawnJobs. clear()
        spawnStart.clear()
        remainingMap.clear()
    }

    fun createParticles(centerX:  Dp, centerY:  Dp, count: Int = 8) {
        val colors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFF6B00),
            Color(0xFF00FF88),
            Color(0xFF00BFFF),
            Color. White
        )

        repeat(count) { i ->
            val angle = (2 * PI * i / count).toFloat()
            val particle = PSParticle(
                id = effectNextId++,
                x = centerX,
                y = centerY,
                angle = angle,
                speed = Random.nextFloat() * 0.5f + 0.5f,
                color = colors. random(),
                size = (Random.nextFloat() * 4f + 4f).dp
            )
            particles.add(particle)

            coroutineScope.launch {
                delay(500)
                particles. removeAll { it.id == particle.id }
            }
        }
    }

    fun checkStreakMilestone(streak: Int) {
        val milestones = listOf(10, 25, 50, 75, 90)
        if (streak in milestones && streak > lastMilestoneReached) {
            lastMilestoneReached = streak
            val milestone = PSStreakMilestone(id = effectNextId++, streak = streak)
            streakMilestones.add(milestone)
        }
    }

    fun triggerGameOver() {
        failed = true
        running = false
        cancelAndClearAllSpawnJobs()
    }

    LaunchedEffect(Unit) {
        delay(120L)
        for (i in 3 downTo 1) {
            countdownValue = i
            delay(1000L)
        }
        showPlayLabel = true
        delay(450L)
        showCountdown = false
        showPlayLabel = false
        running = true
    }

    LaunchedEffect(running) {
        if (! running) return@LaunchedEffect
        while (running && ! success && !failed) {
            delay(1000L)
            elapsedSeconds += 1
        }
    }

    var maxWidth by remember { mutableStateOf(0.dp) }
    var maxHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        rescheduleRemainingOnResume()
        while (running && !success && !failed) {
            val bubbleSizeDp = 72.dp
            val xDp = ((Random.nextFloat() * (maxWidth - bubbleSizeDp).value)).dp
            val yRange = (maxHeight - bubbleSizeDp - totalTopReserved)
            val yDp = (totalTopReserved. value + Random.nextFloat() * yRange. value).dp

            val id = bubbleSeq++
            val spawnedAt = System.currentTimeMillis()
            val bubble = PSBubble(
                id = id,
                x = xDp,
                y = yDp,
                size = bubbleSizeDp,
                lifespanMs = lifespanMs,
                spawnedAtMillis = spawnedAt
            )

            bubbles.add(bubble)
            scheduleTimeoutForBubble(bubble, lifespanMs)

            delay(spawnMs)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        maxWidth = this.maxWidth
        maxHeight = this. maxHeight

        // Background
        if (equippedBg > 0) {
            // Check if it's a generated background (IDs 12-21)
            if (equippedBg >= 12) {
                GeneratedGameBackgroundPreview(id = equippedBg, modifier = Modifier.fillMaxSize())
            } else {
                // Static image backgrounds (IDs 1-11)
                val bgRes = when (equippedBg) {
                    1 -> R.drawable.background1
                    2 -> R.drawable.background2
                    3 -> R.drawable. background3
                    4 -> R.drawable.background4
                    5 -> R. drawable.background5
                    6 -> R.drawable.background6
                    7 -> R.drawable.background7
                    8 -> R.drawable. background8
                    9 -> R.drawable.background9
                    10 -> R.drawable.background10
                    11 -> R.drawable.background11
                    else -> 0
                }
                if (bgRes != 0) {
                    Image(
                        painter = painterResource(id = bgRes),
                        contentDescription = "game_background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale. Crop
                    )
                } else {
                    PSDefaultGradientBackground()
                }
            }
        } else {
            PSDefaultGradientBackground()
        }

        // Settings button
        Box(
            modifier = Modifier
                . align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .size(48.dp)
                .zIndex(10f)
                .clip(CircleShape)
                .background(Color(0xFF1A1A2E).copy(alpha = 0.7f))
                .clickable {
                    if (! showSettingsMenu && !success && !failed) {
                        wasRunningBeforeMenu = running
                        running = false
                        cancelAndStoreRemainingForAll()
                        showSettingsMenu = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable. settingsicon),
                contentDescription = "settings_button",
                modifier = Modifier.size(28.dp)
            )
        }

        // Top bar
        PSTopBar(
            currentStreak = currentStreak,
            targetStreak = targetStreak,
            elapsedSeconds = elapsedSeconds,
            modifier = Modifier. padding(top = 70.dp)
        )

        // Game area
        Box(
            modifier = Modifier
                . fillMaxSize()
                .pointerInput(bubbles. toList(), success, failed, running) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            for (change in event. changes) {
                                try {
                                    if (change.changedToUp()) {
                                        if (! running || success || failed) {
                                            change.consume()
                                            continue
                                        }

                                        val tapOffset = change.position
                                        val densityLocal = density

                                        // Check if tap is on settings button area
                                        val menuSizePx = with(densityLocal) { 48.dp. toPx() }
                                        val menuTopPx = with(densityLocal) { 20.dp.toPx() }
                                        val menuLeftPx = (with(densityLocal) { maxWidth.toPx() } - menuSizePx) / 2f
                                        val menuRightPx = menuLeftPx + menuSizePx
                                        val menuBottomPx = menuTopPx + menuSizePx

                                        if (tapOffset.x >= menuLeftPx && tapOffset.x <= menuRightPx &&
                                            tapOffset.y >= menuTopPx && tapOffset.y <= menuBottomPx) {
                                            continue
                                        }

                                        var hitBubble:  PSBubble?  = null
                                        val snapshot = bubbles.toList().asReversed()

                                        for (b in snapshot) {
                                            val bx = with(densityLocal) { b.x. toPx() }
                                            val by = with(densityLocal) { b.y.toPx() }
                                            val bs = with(densityLocal) { b.size.toPx() }
                                            if (tapOffset.x >= bx && tapOffset. x <= bx + bs &&
                                                tapOffset.y >= by && tapOffset.y <= by + bs
                                            ) {
                                                hitBubble = b
                                                break
                                            }
                                        }

                                        if (hitBubble != null) {
                                            val removed = bubbles.removeAll { it.id == hitBubble.id }
                                            if (removed) {
                                                spawnJobs.remove(hitBubble.id)?.cancel()
                                                spawnStart. remove(hitBubble.id)
                                                remainingMap. remove(hitBubble.id)

                                                // Play bubble pop sound
                                                SoundManager.playBubblePop()

                                                currentStreak += 1

                                                coroutineScope.launch {
                                                    dataStore.addTotalPops(1)
                                                }

                                                checkStreakMilestone(currentStreak)

                                                val popEffectUid = effectNextId++
                                                val effectSize = hitBubble.size * 1.25f
                                                val effectX = hitBubble.x + (hitBubble.size - effectSize) / 2f
                                                val effectY = hitBubble. y + (hitBubble.size - effectSize) / 2f

                                                popEffects.add(PSPopEffect(id = popEffectUid, x = effectX, y = effectY, size = effectSize))

                                                val floatingTextId = effectNextId++
                                                val streakColor = when {
                                                    currentStreak >= 90 -> Color(0xFFFF0000)
                                                    currentStreak >= 75 -> Color(0xFFFF6B00)
                                                    currentStreak >= 50 -> Color(0xFFFFD700)
                                                    currentStreak >= 25 -> Color(0xFF00FF88)
                                                    else -> Color(0xFF00BFFF)
                                                }
                                                floatingTexts.add(
                                                    PSFloatingText(
                                                        id = floatingTextId,
                                                        x = hitBubble.x + hitBubble.size / 4,
                                                        y = hitBubble.y,
                                                        text = "$currentStreak",
                                                        color = streakColor,
                                                        fontSize = 20.sp
                                                    )
                                                )

                                                val centerX = hitBubble.x + hitBubble.size / 2
                                                val centerY = hitBubble.y + hitBubble.size / 2
                                                createParticles(centerX, centerY, 8)

                                                val luxChance = 0.05
                                                if (Random.nextFloat() < luxChance) {
                                                    sessionLuxEarned += 1
                                                    coroutineScope.launch { dataStore.addLux(1) }
                                                    val luxEffectSize = hitBubble.size * 0.9f
                                                    val luxX = hitBubble.x + (hitBubble. size - luxEffectSize) / 2f
                                                    val luxY = hitBubble. y - luxEffectSize * 0.3f
                                                    val uid = effectNextId++
                                                    luxEffects.add(PSLuxEffect(id = uid, x = luxX, y = luxY, size = luxEffectSize))
                                                    coroutineScope.launch {
                                                        delay(900L)
                                                        luxEffects.removeAll { it.id == uid }
                                                    }
                                                }

                                                coroutineScope.launch {
                                                    delay(600L)
                                                    popEffects.removeAll { it.id == popEffectUid }
                                                }

                                                coroutineScope. launch {
                                                    delay(850L)
                                                    floatingTexts.removeAll { it.id == floatingTextId }
                                                }

                                                if (currentStreak >= targetStreak) {
                                                    success = true
                                                    running = false
                                                }
                                            }
                                            change. consume()
                                        } else {
                                            // MISS - Streak broken!  Play lost streak sound
                                            SoundManager.playLostStreak()
                                            failed = true
                                            running = false
                                            change.consume()
                                        }
                                    }
                                } catch (_: Exception) { }
                            }
                        }
                    }
                }
        ) {
            // Draw bubbles
            bubbles.toList().forEach { b ->
                key(b.id) {
                    PSBubbleView(bubble = b, bubblePainter = bubblePainter, equippedBubbleId = equippedBubble)
                }
            }

            // Draw pop effects
            popEffects.toList().forEach { effect ->
                key(effect.id) {
                    PSPopEffectView(effect = effect) { popEffects. removeAll { it.id == effect.id } }
                }
            }

            // Draw particles
            particles.toList().forEach { particle ->
                key(particle.id) {
                    PSParticleView(particle = particle)
                }
            }

            // Draw floating texts
            floatingTexts.toList().forEach { floatingText ->
                key(floatingText.id) {
                    PSFloatingTextEffect(floatingText = floatingText) { floatingTexts. removeAll { it. id == floatingText.id } }
                }
            }

            // Draw lux effects
            luxEffects.toList().forEach { luxEffect ->
                key(luxEffect.id) {
                    PSLuxEffectView(effect = luxEffect) { luxEffects.removeAll { it.id == luxEffect.id } }
                }
            }

            // Draw streak milestones
            Box(modifier = Modifier.fillMaxSize().padding(top = 180.dp), contentAlignment = Alignment.TopCenter) {
                streakMilestones.toList().forEach { milestone ->
                    key(milestone.id) {
                        PSStreakMilestoneEffect(milestone = milestone) { streakMilestones.removeAll { it.id == milestone.id } }
                    }
                }
            }

            // Game Over Screen
            if (success || failed) {
                cancelAndClearAllSpawnJobs()

                val finalPoints = currentStreak * 10
                val coinsEarned = finalPoints / 5

                LaunchedEffect(currentStreak) {
                    if (currentStreak > storedRecord) {
                        dataStore.saveHighScorePerfectStreak(currentStreak)
                    }
                    if (coinsEarned > 0) {
                        dataStore. addCoins(coinsEarned)
                    }
                }

                PSGameOverScreen(
                    isSuccess = success,
                    currentStreak = currentStreak,
                    targetStreak = targetStreak,
                    elapsedSeconds = elapsedSeconds,
                    sessionLuxEarned = sessionLuxEarned,
                    storedRecord = storedRecord,
                    onPlayAgain = {
                        cancelAndClearAllSpawnJobs()
                        bubbles.clear()
                        popEffects.clear()
                        floatingTexts.clear()
                        particles.clear()
                        luxEffects. clear()
                        streakMilestones.clear()

                        elapsedSeconds = 0
                        effectNextId = 0
                        bubbleSeq = 0
                        currentStreak = 0
                        sessionLuxEarned = 0
                        lastMilestoneReached = 0
                        success = false
                        failed = false
                        exitedByUser = false
                        showCountdown = true
                        countdownValue = 3
                        showPlayLabel = false

                        coroutineScope.launch {
                            delay(120L)
                            for (i in 3 downTo 1) {
                                countdownValue = i
                                delay(1000L)
                            }
                            showPlayLabel = true
                            delay(450L)
                            showCountdown = false
                            showPlayLabel = false
                            running = true
                        }
                    },
                    onExit = onExit
                )
            }

            // Countdown overlay
            if (showCountdown) {
                PSCountdownOverlay(countdownValue = countdownValue, showPlayLabel = showPlayLabel)
            }

            // Settings Dialog
            if (showSettingsMenu) {
                PSInGameSettingsDialog(
                    onDismiss = {
                        showSettingsMenu = false
                        if (wasRunningBeforeMenu) {
                            showResumeCountdown = true
                            resumeCountdownValue = 3
                            coroutineScope.launch {
                                for (i in 3 downTo 1) {
                                    resumeCountdownValue = i
                                    SoundManager.playCountdown()
                                    delay(1000L)
                                }
                                showResumeCountdown = false
                                running = true
                                wasRunningBeforeMenu = false
                            }
                        } else {
                            wasRunningBeforeMenu = false
                        }
                    },
                    onResume = {
                        showSettingsMenu = false
                        if (wasRunningBeforeMenu) {
                            showResumeCountdown = true
                            resumeCountdownValue = 3
                            coroutineScope. launch {
                                for (i in 3 downTo 1) {
                                    resumeCountdownValue = i
                                    SoundManager.playCountdown()
                                    delay(1000L)
                                }
                                showResumeCountdown = false
                                running = true
                                wasRunningBeforeMenu = false
                            }
                        } else {
                            wasRunningBeforeMenu = false
                        }
                    },
                    onExit = {
                        showSettingsMenu = false
                        exitedByUser = true
                        triggerGameOver()
                    },
                    musicVolume = musicVolume,
                    onMusicVolumeChange = { newVolume ->
                        musicVolume = newVolume
                        MusicController.setMusicVolume(newVolume)
                    },
                    sfxVolume = sfxVolume,
                    onSfxVolumeChange = { newVolume ->
                        sfxVolume = newVolume
                        SoundManager.setSfxVolume(newVolume)
                    }
                )
            }

            // Resume countdown overlay
            if (showResumeCountdown) {
                Box(
                    modifier = Modifier
                        . fillMaxSize()
                        .background(Color(0xAA000000))
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val ev = awaitPointerEvent()
                                    ev.changes.forEach { it.consume() }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(contentAlignment = Alignment. Center) {
                        PSOutlinedText(
                            text = resumeCountdownValue. toString(),
                            fontSize = 150.sp,
                            fontWeight = FontWeight. ExtraBold,
                            color = Color(0xFFFFD700).copy(alpha = 0.3f),
                            modifier = Modifier.blur(24.dp).scale(1.2f)
                        )
                        PSOutlinedText(
                            text = resumeCountdownValue. toString(),
                            fontSize = 140.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color. White
                        )
                    }
                }
            }
        }
    }
}
