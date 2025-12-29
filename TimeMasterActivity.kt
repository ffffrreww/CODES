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
import androidx.compose.runtime.mutableLongStateOf
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

class TimeMasterActivity :  ComponentActivity() {
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

        MusicController.initIfNeeded(applicationContext)
        SoundManager.init(applicationContext)

        setContent {
            PopITTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TimeMasterScreen(onExit = { finish() })
                }
            }
        }
    }

    override fun onDestroy() {
        super. onDestroy()
        MusicController. stopGameMusic()
    }
}

// ==================== DATA CLASSES ====================

data class TMBubble(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val size:  Dp,
    val lifespanMs: Long,
    val spawnedAtMillis: Long = System.currentTimeMillis()
)

data class TMPopEffect(
    val id: Int,
    val x: Dp,
    val y: Dp,
    val size:  Dp
)

data class TMFloatingText(
    val id: Int,
    val x: Dp,
    val y:  Dp,
    val text: String,
    val color: Color,
    val fontSize: TextUnit = 18.sp
)

data class TMParticle(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Dp
)

data class TMLuxEffect(
    val id: Int,
    val x:  Dp,
    val y: Dp,
    val size:  Dp
)

// ==================== UTILITY COMPOSABLES ====================

@Composable
fun TMOutlinedText(
    text:  String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color. White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign?  = null,
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
fun TMAnimatedOutlinedText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color. White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign:  TextAlign? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "textPulse")
    val scale by infiniteTransition. animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    TMOutlinedText(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier. scale(scale),
        textAlign = textAlign
    )
}

