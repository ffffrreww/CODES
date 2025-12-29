package com.appsdevs.popit

import android.annotation.SuppressLint
import android. content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android. graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core. Animatable
import androidx.compose.animation. core.EaseInOutSine
import androidx.compose.animation.core. FastOutSlowInEasing
import androidx. compose.animation.core.RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. animateFloatAsState
import androidx.compose. animation.core.infiniteRepeatable
import androidx.compose. animation.core.rememberInfiniteTransition
import androidx. compose.animation.core.spring
import androidx.compose.animation.core. tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation. Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx. compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose. foundation.layout. Arrangement
import androidx. compose.foundation.layout.Box
import androidx.compose.foundation. layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose. foundation.layout. Spacer
import androidx.compose.foundation. layout.fillMaxHeight
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.foundation.layout. fillMaxWidth
import androidx.compose.foundation. layout.height
import androidx.compose.foundation.layout. padding
import androidx. compose.foundation.layout.size
import androidx.compose.foundation.layout. width
import androidx. compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose. foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx. compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose. material3.IconButton
import androidx. compose.material3.LinearProgressIndicator
import androidx. compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx. compose.runtime. Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime. getValue
import androidx. compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime. mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime. remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui. Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui. draw.alpha
import androidx.compose.ui.draw. clip
import androidx. compose.ui.draw.scale
import androidx.compose.ui.geometry. CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry. Size
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Path
import androidx.compose.ui.graphics. Shadow
import androidx.compose.ui.graphics. StrokeCap
import androidx. compose.ui.graphics.asImageBitmap
import androidx.compose.ui. graphics.drawscope.Fill
import androidx.compose.ui.graphics. painter.BitmapPainter
import androidx.compose.ui. graphics.painter. Painter
import androidx. compose.ui.input.pointer.pointerInput
import androidx. compose.ui.layout.ContentScale
import androidx. compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit. TextUnit
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import androidx.compose.ui.zIndex
import androidx. core.view.WindowCompat
import androidx. core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appsdevs.popit.ui.theme.PopITTheme
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.delay
import kotlinx. coroutines.launch
import kotlinx.coroutines.withContext
import java.util. Locale

// ==================== MAIN ACTIVITY ====================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        enableEdgeToEdge()

        // Initialize music and sound
        MusicController.initIfNeeded(applicationContext)
        SoundManager.init(applicationContext)

        setContent {
            PopITTheme {
                val dataStoreManager = DataStoreManager(applicationContext)
                val highScore by dataStoreManager.highScoreFlow().collectAsState(initial = 0)
                val coins by dataStoreManager.coinsFlow().collectAsState(initial = 0)
                val lux by dataStoreManager.luxFlow().collectAsState(initial = 0)

                MainMenu(
                    highScore = highScore,
                    coins = coins,
                    lux = lux,
                    ds = dataStoreManager,
                    onPlayClicked = {
                        startActivity(Intent(this@MainActivity, GameActivity::class.java))
                    },
                    onChallengesClicked = { }
                )
            }
        }
    }
}

// ==================== UTILITY FUNCTIONS ====================

private fun formatMillis(ms: Int): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

// ==================== UTILITY COMPOSABLES ====================

@Composable
fun bitmapPainterResourceSafe(@DrawableRes id: Int? ): Painter? {
    if (id == null || id == 0) return null
    val ctx = LocalContext.current

    val painterState = produceState<Painter?>(initialValue = null, key1 = id) {
        try {
            val bmp = withContext(Dispatchers.IO) {
                BitmapFactory.decodeResource(ctx. resources, id)
            }
            value = bmp?. let { BitmapPainter(it. asImageBitmap()) }
            if (bmp == null) Log.e("BitmapPainterSafe", "decodeResource returned null for id=$id")
        } catch (t: Throwable) {
            val name = try { ctx.resources.getResourceName(id) } catch (_: Exception) { "id=$id" }
            Log.e("BitmapPainterSafe", "Failed to decode resource $name", t)
            value = null
        }
    }
    return painterState.value
}

@Composable
fun PlayTriangle(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier. size(24.dp)) {
        val p = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.15f)
            lineTo(size.width * 0.2f, size.height * 0.85f)
            lineTo(size.width * 0.85f, size.height * 0.5f)
            close()
        }
        drawPath(path = p, color = tint, style = Fill)
    }
}

@Composable
fun PauseSymbol(modifier:  Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier.size(24.dp)) {
        val barWidth = size.width * 0.28f
        val gap = size.width * 0.12f
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(barWidth, size.height),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(barWidth + gap, 0f),
            size = Size(barWidth, size.height),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }
}

@Composable
fun SkipNextSymbol(modifier:  Modifier = Modifier, tint: Color = Color.White) {
    Canvas(modifier = modifier.size(24.dp)) {
        val triangle = Path().apply {
            moveTo(0f, 0f)
            lineTo(size. width * 0.65f, size.height * 0.5f)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = triangle, color = tint)
        val barWidth = size.width * 0.12f
        drawRoundRect(
            color = tint,
            topLeft = Offset(size.width - barWidth, 0f),
            size = Size(barWidth, size.height),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

@Composable
fun SkipPreviousSymbol(modifier: Modifier = Modifier, tint:  Color = Color.White) {
    Canvas(modifier = modifier. size(24.dp)) {
        val triangle = Path().apply {
            moveTo(size.width, 0f)
            lineTo(size.width * 0.35f, size.height * 0.5f)
            lineTo(size.width, size.height)
            close()
        }
        drawPath(path = triangle, color = tint)
        val barWidth = size.width * 0.12f
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(barWidth, size.height),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

@Composable
fun MainOutlinedText(
    text:  String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color. White,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign?  = null
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black. copy(alpha = 0.8f),
                offset = Offset(2f, 2f),
                blurRadius = 4f
            )
        )
    )
}

@Composable
fun MainAnimatedTitle(
    text: String,
    fontSize: TextUnit,
    modifier:  Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "titlePulse")
    val scale by infiniteTransition. animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    MainOutlinedText(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight. ExtraBold,
        color = Color.White,
        modifier = modifier.scale(scale)
    )
}

@Composable
fun OutlinedText(
    text:  String,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.White,
    modifier:  Modifier = Modifier
) {
    MainOutlinedText(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}
@Composable
private fun GeneratedMainMenuPreview(id: Int, modifier: Modifier = Modifier) {
    when (id) {
        11 -> NeonPulseMainMenu(modifier = modifier)
        12 -> GeometricWavesMainMenu(modifier = modifier)
        13 -> ParticleFieldMainMenu(modifier = modifier)
        14 -> GradientMeshMainMenu(modifier = modifier)
        15 -> MatrixRainMainMenu(modifier = modifier)
        16 -> CosmicNebulaMainMenu(modifier = modifier)
        17 -> LiquidMetalMainMenu(modifier = modifier)
        18 -> FireIceMainMenu(modifier = modifier)
        19 -> HoneycombMainMenu(modifier = modifier)
        20 -> AuroraDreamsMainMenu(modifier = modifier)
    }
}
// ==================== MAIN MENU ====================

enum class MainPage { CUSTOMIZE, PLAY, CHALLENGES }

@Composable
fun MainMenu(
    onPlayClicked: () -> Unit,
    onChallengesClicked: () -> Unit,
    highScore: Int,
    coins: Int,
    lux: Int,
    ds:  DataStoreManager
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Volume states
    var musicVolume by remember { mutableFloatStateOf(MusicController.getMusicVolume()) }
    var sfxVolume by remember { mutableFloatStateOf(SoundManager.getSfxVolume()) }
    var isMuted by remember { mutableStateOf(MusicController.isMuted()) }
    var showSettingsMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        MusicController.initIfNeeded(context. applicationContext)
    }

    val equippedMainMenu by ds.equippedMainMenuFlow().collectAsState(initial = 0)
    val storedProfileDrawable by ds. profileDrawableFlow().collectAsState(initial = 0)
    val storedGeneratedAvatarId by ds.generatedAvatarIdFlow().collectAsState(initial = 0)

    val profileDrawableRes = if (storedProfileDrawable != 0) storedProfileDrawable else R.drawable. profileuser0
    val profileDrawablePainter = bitmapPainterResourceSafe(profileDrawableRes)
        ?: bitmapPainterResourceSafe(R.drawable.transparent_placeholder)

    var currentPage by remember { mutableStateOf(MainPage.PLAY) }
    val swipeAnim = remember { Animatable(1f) }
    var showProfileOverlay by remember { mutableStateOf(false) }
    var showStoreOverlay by remember { mutableStateOf(false) }
    var insufficientLuxMessage by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    fun targetForPage(page: MainPage): Float = when (page) {
        MainPage. CUSTOMIZE -> 0f
        MainPage.PLAY -> 1f
        MainPage.CHALLENGES -> 2f
    }

    fun pageForValue(value: Float): MainPage = when {
        value < 0.5f -> MainPage.CUSTOMIZE
        value <= 1.5f -> MainPage.PLAY
        else -> MainPage. CHALLENGES
    }

    fun animateToPage(page: MainPage) {
        showSettingsMenu = false
        val target = targetForPage(page)
        coroutineScope.launch {
            swipeAnim.animateTo(
                target,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
            currentPage = page
        }
    }

    val buyLuxConfirmed:  (Int, String) -> Unit = { luxAmount, _ ->
        coroutineScope.launch { ds.addLux(luxAmount) }
    }

    val buyGoldWithLuxConfirmed: (Int, Int) -> Unit = { luxCost, goldAmount ->
        coroutineScope.launch {
            val success = ds.spendLux(luxCost)
            if (success) {
                ds.addCoins(goldAmount)
            } else {
                insufficientLuxMessage = "No tienes suficientes LUX para esta compra."
            }
        }
    }

    val displayProgress = (swipeAnim.value + dragOffset).coerceIn(0f, 2f)
    val animatedPos by animateFloatAsState(
        targetValue = displayProgress,
        animationSpec = tween(150),
        label = "pageAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { },
                    onHorizontalDrag = { change, dragAmount ->
                        val width = size.width. toFloat()
                        if (width > 0f) {
                            val deltaNormalized = -dragAmount / width * 2f
                            dragOffset = (dragOffset + deltaNormalized).coerceIn(
                                -swipeAnim.value,
                                2f - swipeAnim. value
                            )
                        }
                        change. consume()
                    },
                    onDragEnd = {
                        val finalValue = (swipeAnim. value + dragOffset).coerceIn(0f, 2f)
                        val targetPage = pageForValue(finalValue)
                        dragOffset = 0f
                        animateToPage(targetPage)
                    },
                    onDragCancel = { dragOffset = 0f }
                )
            }
    ) {
        // Render background based on equipped main menu
        if (equippedMainMenu >= 11) {
            // Generated main menu backgrounds (IDs 11-20)
            GeneratedMainMenuPreview(id = equippedMainMenu, modifier = Modifier.fillMaxSize())
        } else if (equippedMainMenu >= 1) {
            // Image backgrounds for IDs 1-10
            val mainmenuDrawable = when (equippedMainMenu) {
                1 -> R.drawable.mainmenu1
                2 -> R.drawable.mainmenu2
                3 -> R.drawable.mainmenu3
                4 -> R.drawable.mainmenu4
                5 -> R.drawable.mainmenu5
                6 -> R.drawable.mainmenu6
                7 -> R.drawable.mainmenu7
                8 -> R.drawable.mainmenu8
                9 -> R.drawable.mainmenu9
                10 -> R.drawable.mainmenu10
                else -> 0
            }
            val mainmenuPainter = bitmapPainterResourceSafe(
                if (mainmenuDrawable != 0) mainmenuDrawable else null
            )
            if (mainmenuPainter != null) {
                Image(
                    painter = mainmenuPainter,
                    contentDescription = "mainmenu_background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                )
            }
        } else {
            // Default gradient background for equippedMainMenu == 0
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
            )
        }
        // Settings Button (only on PLAY page)
        if (currentPage == MainPage.PLAY) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    . padding(top = 20.dp)
                    .size(48.dp)
                    .zIndex(10f)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A2E).copy(alpha = 0.7f))
                    .clickable { showSettingsMenu = true },
                contentAlignment = Alignment.Center
            ) {
                val settingsPainter = bitmapPainterResourceSafe(R. drawable.settingsicon)
                if (settingsPainter != null) {
                    Image(
                        painter = settingsPainter,
                        contentDescription = "settings_button",
                        modifier = Modifier. size(28.dp)
                    )
                }
            }
        }

        // Profile Picture (only on PLAY page) - UPDATED FOR GENERATED AVATARS
        if (currentPage == MainPage.PLAY) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    . padding(top = 40.dp, start = 16.dp)
                    .zIndex(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                        .clickable { showProfileOverlay = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (storedGeneratedAvatarId > 0) {
                        // Mostrar Generated Avatar (nuevo)
                        GeneratedAvatar(
                            avatarId = storedGeneratedAvatarId,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        // Mostrar avatar clásico (drawable)
                        profileDrawablePainter?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = "profile",
                                modifier = Modifier
                                    . size(60.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // Currency Display
        CurrencyDisplay(
            coins = coins,
            lux = lux,
            currentPage = currentPage,
            onBuyClick = { showStoreOverlay = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .zIndex(1f)
        )

        // Settings Menu Overlay
        if (showSettingsMenu && currentPage == MainPage. PLAY) {
            MainMenuSettingsMenu(
                onClose = { showSettingsMenu = false },
                musicVolume = musicVolume,
                onMusicVolumeChange = { newVolume ->
                    musicVolume = newVolume
                    MusicController.setMusicVolume(newVolume)
                },
                sfxVolume = sfxVolume,
                onSfxVolumeChange = { newVolume ->
                    sfxVolume = newVolume
                    SoundManager.setSfxVolume(newVolume)
                },
                isMuted = isMuted,
                onToggleMute = {
                    isMuted = !isMuted
                    MusicController.setMuted(isMuted)
                    SoundManager.setSfxMuted(isMuted)
                },
                musicPlayer = MusicController.get()
            )
        }

        // Profile Overlay
        if (showProfileOverlay) {
            ProfileOverlay(
                profileDrawable = profileDrawableRes,
                pencilDrawable = R.drawable.penciledit,
                onRequestClose = { showProfileOverlay = false },
                onSave = { selectedResId ->
                    coroutineScope.launch { ds.saveProfileDrawable(selectedResId) }
                }
            )
        }

        // Store Overlay
        if (showStoreOverlay) {
            LaunchedEffect(showStoreOverlay) { showProfileOverlay = false }
            FullscreenStoreDialog(
                coins = coins,
                lux = lux,
                onClose = { showStoreOverlay = false },
                onBuyLuxPack = { luxAmount, priceLabel -> buyLuxConfirmed(luxAmount, priceLabel) },
                onBuyGoldWithLux = { luxCost, goldAmount -> buyGoldWithLuxConfirmed(luxCost, goldAmount) }
            )
        }

        // Insufficient Lux Dialog
        insufficientLuxMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { insufficientLuxMessage = null },
                title = {
                    Text(
                        text = "Compra fallida",
                        fontWeight = FontWeight. Bold
                    )
                },
                text = { Text(msg) },
                confirmButton = {
                    TextButton(onClick = { insufficientLuxMessage = null }) {
                        Text("OK")
                    }
                }
            )
        }

        // Page Content
        AnimatedContent(
            targetState = currentPage,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp),
            label = "pageContent"
        ) { page ->
            when (page) {
                MainPage.CUSTOMIZE -> CustomizationScreen()
                MainPage. PLAY -> PlayPageContent(
                    highScore = highScore,
                    equippedMainMenu = equippedMainMenu,
                    onPlayClicked = onPlayClicked
                )
                MainPage.CHALLENGES -> ChallengesPageContent(
                    ds = ds,
                    context = context
                )
            }
        }

        // Bottom Navigation Bar
        BottomBar(
            modifier = Modifier
                .align(Alignment. BottomCenter)
                .padding(12.dp),
            indicatorProgress = animatedPos,
            onSelectCustomize = { animateToPage(MainPage.CUSTOMIZE) },
            onSelectPlay = { animateToPage(MainPage. PLAY) },
            onSelectChallenges = {
                animateToPage(MainPage.CHALLENGES)
                onChallengesClicked()
            }
        )
    }
}

// ==================== CURRENCY DISPLAY ====================

@Composable
fun CurrencyDisplay(
    coins:  Int,
    lux: Int,
    currentPage: MainPage,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHorizontal = currentPage == MainPage.CUSTOMIZE

    if (isHorizontal) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyItem(
                amount = coins,
                iconRes = R.drawable. coin,
                onBuyClick = onBuyClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            CurrencyItem(
                amount = lux,
                iconRes = R.drawable.gemgame,
                onBuyClick = onBuyClick
            )
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.End
        ) {
            CurrencyItem(
                amount = coins,
                iconRes = R.drawable.coin,
                onBuyClick = onBuyClick
            )
            Spacer(modifier = Modifier.height(8.dp))
            CurrencyItem(
                amount = lux,
                iconRes = R.drawable.gemgame,
                onBuyClick = onBuyClick
            )
        }
    }
}