@Composable
fun TMDefaultGradientBackground() {
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
@Composable
fun TMBubbleView(
    bubble: TMBubble,
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
fun TMPopEffectView(effect: TMPopEffect, onFinished: () -> Unit) {
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
fun TMParticleView(particle: TMParticle) {
    val progress = remember { Animatable(0f) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(particle.id) {
        progress.animateTo(1f, tween(400, easing = EaseOutCubic))
        visible = false
    }

    if (visible) {
        val offsetX = (cos(particle.angle. toDouble()) * particle.speed * progress.value * 50).toFloat()
        val offsetY = (sin(particle.angle.toDouble()) * particle.speed * progress.value * 50).toFloat()

        Box(
            modifier = Modifier
                . offset { IntOffset((particle.x + offsetX. dp).roundToPx(), (particle.y + offsetY. dp).roundToPx()) }
                .size(particle. size * (1f - progress.value * 0.5f))
                .alpha(1f - progress.value)
                .background(particle.color, CircleShape)
        )
    }
}

// ==================== LUX EFFECT VIEW ====================

@Composable
fun TMLuxEffectView(effect: TMLuxEffect, onFinished: () -> Unit) {
    val translateY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scaleAnim = remember { Animatable(0.5f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(effect. id) {
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
            painter = painterResource(id = R.drawable. gemgame),
            contentDescription = "lux",
            modifier = Modifier.size((effect.size. value * 0.55f).dp)
        )
        TMOutlinedText(
            text = "+1 LUX",
            fontSize = 14. sp,
            fontWeight = FontWeight. ExtraBold,
            color = Color(0xFFFFD700),
            textAlign = TextAlign. Center
        )
    }
}

// ==================== FLOATING TEXT EFFECT ====================

@Composable
fun TMFloatingTextEffect(floatingText: TMFloatingText, onFinished: () -> Unit) {
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

    TMOutlinedText(
        text = floatingText.text,
        fontSize = floatingText.fontSize,
        fontWeight = FontWeight.Bold,
        color = floatingText.color,
        modifier = Modifier
            .offset { IntOffset(floatingText.x. roundToPx(), (floatingText. y + offsetY. value.dp).roundToPx()) }
            .alpha(alpha.value)
            .scale(scale. value)
    )
}

// ==================== TOP BAR ====================

@Composable
fun TMTopBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    poppedCount: Int,
    targetPops: Int,
    modifier: Modifier = Modifier
) {
    val poppedAnimated by animateIntAsState(
        targetValue = poppedCount,
        animationSpec = tween(300, easing = EaseOutCubic),
        label = "poppedAnim"
    )

    val progress = (poppedCount.toFloat() / targetPops.toFloat()).coerceIn(0f, 1f)
    val timeProgress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val timeColor = when {
        remainingSeconds <= 10 -> Color(0xFFFF0000)
        remainingSeconds <= 20 -> Color(0xFFFF6B00)
        remainingSeconds <= 30 -> Color(0xFFFFD700)
        else -> Color(0xFF00FF88)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. SpaceBetween,
                verticalAlignment = Alignment. CenterVertically
            ) {
                Card(
                    colors = CardDefaults. cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults. cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TMOutlinedText(
                            text = "⏱️",
                            fontSize = 18.sp,
                            color = timeColor
                        )
                        Spacer(modifier = Modifier. width(8.dp))
                        TMOutlinedText(
                            text = "${remainingSeconds}s",
                            fontSize = 22.sp,
                            fontWeight = FontWeight. Bold,
                            color = timeColor
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TMOutlinedText(
                            text = "🫧",
                            fontSize = 18.sp,
                            color = Color(0xFF00BFFF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TMOutlinedText(
                            text = "$poppedAnimated / $targetPops",
                            fontSize = 18.sp,
                            fontWeight = FontWeight. Bold,
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
                        TMOutlinedText(
                            text = "Progress to Victory",
                            fontSize = 12.sp,
                            color = Color. White. copy(alpha = 0.7f)
                        )
                        TMOutlinedText(
                            text = "${(progress * 100).roundToInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFD700),
                        trackColor = Color. White. copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TMOutlinedText(
                            text = "Time Remaining",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        TMOutlinedText(
                            text = "${(timeProgress * 100).roundToInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = timeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { timeProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = timeColor,
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

fun formatTimeTM(seconds: Int): String {
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
fun TMCountdownOverlay(countdownValue: Int, showPlayLabel: Boolean) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }

    LaunchedEffect(countdownValue, showPlayLabel) {
        if (showPlayLabel) {
            SoundManager.playGo()
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
            .fillMaxSize()
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
                    TMOutlinedText(
                        text = countdownValue.toString(),
                        fontSize = 150.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        modifier = Modifier
                            .blur(24.dp)
                            .scale(1.2f)
                    )
                    TMOutlinedText(
                        text = countdownValue.toString(),
                        fontSize = 140.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color. White
                    )
                }
            } else {
                Box(contentAlignment = Alignment.Center) {
                    TMOutlinedText(
                        text = "PLAY! ",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700).copy(alpha = 0.3f),
                        modifier = Modifier
                            .blur(24.dp)
                            .scale(1.2f)
                    )
                    TMOutlinedText(
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
fun TMInGameSettingsDialog(
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
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A2E)
            ),
            elevation = CardDefaults. cardElevation(defaultElevation = 16.dp)
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
                TMOutlinedText(
                    text = "⏸️ Paused",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier. height(24.dp))

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
                            verticalAlignment = Alignment. CenterVertically
                        ) {
                            TMOutlinedText(
                                text = "🎵 Music",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color. White
                            )
                            TMOutlinedText(
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
                            colors = SliderDefaults. colors(
                                thumbColor = Color(0xFFFF6D00),
                                activeTrackColor = Color(0xFFFF6D00),
                                inactiveTrackColor = Color. White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment. CenterVertically
                        ) {
                            TMOutlinedText(
                                text = "🔊 Sound Effects",
                                fontSize = 16.sp,
                                fontWeight = FontWeight. Bold,
                                color = Color.White
                            )
                            TMOutlinedText(
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

                Spacer(modifier = Modifier. height(24.dp))

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
                    TMOutlinedText(
                        text = "▶ Resume",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color. White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                    TMOutlinedText(
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
fun TMGameOverScreen(
    isSuccess: Boolean,
    totalPopped: Int,
    targetPops: Int,
    totalSeconds: Int,
    sessionLuxEarned: Int,
    storedRecord: Int,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val finalPoints = totalPopped * 10
    val coinsEarned = finalPoints / 5
    val isNewRecord = totalPopped > storedRecord

    val overlayAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.8f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
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
                TMAnimatedOutlinedText(
                    text = "⏱️ TIME MASTER!  ⏱️",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700)
                )
            } else {
                TMOutlinedText(
                    text = "⏱️ TIME'S UP! ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight. ExtraBold,
                    color = Color(0xFFFF4444)
                )
            }

            Spacer(modifier = Modifier. height(8.dp))

            if (isNewRecord && totalPopped > 0) {
                TMAnimatedOutlinedText(
                    text = "🏆 NEW RECORD! 🏆",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
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
                            . background(
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
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TMOutlinedText(
                                text = "BUBBLES POPPED",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color. White. copy(alpha = 0.7f)
                            )
                            TMAnimatedOutlinedText(
                                text = "$totalPopped",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSuccess) Color(0xFFFFD700) else Color(0xFFFF6B00)
                            )
                            TMOutlinedText(
                                text = "/ $targetPops in ${totalSeconds}s",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color. White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier. fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TMStatItem(label = "Points", value = "$finalPoints", icon = "⭐")
                        TMStatItem(label = "Speed", value = "${(totalPopped. toFloat() / totalSeconds * 60).roundToInt()}/min", icon = "🚀")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color. White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TMRewardItem(icon = R.drawable.coin, value = "+$coinsEarned", label = "Coins")
                        TMRewardItem(icon = R.drawable.gemgame, value = "+$sessionLuxEarned", label = "Lux")
                    }

                    Spacer(modifier = Modifier. height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TMOutlinedText(
                            text = "🏆 Record:  ${maxOf(totalPopped, storedRecord)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color. White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier. height(24.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) Color(0xFF00C853) else Color(0xFFFF6D00)
                ),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 8.dp)
            ) {
                TMOutlinedText(
                    text = "🎮 TRY AGAIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onExit) {
                TMOutlinedText(
                    text = "Exit",
                    fontSize = 16.sp,
                    color = Color.White. copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun TMStatItem(label: String, value:  String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TMOutlinedText(text = icon, fontSize = 20.sp, color = Color.White)
        TMOutlinedText(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        TMOutlinedText(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun TMRewardItem(icon: Int, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            TMOutlinedText(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            TMOutlinedText(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

// ==================== MAIN GAME SCREEN ====================

@Composable
fun TimeMasterScreen(onExit: () -> Unit) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val equippedBubble by dataStore.equippedBubbleFlow().collectAsState(initial = 0)
    val equippedBg by dataStore.equippedBackgroundFlow().collectAsState(initial = 0)

    val bubbleRes = when (equippedBubble) {
        1 -> R.drawable.goldenbubble
        2 -> R.drawable.rainbowbubble
        3 -> R.drawable.greenbubble
        4 -> R. drawable.pinkbubble
        5 -> R. drawable.cyberpunkbubble
        6 -> R.drawable.oceanbubble
        7 -> R. drawable.animebubble1
        8 -> R.drawable.spacebubble
        10 -> R.drawable.levelbubble
        else -> R.drawable. bubble
    }
    val bubblePainter = painterResource(id = bubbleRes)

    var musicVolume by remember { mutableFloatStateOf(MusicController.getMusicVolume()) }
    var sfxVolume by remember { mutableFloatStateOf(SoundManager.getSfxVolume()) }

    val bubbles = remember { mutableStateListOf<TMBubble>() }
    val popEffects = remember { mutableStateListOf<TMPopEffect>() }
    val floatingTexts = remember { mutableStateListOf<TMFloatingText>() }
    val particles = remember { mutableStateListOf<TMParticle>() }
    val luxEffects = remember { mutableStateListOf<TMLuxEffect>() }

    val spawnJobs = remember { mutableStateMapOf<Int, Job>() }
    val spawnStart = remember { mutableStateMapOf<Int, Long>() }
    val remainingMap = remember { mutableStateMapOf<Int, Long>() }

    var effectNextId by remember { mutableIntStateOf(0) }
    var bubbleSeq by remember { mutableIntStateOf(0) }

    val spawnMs = 220L
    val lifespanMs = 1200L
    val totalSeconds = 50
    val targetPops = 140

    var remainingMs by remember { mutableLongStateOf(totalSeconds * 1000L) }
    var running by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var exitedByUser by remember { mutableStateOf(false) }

    var totalPopped by remember { mutableIntStateOf(0) }
    var sessionLuxEarned by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val storedRecord by dataStore.highScoreTimeMasterFlow().collectAsState(initial = 0)

    val topBarHeight = 140. dp
    val topBarPadding = 12.dp
    val totalTopReserved = topBarHeight + topBarPadding

    var showCountdown by remember { mutableStateOf(true) }
    var countdownValue by remember { mutableIntStateOf(3) }
    var showPlayLabel by remember { mutableStateOf(false) }

    var showSettingsMenu by remember { mutableStateOf(false) }
    var wasRunningBeforeMenu by remember { mutableStateOf(false) }
    var showResumeCountdown by remember { mutableStateOf(false) }
    var resumeCountdownValue by remember { mutableIntStateOf(3) }

    var pausedRemainingMs by remember { mutableLongStateOf(0L) }

    DisposableEffect(Unit) {
        MusicController.startGameMusic()
        onDispose {
            MusicController.stopGameMusic()
        }
    }

    fun scheduleTimeoutForBubble(bubble: TMBubble, delayMs: Long) {
        spawnJobs. remove(bubble.id)?. cancel()
        spawnStart[bubble.id] = System.currentTimeMillis()
        val job = coroutineScope.launch {
            try {
                if (delayMs > 0) delay(delayMs)
                bubbles.removeAll { it.id == bubble.id }
            } catch (_: CancellationException) { }
        }
        spawnJobs[bubble.id] = job
    }

    fun cancelAndStoreRemainingForAll() {
        val now = System.currentTimeMillis()
        for ((id, job) in spawnJobs. entries. toList()) {
            val start = spawnStart[id] ?: now
            val bubble = bubbles.find { it.id == id }
            val lifespan = bubble?.lifespanMs ?:  lifespanMs
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
            val particle = TMParticle(
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
        while (running && remainingMs > 0 && ! success && !failed) {
            delay(100L)
            remainingMs = (remainingMs - 100L).coerceAtLeast(0L)
            if (remainingMs == 0L) {
                running = false
                success = totalPopped >= targetPops
                if (! success) failed = true
            }
        }
    }

    var maxWidth by remember { mutableStateOf(0.dp) }
    var maxHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        rescheduleRemainingOnResume()
        while (running && ! success && !failed) {
            val bubbleSizeDp = 64.dp
            val xDp = ((Random.nextFloat() * (maxWidth - bubbleSizeDp).value)).dp
            val yRange = (maxHeight - bubbleSizeDp - totalTopReserved)
            val yDp = (totalTopReserved. value + Random.nextFloat() * yRange.value).dp

            val id = bubbleSeq++
            val spawnedAt = System.currentTimeMillis()
            val bubble = TMBubble(
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
                    TMDefaultGradientBackground()
                }
            }
        } else {
            TMDefaultGradientBackground()
        }

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
                        pausedRemainingMs = remainingMs
                        running = false
                        cancelAndStoreRemainingForAll()
                        showSettingsMenu = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.settingsicon),
                contentDescription = "settings_button",
                modifier = Modifier.size(28.dp)
            )
        }

        TMTopBar(
            remainingSeconds = (remainingMs / 1000L).toInt(),
            totalSeconds = totalSeconds,
            poppedCount = totalPopped,
            targetPops = targetPops,
            modifier = Modifier.padding(top = 70.dp)
        )

        Box(
            modifier = Modifier
                . fillMaxSize()
                .pointerInput(bubbles.toList(), success, failed, running) {
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

                                        var hitBubble:  TMBubble?  = null
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
                                                remainingMap.remove(hitBubble. id)

                                                SoundManager.playBubblePop()

                                                totalPopped += 1

                                                coroutineScope.launch {
                                                    dataStore.addTotalPops(1)
                                                }

                                                val popEffectUid = effectNextId++
                                                val effectSize = hitBubble.size * 1.25f
                                                val effectX = hitBubble.x + (hitBubble.size - effectSize) / 2f
                                                val effectY = hitBubble.y + (hitBubble.size - effectSize) / 2f

                                                popEffects.add(TMPopEffect(id = popEffectUid, x = effectX, y = effectY, size = effectSize))

                                                val floatingTextId = effectNextId++
                                                floatingTexts.add(
                                                    TMFloatingText(
                                                        id = floatingTextId,
                                                        x = hitBubble.x + hitBubble.size / 4,
                                                        y = hitBubble.y,
                                                        text = "+10",
                                                        color = Color(0xFF00FF88),
                                                        fontSize = 18.sp
                                                    )
                                                )

                                                val centerX = hitBubble.x + hitBubble.size / 2
                                                val centerY = hitBubble.y + hitBubble.size / 2
                                                createParticles(centerX, centerY, 6)

                                                val luxChance = 0.05
                                                if (Random.nextFloat() < luxChance) {
                                                    sessionLuxEarned += 1
                                                    coroutineScope.launch { dataStore.addLux(1) }
                                                    val luxEffectSize = hitBubble.size * 0.9f
                                                    val luxX = hitBubble.x + (hitBubble. size - luxEffectSize) / 2f
                                                    val luxY = hitBubble. y - luxEffectSize * 0.3f
                                                    val uid = effectNextId++
                                                    luxEffects.add(TMLuxEffect(id = uid, x = luxX, y = luxY, size = luxEffectSize))
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

                                                if (totalPopped >= targetPops) {
                                                    success = true
                                                    running = false
                                                }
                                            }
                                            change.consume()
                                        }
                                    }
                                } catch (_: Exception) { }
                            }
                        }
                    }
                }
        ) {
            bubbles.toList().forEach { b ->
                key(b.id) {
                    TMBubbleView(bubble = b, bubblePainter = bubblePainter, equippedBubbleId = equippedBubble)
                }
            }

            popEffects.toList().forEach { effect ->
                key(effect.id) {
                    TMPopEffectView(effect = effect) { popEffects.removeAll { it.id == effect.id } }
                }
            }

            particles.toList().forEach { particle ->
                key(particle.id) {
                    TMParticleView(particle = particle)
                }
            }

            floatingTexts.toList().forEach { floatingText ->
                key(floatingText.id) {
                    TMFloatingTextEffect(floatingText = floatingText) { floatingTexts. removeAll { it. id == floatingText.id } }
                }
            }

            luxEffects. toList().forEach { luxEffect ->
                key(luxEffect. id) {
                    TMLuxEffectView(effect = luxEffect) { luxEffects.removeAll { it.id == luxEffect.id } }
                }
            }

            if (success || failed) {
                cancelAndClearAllSpawnJobs()

                val finalPoints = totalPopped * 10
                val coinsEarned = finalPoints / 5

                LaunchedEffect(totalPopped) {
                    if (totalPopped > storedRecord) {
                        dataStore. saveHighScoreTimeMaster(totalPopped)
                    }
                    if (coinsEarned > 0) {
                        dataStore.addCoins(coinsEarned)
                    }
                }

                TMGameOverScreen(
                    isSuccess = success,
                    totalPopped = totalPopped,
                    targetPops = targetPops,
                    totalSeconds = totalSeconds,
                    sessionLuxEarned = sessionLuxEarned,
                    storedRecord = storedRecord,
                    onPlayAgain = {
                        cancelAndClearAllSpawnJobs()
                        bubbles.clear()
                        popEffects.clear()
                        floatingTexts.clear()
                        particles.clear()
                        luxEffects.clear()

                        remainingMs = totalSeconds * 1000L
                        effectNextId = 0
                        bubbleSeq = 0
                        totalPopped = 0
                        sessionLuxEarned = 0
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

            if (showCountdown) {
                TMCountdownOverlay(countdownValue = countdownValue, showPlayLabel = showPlayLabel)
            }

            if (showSettingsMenu) {
                TMInGameSettingsDialog(
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
                                remainingMs = pausedRemainingMs
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
                                    SoundManager. playCountdown()
                                    delay(1000L)
                                }
                                showResumeCountdown = false
                                remainingMs = pausedRemainingMs
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

            if (showResumeCountdown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        . background(Color(0xAA000000))
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
                        TMOutlinedText(
                            text = resumeCountdownValue. toString(),
                            fontSize = 150.sp,
                            fontWeight = FontWeight. ExtraBold,
                            color = Color(0xFFFFD700).copy(alpha = 0.3f),
                            modifier = Modifier. blur(24.dp).scale(1.2f)
                        )
                        TMOutlinedText(
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