@Composable
fun CurrencyItem(
    amount: Int,
    @DrawableRes iconRes: Int,
    onBuyClick:  () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        bitmapPainterResourceSafe(R.drawable.buymore)?.let { painter ->
            Image(
                painter = painter,
                contentDescription = "buy more",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onBuyClick() }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        MainOutlinedText(
            text = "$amount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        bitmapPainterResourceSafe(iconRes)?.let { painter ->
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier. size(20.dp)
            )
        }
    }
}

// ==================== PLAY PAGE CONTENT ====================

@Composable
fun PlayPageContent(
    highScore: Int,
    equippedMainMenu: Int,
    onPlayClicked:  () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Render background based on equipped main menu
        if (equippedMainMenu >= 11) {
            // Generated main menu backgrounds (IDs 11-20)
            GeneratedMainMenuPreview(id = equippedMainMenu, modifier = Modifier.fillMaxSize())
        } else {
            val mainmenuDrawable = when (equippedMainMenu) {
                1 -> R.drawable.mainmenu1
                2 -> R.drawable.mainmenu2
                3 -> R.drawable.mainmenu3
                4 -> R.drawable.mainmenu4
                5 -> R.drawable.mainmenu5
                6 -> R.drawable.mainmenu6
                7 -> R.drawable.mainmenu7
                8 -> R.drawable.mainmenu8
                9 -> R.drawable.mainmenu9
                10 -> R.drawable.mainmenu10
                else -> 0
            }

            val mainmenuPainter = bitmapPainterResourceSafe(
                if (mainmenuDrawable != 0) mainmenuDrawable else null
            )
            if (mainmenuPainter != null) {
                Image(
                    painter = mainmenuPainter,
                    contentDescription = "mainmenu_decor",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                )
            }
        }

        // Title with bubble background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            bitmapPainterResourceSafe(R.drawable.bubble)?.let { bubblePainter ->
                Image(
                    painter = bubblePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(220.dp)
                        .alpha(0.25f)
                )
            }

            MainAnimatedTitle(
                text = "Pop IT",
                fontSize = 56.sp
            )
        }

        // Play button and record
        Column(
            modifier = Modifier
                . fillMaxWidth()
                .align(Alignment. BottomCenter)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults. cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainOutlinedText(
                        text = "🏆",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier. width(8.dp))
                    MainOutlinedText(
                        text = "Record:  $highScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight. Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onPlayClicked,
                modifier = Modifier
                    .height(80.dp)
                    .width(260.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults. buttonColors(
                    containerColor = Color(0xFFFF6D00)
                ),
                elevation = ButtonDefaults. buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {
                MainOutlinedText(
                    text = "PLAY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==================== CHALLENGES LIST ====================

@Composable
fun ChallengesList(
    ds: DataStoreManager,
    onChallengeClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    // Get high scores for all challenges
    val bubbleKingScore by ds.highScoreBubbleKingFlow().collectAsState(initial = 0)
    val perfectStreakScore by ds.highScorePerfectStreakFlow().collectAsState(initial = 0)
    val timeMasterScore by ds.highScoreTimeMasterFlow().collectAsState(initial = 0)
    val speedDemonScore by ds.highScoreSpeedDemonFlow().collectAsState(initial = 0)
    val accuracyKingScore by ds.highScoreAccuracyKingFlow().collectAsState(initial = 0)
    val enduranceMasterScore by ds.highScoreEnduranceMasterFlow().collectAsState(initial = 0)
    
    // Reward claim states
    val challenge1_30 by ds.isChallengeRewardClaimedFlow(1, 30).collectAsState(initial = false)
    val challenge1_60 by ds.isChallengeRewardClaimedFlow(1, 60).collectAsState(initial = false)
    val challenge1_100 by ds.isChallengeRewardClaimedFlow(1, 100).collectAsState(initial = false)
    
    val challenge2_30 by ds.isChallengeRewardClaimedFlow(2, 30).collectAsState(initial = false)
    val challenge2_60 by ds.isChallengeRewardClaimedFlow(2, 60).collectAsState(initial = false)
    val challenge2_100 by ds.isChallengeRewardClaimedFlow(2, 100).collectAsState(initial = false)
    
    val challenge3_30 by ds.isChallengeRewardClaimedFlow(3, 30).collectAsState(initial = false)
    val challenge3_60 by ds.isChallengeRewardClaimedFlow(3, 60).collectAsState(initial = false)
    val challenge3_100 by ds.isChallengeRewardClaimedFlow(3, 100).collectAsState(initial = false)
    
    val challenge4_30 by ds.isChallengeRewardClaimedFlow(4, 30).collectAsState(initial = false)
    val challenge4_60 by ds.isChallengeRewardClaimedFlow(4, 60).collectAsState(initial = false)
    val challenge4_100 by ds.isChallengeRewardClaimedFlow(4, 100).collectAsState(initial = false)
    
    val challenge5_30 by ds.isChallengeRewardClaimedFlow(5, 30).collectAsState(initial = false)
    val challenge5_60 by ds.isChallengeRewardClaimedFlow(5, 60).collectAsState(initial = false)
    val challenge5_100 by ds.isChallengeRewardClaimedFlow(5, 100).collectAsState(initial = false)
    
    val challenge6_30 by ds.isChallengeRewardClaimedFlow(6, 30).collectAsState(initial = false)
    val challenge6_60 by ds.isChallengeRewardClaimedFlow(6, 60).collectAsState(initial = false)
    val challenge6_100 by ds.isChallengeRewardClaimedFlow(6, 100).collectAsState(initial = false)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        MainOutlinedText(
            text = "🏆 CHALLENGES",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Challenge 1: Bubble King
        ChallengeCard(
            id = 1,
            title = "Bubble King",
            description = "Survive as many waves as possible",
            icon = "👑",
            bestScore = bubbleKingScore,
            targetScore = 30,
            reward30 = "Fire Bubble",
            reward60 = "Starfield Background",
            reward100 = "Neon Pulse Menu",
            claimed30 = challenge1_30,
            claimed60 = challenge1_60,
            claimed100 = challenge1_100,
            onClick = { onChallengeClick(1) }
        )
        
        // Challenge 2: Perfect Streak
        ChallengeCard(
            id = 2,
            title = "Perfect Streak",
            description = "Maintain perfect accuracy",
            icon = "🎯",
            bestScore = perfectStreakScore,
            targetScore = 100,
            reward30 = "Ice Bubble",
            reward60 = "Ocean Waves Background",
            reward100 = "Geometric Waves Menu",
            claimed30 = challenge2_30,
            claimed60 = challenge2_60,
            claimed100 = challenge2_100,
            onClick = { onChallengeClick(2) }
        )
        
        // Challenge 3: Time Master
        ChallengeCard(
            id = 3,
            title = "Time Master",
            description = "Pop bubbles against the clock",
            icon = "⏱️",
            bestScore = timeMasterScore,
            targetScore = 50,
            reward30 = "Electric Bubble",
            reward60 = "Aurora Borealis Background",
            reward100 = "Particle Field Menu",
            claimed30 = challenge3_30,
            claimed60 = challenge3_60,
            claimed100 = challenge3_100,
            onClick = { onChallengeClick(3) }
        )
        
        // Challenge 4: Speed Demon
        ChallengeCard(
            id = 4,
            title = "Speed Demon",
            description = "Pop bubbles as fast as possible",
            icon = "⚡",
            bestScore = speedDemonScore,
            targetScore = 5000,
            reward30 = "Nature Bubble",
            reward60 = "Volcanic Background",
            reward100 = "Gradient Mesh Menu",
            claimed30 = challenge4_30,
            claimed60 = challenge4_60,
            claimed100 = challenge4_100,
            onClick = { onChallengeClick(4) }
        )
        
        // Challenge 5: Accuracy King
        ChallengeCard(
            id = 5,
            title = "Accuracy King",
            description = "Perfect accuracy challenge",
            icon = "🎪",
            bestScore = accuracyKingScore,
            targetScore = 100,
            reward30 = "Galaxy Bubble",
            reward60 = "Cyberpunk City Background",
            reward100 = "Matrix Rain Menu",
            claimed30 = challenge5_30,
            claimed60 = challenge5_60,
            claimed100 = challenge5_100,
            onClick = { onChallengeClick(5) }
        )
        
        // Challenge 6: Endurance Master
        ChallengeCard(
            id = 6,
            title = "Endurance Master",
            description = "Survive without missing",
            icon = "🛡️",
            bestScore = enduranceMasterScore,
            targetScore = 180,
            reward30 = "Lava Bubble",
            reward60 = "Underwater Background",
            reward100 = "Cosmic Nebula Menu",
            claimed30 = challenge6_30,
            claimed60 = challenge6_60,
            claimed100 = challenge6_100,
            onClick = { onChallengeClick(6) }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ChallengeCard(
    id: Int,
    title: String,
    description: String,
    icon: String,
    bestScore: Int,
    targetScore: Int,
    reward30: String,
    reward60: String,
    reward100: String,
    claimed30: Boolean,
    claimed60: Boolean,
    claimed100: Boolean,
    onClick: () -> Unit
) {
    val progress = (bestScore.toFloat() / targetScore.toFloat()).coerceIn(0f, 1f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        MainOutlinedText(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        MainOutlinedText(
                            text = description,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Best score
                Column(horizontalAlignment = Alignment.End) {
                    MainOutlinedText(
                        text = "Best",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    MainOutlinedText(
                        text = "$bestScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainOutlinedText(
                        text = "Progress",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    MainOutlinedText(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = Color(0xFFFF6D00)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFF6D00),
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Rewards
            MainOutlinedText(
                text = "Rewards",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Reward rows
            RewardRow(
                percentage = "30%",
                rewardName = reward30,
                claimed = claimed30,
                achieved = progress >= 0.3f
            )
            Spacer(modifier = Modifier.height(6.dp))
            RewardRow(
                percentage = "60%",
                rewardName = reward60,
                claimed = claimed60,
                achieved = progress >= 0.6f
            )
            Spacer(modifier = Modifier.height(6.dp))
            RewardRow(
                percentage = "100%",
                rewardName = reward100,
                claimed = claimed100,
                achieved = progress >= 1.0f
            )
        }
    }
}

@Composable
fun RewardRow(
    percentage: String,
    rewardName: String,
    claimed: Boolean,
    achieved: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            claimed -> Color(0xFF4CAF50)
                            achieved -> Color(0xFFFF6D00)
                            else -> Color.White.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        claimed -> "✓"
                        achieved -> "!"
                        else -> percentage
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            MainOutlinedText(
                text = rewardName,
                fontSize = 13.sp,
                color = when {
                    claimed -> Color(0xFF4CAF50)
                    achieved -> Color.White
                    else -> Color.White.copy(alpha = 0.5f)
                }
            )
        }
        
        if (claimed) {
            MainOutlinedText(
                text = "Claimed",
                fontSize = 11.sp,
                color = Color(0xFF4CAF50)
            )
        } else if (achieved) {
            MainOutlinedText(
                text = "Ready!",
                fontSize = 11.sp,
                color = Color(0xFFFF6D00),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==================== CHALLENGES PAGE CONTENT ====================

@Composable
fun ChallengesPageContent(
    ds: DataStoreManager,
    context: Context
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ChallengesList(ds = ds) { id ->
            when (id) {
                1 -> context.startActivity(Intent(context, BubbleKingActivity::class.java))
                2 -> context.startActivity(Intent(context, PerfectStreakActivity::class.java))
                3 -> context.startActivity(Intent(context, TimeMasterActivity::class.java))
                4 -> context.startActivity(Intent(context, SpeedDemonActivity::class.java))
                5 -> context.startActivity(Intent(context, AccuracyKingActivity::class.java))
                6 -> context.startActivity(Intent(context, EnduranceMasterActivity::class.java))
            }
        }
    }
}

// ==================== MAIN MENU SETTINGS MENU ====================

@Composable
fun MainMenuSettingsMenu(
    onClose:  () -> Unit,
    musicVolume:  Float,
    onMusicVolumeChange: (Float) -> Unit,
    sfxVolume: Float,
    onSfxVolumeChange: (Float) -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    musicPlayer: MusicPlayer?
) {
    Box(
        modifier = Modifier
            . fillMaxSize()
            .zIndex(50f)
    ) {
        Box(
            modifier = Modifier
                . fillMaxSize()
                .background(Color. Black.copy(alpha = 0.6f))
                .pointerInput(Unit) {
                    detectTapGestures { onClose() }
                }
        )

        Card(
            modifier = Modifier
                . align(Alignment.Center)
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .zIndex(51f)
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainOutlinedText(
                    text = "⚙️ Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier. height(20.dp))

                // Music Player Section
                MusicPlayerSection(musicPlayer = musicPlayer)

                Spacer(modifier = Modifier.height(16.dp))

                // Volume Controls Section
                VolumeControlsSection(
                    musicVolume = musicVolume,
                    onMusicVolumeChange = onMusicVolumeChange,
                    sfxVolume = sfxVolume,
                    onSfxVolumeChange = onSfxVolumeChange
                )

                Spacer(modifier = Modifier. height(16.dp))

                // Mute Section
                MuteSection(
                    isMuted = isMuted,
                    onToggleMute = onToggleMute
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MainOutlinedText(
                        text = "Cerrar",
                        fontSize = 16.sp,
                        color = Color. White. copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ==================== VOLUME CONTROLS SECTION ====================

@Composable
fun VolumeControlsSection(
    musicVolume: Float,
    onMusicVolumeChange: (Float) -> Unit,
    sfxVolume: Float,
    onSfxVolumeChange: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
        ),
        modifier = Modifier. fillMaxWidth()
    ) {
        Column(
            modifier = Modifier. padding(16.dp)
        ) {
            MainOutlinedText(
                text = "🎚️ Volume Controls",
                fontSize = 14.sp,
                color = Color.White. copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Music Volume
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainOutlinedText(
                    text = "🎵 Music",
                    fontSize = 14.sp,
                    fontWeight = FontWeight. Medium,
                    color = Color.White
                )
                MainOutlinedText(
                    text = "${(musicVolume * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6D00)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = musicVolume,
                onValueChange = onMusicVolumeChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF6D00),
                    activeTrackColor = Color(0xFFFF6D00),
                    inactiveTrackColor = Color. White. copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier. height(12.dp))

            // SFX Volume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainOutlinedText(
                    text = "🔊 Sound Effects",
                    fontSize = 14.sp,
                    fontWeight = FontWeight. Medium,
                    color = Color.White
                )
                MainOutlinedText(
                    text = "${(sfxVolume * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
            Spacer(modifier = Modifier. height(4.dp))
            Slider(
                value = sfxVolume,
                onValueChange = onSfxVolumeChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults. colors(
                    thumbColor = Color(0xFF4CAF50),
                    activeTrackColor = Color(0xFF4CAF50),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

// ==================== MUSIC PLAYER SECTION ====================

@Composable
fun MusicPlayerSection(musicPlayer: MusicPlayer?) {
    var elapsed by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }
    var trackName by remember { mutableStateOf(musicPlayer?.getCurrentTitle() ?: "") }
    var isPlaying by remember { mutableStateOf(musicPlayer?.isPlaying() ?: false) }

    LaunchedEffect(musicPlayer) {
        while (true) {
            elapsed = musicPlayer?.getCurrentPositionMillis() ?: 0
            duration = musicPlayer?. getDurationMillis() ?: 0
            trackName = musicPlayer?. getCurrentTitle() ?: ""
            isPlaying = musicPlayer?. isPlaying() ?: false
            delay(300)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier. padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainOutlinedText(
                text = "🎵 Background Music",
                fontSize = 14.sp,
                color = Color.White. copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MainOutlinedText(
                text = trackName. ifEmpty { "No track" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign. Center
            )

            Spacer(modifier = Modifier. height(12.dp))

            val progressFraction = if (duration > 0) {
                (elapsed. toFloat() / duration. toFloat()).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFFFF6D00),
                trackColor = Color. White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMillisDisplay(elapsed),
                    fontSize = 12.sp,
                    color = Color. White.copy(alpha = 0.7f)
                )
                Text(
                    text = formatMillisDisplay(duration),
                    fontSize = 12.sp,
                    color = Color. White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { musicPlayer?.requestPrevious() },
                    modifier = Modifier.size(48.dp)
                ) {
                    SkipPreviousSymbol(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { musicPlayer?.togglePlayPause() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6D00).copy(alpha = 0.2f))
                ) {
                    if (isPlaying) {
                        PauseSymbol(modifier = Modifier. size(28.dp))
                    } else {
                        PlayTriangle(modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(modifier = Modifier. width(16.dp))

                IconButton(
                    onClick = { musicPlayer?. requestNext() },
                    modifier = Modifier. size(48.dp)
                ) {
                    SkipNextSymbol(modifier = Modifier. size(24.dp))
                }
            }
        }
    }
}

// ==================== MUTE SECTION ====================

@Composable
fun MuteSection(
    isMuted: Boolean,
    onToggleMute: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
        ),
        modifier = Modifier. fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp)) {
                    val speakerPainter = bitmapPainterResourceSafe(R.drawable.iconbocina)
                    if (speakerPainter != null) {
                        Image(
                            painter = speakerPainter,
                            contentDescription = "speaker",
                            modifier = Modifier. fillMaxSize()
                        )
                        if (isMuted) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                    color = Color.Red,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 6f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color.Red,
                                    start = Offset(size.width, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = 6f,
                                    cap = StrokeCap. Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier. width(12.dp))

                MainOutlinedText(
                    text = if (isMuted) "All Sound Off" else "All Sound On",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onToggleMute,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults. buttonColors(
                    containerColor = if (isMuted) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )
            ) {
                Text(
                    text = if (isMuted) "Unmute" else "Mute",
                    fontWeight = FontWeight. Bold
                )
            }
        }
    }
}

// Helper function for MusicPlayerSection
private fun formatMillisDisplay(ms: Int): String {
    if (ms <= 0) return "0:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

// ==================== BOTTOM NAVIGATION BAR ====================

@Composable
fun BottomBar(
    indicatorProgress: Float,
    onSelectCustomize: () -> Unit,
    onSelectPlay: () -> Unit,
    onSelectChallenges: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeight = 96.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults. cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarSection(
                label = "Custom",
                icon = "🎨",
                isSelected = indicatorProgress < 0.5f,
                selectedColor = Color(0xFF4CAF50),
                onClick = onSelectCustomize,
                modifier = Modifier.weight(1f)
            )

            PlayButtonSection(
                isSelected = indicatorProgress in 0.5f..1.5f,
                onClick = onSelectPlay,
                modifier = Modifier.weight(1.2f)
            )

            BottomBarSection(
                label = "Challenges",
                icon = "🏆",
                isSelected = indicatorProgress > 1.5f,
                selectedColor = Color(0xFF2196F3),
                onClick = onSelectChallenges,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BottomBarSection(
    label:  String,
    icon: String,
    isSelected: Boolean,
    selectedColor:  Color,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "sectionScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) selectedColor. copy(alpha = 0.15f) else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(selectedColor)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = icon,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = if (isSelected) selectedColor else Color.White. copy(alpha = 0.7f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PlayButtonSection(
    isSelected: Boolean,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "playScale"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFFF6D00))
                )
                Spacer(modifier = Modifier. height(6.dp))
            }

            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(52.dp)
                    .width(140.dp)
                    .scale(scale),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    text = "▶ PLAY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )
            }
        }
    }
}

// ==================== LEGACY SETTINGS MENU (for compatibility) ====================

@Composable
fun SettingsMenu(
    onClose: () -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    musicPlayer: MusicPlayer?
) {
    // Redirect to new settings menu with default values
    MainMenuSettingsMenu(
        onClose = onClose,
        musicVolume = MusicController.getMusicVolume(),
        onMusicVolumeChange = { MusicController.setMusicVolume(it) },
        sfxVolume = SoundManager.getSfxVolume(),
        onSfxVolumeChange = { SoundManager.setSfxVolume(it) },
        isMuted = isMuted,
        onToggleMute = onToggleMute,
        musicPlayer = musicPlayer
    )
}