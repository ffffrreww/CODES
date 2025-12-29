package com.appsdevs.popit

import android.util.Log
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core. RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. animateFloatAsState
import androidx.compose.animation.core. infiniteRepeatable
import androidx.compose. animation.core.rememberInfiniteTransition
import androidx. compose.animation.core.spring
import androidx.compose.animation.core. tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose. foundation.clickable
import androidx.compose. foundation.layout. Arrangement
import androidx.compose.foundation. layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation. layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout. Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout. fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose. foundation.layout.padding
import androidx.compose.foundation.layout. size
import androidx. compose.foundation.layout.width
import androidx.compose.foundation. lazy.grid.GridCells
import androidx.compose. foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation. lazy.grid.items
import androidx.compose.foundation.lazy. grid.rememberLazyGridState
import androidx.compose. foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx. compose.material3.Button
import androidx. compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime. Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime. getValue
import androidx. compose.runtime.mutableIntStateOf
import androidx.compose.runtime. mutableStateOf
import androidx.compose. runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose. ui.Alignment
import androidx.compose. ui.Modifier
import androidx.compose. ui.draw.alpha
import androidx.compose.ui.draw. clip
import androidx. compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Shadow
import androidx.compose.ui.graphics. graphicsLayer
import androidx.compose.ui. layout.ContentScale
import androidx. compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose. ui.text.TextStyle
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit. dp
import androidx. compose.ui.unit.sp
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx. coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainMenuCustomization"

// ==================== MAIN MENU SECTION ====================

@Composable
fun MainMenuSection() {
    val ctx = LocalContext.current
    val ds = remember { DataStoreManager(ctx) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Flows
    val equippedFlow by ds.equippedMainMenuFlow().collectAsState(initial = 0)
    val purchased1Flow by ds.isMainMenuPurchasedFlow(1).collectAsState(initial = false)
    val purchased2Flow by ds. isMainMenuPurchasedFlow(2).collectAsState(initial = false)
    val purchased3Flow by ds.isMainMenuPurchasedFlow(3).collectAsState(initial = false)
    val purchased4Flow by ds.isMainMenuPurchasedFlow(4).collectAsState(initial = false)
    val purchased5Flow by ds.isMainMenuPurchasedFlow(5).collectAsState(initial = false)
    val purchased6Flow by ds.isMainMenuPurchasedFlow(6).collectAsState(initial = false)
    val purchased7Flow by ds. isMainMenuPurchasedFlow(7).collectAsState(initial = false)
    val purchased8Flow by ds.isMainMenuPurchasedFlow(8).collectAsState(initial = false)
    val purchased9Flow by ds.isMainMenuPurchasedFlow(9).collectAsState(initial = false)

    // Level 7 exclusive main menu (ID 10)
    val purchased10Flow by ds. isMainMenuPurchasedFlow(10).collectAsState(initial = false)
    val isLevel7MainMenuUnlocked by ds.isLevel7MainMenuUnlockedFlow().collectAsState(initial = false)

    // Flows for generated main menus 11-20
    val purchased11Flow by ds.isMainMenuPurchasedFlow(11).collectAsState(initial = false)
    val purchased12Flow by ds.isMainMenuPurchasedFlow(12).collectAsState(initial = false)
    val purchased13Flow by ds.isMainMenuPurchasedFlow(13).collectAsState(initial = false)
    val purchased14Flow by ds.isMainMenuPurchasedFlow(14).collectAsState(initial = false)
    val purchased15Flow by ds.isMainMenuPurchasedFlow(15).collectAsState(initial = false)
    val purchased16Flow by ds.isMainMenuPurchasedFlow(16).collectAsState(initial = false)
    val purchased17Flow by ds.isMainMenuPurchasedFlow(17).collectAsState(initial = false)
    val purchased18Flow by ds.isMainMenuPurchasedFlow(18).collectAsState(initial = false)
    val purchased19Flow by ds.isMainMenuPurchasedFlow(19).collectAsState(initial = false)
    val purchased20Flow by ds.isMainMenuPurchasedFlow(20).collectAsState(initial = false)
    // Get current player level
    val totalPops by ds.totalPopsFlow().collectAsState(initial = 0)
    val currentLevel = ds.calculateLevelFromPops(totalPops)

    // Local state
    var equippedLocal by remember { mutableIntStateOf(equippedFlow) }
    LaunchedEffect(equippedFlow) { equippedLocal = equippedFlow }

    var purchased1Local by remember { mutableStateOf(purchased1Flow) }
    var purchased2Local by remember { mutableStateOf(purchased2Flow) }
    var purchased3Local by remember { mutableStateOf(purchased3Flow) }
    var purchased4Local by remember { mutableStateOf(purchased4Flow) }
    var purchased5Local by remember { mutableStateOf(purchased5Flow) }
    var purchased6Local by remember { mutableStateOf(purchased6Flow) }
    var purchased7Local by remember { mutableStateOf(purchased7Flow) }
    var purchased8Local by remember { mutableStateOf(purchased8Flow) }
    var purchased9Local by remember { mutableStateOf(purchased9Flow) }
    var purchased10Local by remember { mutableStateOf(purchased10Flow || isLevel7MainMenuUnlocked) }
    var purchased11Local by remember { mutableStateOf(purchased11Flow) }
    var purchased12Local by remember { mutableStateOf(purchased12Flow) }
    var purchased13Local by remember { mutableStateOf(purchased13Flow) }
    var purchased14Local by remember { mutableStateOf(purchased14Flow) }
    var purchased15Local by remember { mutableStateOf(purchased15Flow) }
    var purchased16Local by remember { mutableStateOf(purchased16Flow) }
    var purchased17Local by remember { mutableStateOf(purchased17Flow) }
    var purchased18Local by remember { mutableStateOf(purchased18Flow) }
    var purchased19Local by remember { mutableStateOf(purchased19Flow) }
    var purchased20Local by remember { mutableStateOf(purchased20Flow) }

    LaunchedEffect(purchased1Flow) { purchased1Local = purchased1Flow }
    LaunchedEffect(purchased2Flow) { purchased2Local = purchased2Flow }
    LaunchedEffect(purchased3Flow) { purchased3Local = purchased3Flow }
    LaunchedEffect(purchased4Flow) { purchased4Local = purchased4Flow }
    LaunchedEffect(purchased5Flow) { purchased5Local = purchased5Flow }
    LaunchedEffect(purchased6Flow) { purchased6Local = purchased6Flow }
    LaunchedEffect(purchased7Flow) { purchased7Local = purchased7Flow }
    LaunchedEffect(purchased8Flow) { purchased8Local = purchased8Flow }
    LaunchedEffect(purchased9Flow) { purchased9Local = purchased9Flow }
    LaunchedEffect(purchased10Flow, isLevel7MainMenuUnlocked) {
        purchased10Local = purchased10Flow || isLevel7MainMenuUnlocked
    }
    LaunchedEffect(purchased11Flow) { purchased11Local = purchased11Flow }
    LaunchedEffect(purchased12Flow) { purchased12Local = purchased12Flow }
    LaunchedEffect(purchased13Flow) { purchased13Local = purchased13Flow }
    LaunchedEffect(purchased14Flow) { purchased14Local = purchased14Flow }
    LaunchedEffect(purchased15Flow) { purchased15Local = purchased15Flow }
    LaunchedEffect(purchased16Flow) { purchased16Local = purchased16Flow }
    LaunchedEffect(purchased17Flow) { purchased17Local = purchased17Flow }
    LaunchedEffect(purchased18Flow) { purchased18Local = purchased18Flow }
    LaunchedEffect(purchased19Flow) { purchased19Local = purchased19Flow }
    LaunchedEffect(purchased20Flow) { purchased20Local = purchased20Flow }

    var selectedMain by remember { mutableIntStateOf(if (equippedLocal > 0) equippedLocal else 0) }

    // Prices - ID 10 has no price (level unlock only), IDs 11-20 are generated main menus
    val prices = mapOf(
        1 to 500, 2 to 1000, 3 to 1500, 4 to 2000, 5 to 4000,
        6 to 50, 7 to 80, 8 to 120, 9 to 200,
        10 to 0, // Level 7 unlock - no price
        11 to 800, 12 to 900, 13 to 1000, 14 to 65, 15 to 1200,
        16 to 75, 17 to 1400, 18 to 85, 19 to 1500, 20 to 95
    )

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmTargetId by remember { mutableIntStateOf(0) }

    var showLevelLockDialog by remember { mutableStateOf(false) }
    var levelLockTargetId by remember { mutableIntStateOf(0) }

    val purchasedFlags = listOf(
        purchased1Local, purchased2Local, purchased3Local, purchased4Local, purchased5Local,
        purchased6Local, purchased7Local, purchased8Local, purchased9Local, purchased10Local,
        purchased11Local, purchased12Local, purchased13Local, purchased14Local, purchased15Local,
        purchased16Local, purchased17Local, purchased18Local, purchased19Local, purchased20Local
    )

    fun isPurchased(id:  Int): Boolean = purchasedFlags. getOrNull(id - 1) ?: false

    fun setPurchased(id:  Int, value: Boolean) {
        when (id) {
            1 -> purchased1Local = value
            2 -> purchased2Local = value
            3 -> purchased3Local = value
            4 -> purchased4Local = value
            5 -> purchased5Local = value
            6 -> purchased6Local = value
            7 -> purchased7Local = value
            8 -> purchased8Local = value
            9 -> purchased9Local = value
            10 -> purchased10Local = value
            11 -> purchased11Local = value
            12 -> purchased12Local = value
            13 -> purchased13Local = value
            14 -> purchased14Local = value
            15 -> purchased15Local = value
            16 -> purchased16Local = value
            17 -> purchased17Local = value
            18 -> purchased18Local = value
            19 -> purchased19Local = value
            20 -> purchased20Local = value
        }
    }

    fun isLevelLockedItem(id: Int): Boolean = id == 10 && ! isLevel7MainMenuUnlocked

    val outerScroll = rememberScrollState()

    Column(
        modifier = Modifier
            . fillMaxWidth()
            .verticalScroll(outerScroll)
            .padding(12.dp)
    ) {
        // Header
        Text(
            text = "🏠 MAIN MENU SHOP",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color. White,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black. copy(alpha = 0.5f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWide = maxWidth > 760.dp

            if (isWide) {
                // Tablet layout
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(0.6f)
                            .height(620.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults. cardElevation(defaultElevation = 8.dp)
                    ) {
                        MMPreviewPanel(
                            selectedMain = selectedMain,
                            isPurchased = isPurchased(selectedMain),
                            isEquipped = (equippedLocal == selectedMain && selectedMain != 0),
                            price = prices[selectedMain] ?: 0,
                            isLevelLocked = isLevelLockedItem(selectedMain),
                            requiredLevel = if (selectedMain == 10) 7 else 0,
                            currentLevel = currentLevel,
                            onBuyOrEquip = {
                                if (selectedMain == 10) {
                                    if (isLevel7MainMenuUnlocked) {
                                        handleMainMenuAction(
                                            selectedMain = selectedMain,
                                            equipped = equippedLocal,
                                            isPurchased = isPurchased(selectedMain),
                                            ds = ds,
                                            coroutineScope = coroutineScope,
                                            snackbarHostState = snackbarHostState,
                                            onEquipChange = { equippedLocal = it },
                                            onShowConfirm = { }
                                        )
                                    } else {
                                        levelLockTargetId = 10
                                        showLevelLockDialog = true
                                    }
                                } else {
                                    handleMainMenuAction(
                                        selectedMain = selectedMain,
                                        equipped = equippedLocal,
                                        isPurchased = isPurchased(selectedMain),
                                        ds = ds,
                                        coroutineScope = coroutineScope,
                                        snackbarHostState = snackbarHostState,
                                        onEquipChange = { equippedLocal = it },
                                        onShowConfirm = { id ->
                                            confirmTargetId = id
                                            showConfirmDialog = true
                                        }
                                    )
                                }
                            },
                            onReset = {
                                coroutineScope.launch {
                                    selectedMain = 0
                                    equippedLocal = 0
                                    withContext(Dispatchers.IO) {
                                        runCatching { ds.resetMainMenuToDefault() }
                                            .onFailure { Log.e(TAG, "resetMainMenuToDefault failed", it) }
                                    }
                                    snackbarHostState. showSnackbar("🔄 Reset to default")
                                }
                            }
                        )
                    }

                    Card(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(620.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        MMSelectorPanel(
                            selectedMain = selectedMain,
                            equippedId = equippedLocal,
                            onSelect = { id -> selectedMain = id },
                            onQuickBuy = { id ->
                                if (id == 10) {
                                    levelLockTargetId = 10
                                    showLevelLockDialog = true
                                } else {
                                    confirmTargetId = id
                                    showConfirmDialog = true
                                }
                            },
                            purchasedFlags = purchasedFlags,
                            prices = prices,
                            isLevel7MainMenuUnlocked = isLevel7MainMenuUnlocked,
                            currentLevel = currentLevel
                        )
                    }
                }
            } else {
                // Phone layout
                Column(
                    modifier = Modifier. fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        MMPreviewPanel(
                            selectedMain = selectedMain,
                            isPurchased = isPurchased(selectedMain),
                            isEquipped = (equippedLocal == selectedMain && selectedMain != 0),
                            price = prices[selectedMain] ?: 0,
                            isLevelLocked = isLevelLockedItem(selectedMain),
                            requiredLevel = if (selectedMain == 10) 7 else 0,
                            currentLevel = currentLevel,
                            onBuyOrEquip = {
                                if (selectedMain == 10) {
                                    if (isLevel7MainMenuUnlocked) {
                                        handleMainMenuAction(
                                            selectedMain = selectedMain,
                                            equipped = equippedLocal,
                                            isPurchased = isPurchased(selectedMain),
                                            ds = ds,
                                            coroutineScope = coroutineScope,
                                            snackbarHostState = snackbarHostState,
                                            onEquipChange = { equippedLocal = it },
                                            onShowConfirm = { }
                                        )
                                    } else {
                                        levelLockTargetId = 10
                                        showLevelLockDialog = true
                                    }
                                } else {
                                    handleMainMenuAction(
                                        selectedMain = selectedMain,
                                        equipped = equippedLocal,
                                        isPurchased = isPurchased(selectedMain),
                                        ds = ds,
                                        coroutineScope = coroutineScope,
                                        snackbarHostState = snackbarHostState,
                                        onEquipChange = { equippedLocal = it },
                                        onShowConfirm = { id ->
                                            confirmTargetId = id
                                            showConfirmDialog = true
                                        }
                                    )
                                }
                            },
                            onReset = {
                                coroutineScope.launch {
                                    selectedMain = 0
                                    equippedLocal = 0
                                    withContext(Dispatchers.IO) {
                                        runCatching { ds. resetMainMenuToDefault() }
                                            .onFailure { Log.e(TAG, "resetMainMenuToDefault", it) }
                                    }
                                    snackbarHostState.showSnackbar("🔄 Reset to default")
                                }
                            }
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        MMSelectorPanel(
                            selectedMain = selectedMain,
                            equippedId = equippedLocal,
                            onSelect = { id -> selectedMain = id },
                            onQuickBuy = { id ->
                                if (id == 10) {
                                    levelLockTargetId = 10
                                    showLevelLockDialog = true
                                } else {
                                    confirmTargetId = id
                                    showConfirmDialog = true
                                }
                            },
                            purchasedFlags = purchasedFlags,
                            prices = prices,
                            isLevel7MainMenuUnlocked = isLevel7MainMenuUnlocked,
                            currentLevel = currentLevel
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier. height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            SnackbarHost(hostState = snackbarHostState)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Purchase Dialog (for buyable items, excluding 10 which is level-locked)
    if (showConfirmDialog && isBuyableMainMenu(confirmTargetId)) {
        MMPurchaseDialog(
            mainMenuId = confirmTargetId,
            price = prices[confirmTargetId] ?:  0,
            isLuxPriced = isLuxPricedMainMenu(confirmTargetId),
            onConfirm = {
                val id = confirmTargetId
                val price = prices[id] ?: 0
                val isLuxPriced = isLuxPricedMainMenu(id)

                setPurchased(id, true)
                equippedLocal = id
                selectedMain = id
                showConfirmDialog = false

                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val ok = if (isLuxPriced) {
                                ds.buyMainMenuWithLux(id, price)
                            } else {
                                ds.buyMainMenu(id, price)
                            }
                            if (ok) {
                                snackbarHostState.showSnackbar("🎉 Purchased!")
                            } else {
                                val actualPurchased = ds.isMainMenuPurchasedFlow(id).first()
                                val actualEquipped = ds.equippedMainMenuFlow().first()
                                setPurchased(id, actualPurchased)
                                equippedLocal = actualEquipped
                                snackbarHostState. showSnackbar("❌ Not enough resources")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "buyMainMenu exception", e)
                            val actualPurchased = ds.isMainMenuPurchasedFlow(id).first()
                            val actualEquipped = ds. equippedMainMenuFlow().first()
                            setPurchased(id, actualPurchased)
                            equippedLocal = actualEquipped
                            snackbarHostState.showSnackbar("Error: ${e.localizedMessage ?: "unknown"}")
                        }
                    }
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    // Level Lock Dialog
    if (showLevelLockDialog) {
        MMLevelLockDialog(
            requiredLevel = 7,
            currentLevel = currentLevel,
            itemName = getMainMenuName(levelLockTargetId),
            onDismiss = { showLevelLockDialog = false }
        )
    }
}

// ==================== HELPER FUNCTION ====================

private fun handleMainMenuAction(
    selectedMain: Int,
    equipped:  Int,
    isPurchased: Boolean,
    ds: DataStoreManager,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState:  SnackbarHostState,
    onEquipChange: (Int) -> Unit,
    onShowConfirm: (Int) -> Unit
) {
    coroutineScope.launch {
        try {
            if (selectedMain == 0) {
                snackbarHostState. showSnackbar("Select a main menu first")
                return@launch
            }

            if (equipped == selectedMain) {
                onEquipChange(0)
                withContext(Dispatchers.IO) {
                    runCatching { ds.equipMainMenu(0) }
                        .onFailure { Log.e(TAG, "equipMainMenu(0) failed", it) }
                }
                snackbarHostState.showSnackbar("Unequipped")
            } else if (isPurchased) {
                onEquipChange(selectedMain)
                val ok = withContext(Dispatchers.IO) {
                    runCatching { ds.equipMainMenu(selectedMain) }
                        .getOrElse { e -> Log.e(TAG, "equipMainMenu($selectedMain) exception", e); false }
                }
                if (ok) {
                    snackbarHostState.showSnackbar("✅ Equipped!")
                } else {
                    val actual = ds.equippedMainMenuFlow().first()
                    onEquipChange(actual)
                    snackbarHostState.showSnackbar("Cannot equip")
                }
            } else {
                onShowConfirm(selectedMain)
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleMainMenuAction failed", e)
            snackbarHostState.showSnackbar("Error: ${e.localizedMessage ?: "unknown"}")
        }
    }
}

// ==================== HELPER FUNCTIONS ====================

private fun getMainMenuDrawable(id: Int): Int = when (id) {
    1 -> R.drawable.mainmenu1
    2 -> R.drawable.mainmenu2
    3 -> R. drawable.mainmenu3
    4 -> R.drawable. mainmenu4
    5 -> R.drawable.mainmenu5
    6 -> R.drawable.mainmenu6
    7 -> R.drawable.mainmenu7
    8 -> R. drawable.mainmenu8
    9 -> R.drawable. mainmenu9
    10 -> R.drawable.mainmenu10 // Level 7 exclusive
    else -> 0
}

private fun getMainMenuName(id: Int): String = when (id) {
    1 -> "Classic Vibes"
    2 -> "Sunset Street"
    3 -> "Happy Sunset"
    4 -> "Bamboo"
    5 -> "Cyberpunk City"
    6 -> "Night Life"
    7 -> "Future Night"
    8 -> "Feather Girl"
    9 -> "Space Experience"
    10 -> "🌟 Level 7 Exclusive"
    11 -> "✨ Neon Pulse"
    12 -> "✨ Geometric Waves"
    13 -> "✨ Particle Field"
    14 -> "✨ Gradient Mesh"
    15 -> "✨ Matrix Rain"
    16 -> "✨ Cosmic Nebula"
    17 -> "✨ Liquid Metal"
    18 -> "✨ Fire & Ice"
    19 -> "✨ Honeycomb"
    20 -> "✨ Aurora Dreams"
    else -> "Default"
}

private fun getMainMenuRarity(id: Int): MMRarity = when (id) {
    1, 2 -> MMRarity. COMMON
    3, 4 -> MMRarity.RARE
    5 -> MMRarity. EPIC
    6, 7 -> MMRarity.LEGENDARY
    8, 9 -> MMRarity.MYTHIC
    10 -> MMRarity. EXCLUSIVE
    11, 12 -> MMRarity.RARE
    13, 14 -> MMRarity.EPIC
    15, 16, 17 -> MMRarity.LEGENDARY
    18, 19 -> MMRarity.MYTHIC
    20 -> MMRarity.EXCLUSIVE
    else -> MMRarity.COMMON
}
private fun isGeneratedMainMenu(id: Int): Boolean = id in 11..20

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

private fun isLuxPricedMainMenu(id: Int): Boolean = when(id) {
    6, 7, 8, 9 -> true  // Original lux items
    14, 16, 18, 20 -> true  // New generated lux items
    else -> false
}

private fun isBuyableMainMenu(id: Int): Boolean = id in 1..20 && id != 10

private enum class MMRarity(val color: Color, val label: String) {
    COMMON(Color(0xFF9E9E9E), "Common"),
    RARE(Color(0xFF2196F3), "Rare"),
    EPIC(Color(0xFF9C27B0), "Epic"),
    LEGENDARY(Color(0xFFFFD700), "Legendary"),
    MYTHIC(Color(0xFFFF1744), "Mythic"),
    EXCLUSIVE(Color(0xFF00E676), "Exclusive")
}

// ==================== STYLED TEXT ====================

@Composable
private fun MMStyledText(
    text: String,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    color:  Color = Color.White,
    modifier:  Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize. sp,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.6f),
                offset = Offset(1f, 1f),
                blurRadius = 2f
            )
        )
    )
}
// ==================== PREVIEW PANEL ====================

@Composable
private fun MMPreviewPanel(
    selectedMain: Int,
    isPurchased: Boolean,
    isEquipped: Boolean,
    price: Int,
    isLevelLocked: Boolean = false,
    requiredLevel: Int = 0,
    currentLevel: Int = 1,
    onBuyOrEquip: () -> Unit,
    onReset: () -> Unit
) {
    val rarity = getMainMenuRarity(selectedMain)
    val mmName = getMainMenuName(selectedMain)

    val infiniteTransition = rememberInfiniteTransition(label = "mmFloat")
    val floatOffset by infiniteTransition. animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "float"
    )

    Column(
        modifier = Modifier
            . fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A1A4A).copy(alpha = 0.5f),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement. SpaceBetween,
            verticalAlignment = Alignment. CenterVertically
        ) {
            Column {
                MMStyledText(
                    text = "🏠 Preview",
                    fontSize = 12,
                    color = Color.White. copy(alpha = 0.7f)
                )
                MMStyledText(
                    text = mmName,
                    fontSize = 18,
                    fontWeight = FontWeight. Bold
                )
            }

            if (selectedMain > 0) {
                MMRarityBadge(rarity = rarity)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preview Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            rarity.color. copy(alpha = 0.1f),
                            Color(0xFF0A0A15).copy(alpha = 0.9f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            rarity.color.copy(alpha = 0.5f),
                            rarity.color.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Particles
            MMParticlesBackground(rarityColor = rarity.color)

            val drawableId = getMainMenuDrawable(selectedMain)
            val isGenerated = isGeneratedMainMenu(selectedMain)
            if (selectedMain == 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏠", fontSize = 48. sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    MMStyledText(
                        text = "Default Main Menu",
                        fontSize = 14,
                        color = Color.White. copy(alpha = 0.6f)
                    )
                }
            } else if (isGenerated) {
                // Show generated main menu
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (isLevelLocked) 0.5f else 1f)
                ) {
                    GeneratedMainMenuPreview(id = selectedMain, modifier = Modifier.fillMaxSize())
                }
            } else if (drawableId != 0) {
                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "mainmenu_preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .graphicsLayer {
                            translationY = floatOffset
                        }
                        .alpha(if (isLevelLocked) 0.5f else 1f),
                    contentScale = ContentScale. Fit
                )
            }

            // Level lock overlay
            if (isLevelLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        . background(Color. Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔒", fontSize = 48.sp)
                        Spacer(modifier = Modifier. height(8.dp))
                        MMStyledText(
                            text = "Reach Level $requiredLevel",
                            fontSize = 16,
                            fontWeight = FontWeight. Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        MMStyledText(
                            text = "Current:  Level $currentLevel",
                            fontSize = 12,
                            color = Color. White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Animated badge for generated main menus
                if (isGenerated && !isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF6D00))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✨ ANIMATED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Equipped badge
            if (isEquipped && ! isLevelLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        . padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ EQUIPPED",
                        fontSize = 10. sp,
                        fontWeight = FontWeight. Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Info
        if (selectedMain > 0) {
            if (isLevelLocked) {
                MMLevelLockInfo(requiredLevel = requiredLevel, currentLevel = currentLevel)
            } else {
                MMStatusInfo(
                    isPurchased = isPurchased,
                    isEquipped = isEquipped,
                    price = price,
                    isLuxPriced = selectedMain in 6..9,
                    isLevelUnlock = selectedMain == 10
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBuyOrEquip,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        selectedMain == 0 -> Color(0xFF666666)
                        isLevelLocked -> Color(0xFF9C27B0)
                        ! isPurchased -> Color(0xFFFF6D00)
                        isEquipped -> Color(0xFFFF5252)
                        else -> Color(0xFF4CAF50)
                    }
                ),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 4.dp)
            ) {
                when {
                    selectedMain == 0 -> {
                        MMStyledText(text = "Select", fontSize = 14, fontWeight = FontWeight. Bold)
                    }
                    isLevelLocked -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            MMStyledText(text = "Level $requiredLevel Required", fontSize = 12, fontWeight = FontWeight. Bold)
                        }
                    }
                    !isPurchased && selectedMain != 10 -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MMStyledText(text = "BUY $price", fontSize = 14, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(
                                    id = if (selectedMain >= 6) R.drawable.gemgame else R.drawable. coin
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    isEquipped -> {
                        MMStyledText(text = "UNEQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                    else -> {
                        MMStyledText(text = "✓ EQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = onReset,
                modifier = Modifier
                    .width(70.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 4.dp)
            ) {
                MMStyledText(text = "↺", fontSize = 20, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== LEVEL LOCK INFO ====================

@Composable
private fun MMLevelLockInfo(requiredLevel: Int, currentLevel: Int) {
    val progress = (currentLevel. toFloat() / requiredLevel.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐", fontSize = 16. sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Level $requiredLevel Exclusive",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier. height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color. White. copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        . height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your Level: $currentLevel / $requiredLevel",
                fontSize = 11.sp,
                color = Color. White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier. height(4.dp))

            Text(
                text = "🎮 Keep playing to level up!",
                fontSize = 10.sp,
                color = Color(0xFF00E676)
            )
        }
    }
}

// ==================== RARITY BADGE ====================

@Composable
private fun MMRarityBadge(rarity: MMRarity) {
    val infiniteTransition = rememberInfiniteTransition(label = "rarityGlow")
    val glowAlpha by infiniteTransition. animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            . clip(RoundedCornerShape(8.dp))
            .background(rarity.color. copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = rarity. color.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = rarity. label. uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = rarity.color
        )
    }
}

// ==================== STATUS INFO ====================

@Composable
private fun MMStatusInfo(
    isPurchased: Boolean,
    isEquipped: Boolean,
    price: Int,
    isLuxPriced: Boolean,
    isLevelUnlock: Boolean = false
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                . fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Status",
                    fontSize = 11.sp,
                    color = Color. White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when {
                        isEquipped -> "Equipped"
                        isPurchased -> "Owned"
                        else -> "Locked"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight. Bold,
                    color = when {
                        isEquipped -> Color(0xFF4CAF50)
                        isPurchased -> Color(0xFF2196F3)
                        else -> Color(0xFFFF5252)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isLevelUnlock) "Unlock" else "Price",
                    fontSize = 11.sp,
                    color = Color. White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                if (isLevelUnlock) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPurchased) "UNLOCKED" else "Level 7",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700)
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPurchased) "OWNED" else "$price",
                            fontSize = 13.sp,
                            fontWeight = FontWeight. Bold,
                            color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700)
                        )
                        if (! isPurchased) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                painter = painterResource(
                                    id = if (isLuxPriced) R.drawable.gemgame else R. drawable.coin
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== PARTICLES BACKGROUND ====================

@Composable
private fun MMParticlesBackground(rarityColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particle1Y by infiniteTransition. animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "p1"
    )

    val particle2Y by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, delayMillis = 500, easing = EaseInOutSine),
            repeatMode = RepeatMode. Restart
        ),
        label = "p2"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
        val particleColor = rarityColor. copy(alpha = 0.4f)

        drawCircle(
            color = particleColor,
            radius = 8f,
            center = Offset(size.width * 0.15f, size.height * particle1Y)
        )

        drawCircle(
            color = particleColor,
            radius = 5f,
            center = Offset(size.width * 0.8f, size.height * particle2Y)
        )

        drawCircle(
            color = particleColor,
            radius = 10f,
            center = Offset(size.width * 0.5f, size.height * ((particle1Y + particle2Y) / 2))
        )
    }
}

// ==================== SELECTOR PANEL ====================

@Composable
private fun MMSelectorPanel(
    selectedMain:  Int,
    equippedId: Int,
    onSelect: (Int) -> Unit,
    onQuickBuy: (Int) -> Unit,
    purchasedFlags: List<Boolean>,
    prices: Map<Int, Int>,
    isLevel7MainMenuUnlocked: Boolean = false,
    currentLevel: Int = 1
) {
    // Include IDs 1-20 (includes Level 7 exclusive ID 10)
    val mainList = (1..20).toList()
    val gridState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF2A1A4A).copy(alpha = 0.5f)
                    )
                )
            )
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MMStyledText(
                text = "🎯 Collection",
                fontSize = 16,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${purchasedFlags.count { it }}/${mainList.size}",
                fontSize = 12. sp,
                color = Color.White. copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mainList) { id ->
                val isLevelLockItem = id == 10
                val isPurchased = if (isLevelLockItem) isLevel7MainMenuUnlocked else (purchasedFlags.getOrNull(id - 1) ?: false)

                MMSelectorItem(
                    id = id,
                    name = getMainMenuName(id),
                    drawableId = getMainMenuDrawable(id),
                    rarity = getMainMenuRarity(id),
                    isPurchased = isPurchased,
                    isEquipped = equippedId == id,
                    isSelected = selectedMain == id,
                    price = prices[id] ?: 0,
                    isLevelLocked = isLevelLockItem && !isLevel7MainMenuUnlocked,
                    requiredLevel = if (isLevelLockItem) 7 else 0,
                    currentLevel = currentLevel,
                    onSelect = { onSelect(id) },
                    onQuickBuy = { onQuickBuy(id) }
                )
            }
        }
    }
}

// ==================== SELECTOR ITEM ====================

@Composable
private fun MMSelectorItem(
    id: Int,
    name: String,
    drawableId:  Int,
    rarity: MMRarity,
    isPurchased: Boolean,
    isEquipped:  Boolean,
    isSelected: Boolean,
    price: Int,
    isLevelLocked: Boolean = false,
    requiredLevel: Int = 0,
    currentLevel: Int = 1,
    onSelect: () -> Unit,
    onQuickBuy:  () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "itemScale"
    )
    val isLuxPriced = id in 6..9

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scale)
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                rarity.color.copy(alpha = 0.15f)
            else
                Color(0xFF0F0F1A).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults. cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(rarity.color, rarity.color.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else Modifier
                )
        ) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color(0xFF0A0A15)),
                contentAlignment = Alignment.Center
            ) {
                val isGenerated = isGeneratedMainMenu(id)

                if (isGenerated) {
                    // Show generated main menu preview
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isLevelLocked) 0.4f else 1f)
                    ) {
                        GeneratedMainMenuPreview(id = id, modifier = Modifier.fillMaxSize())
                    }
                } else if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "mainmenu_$id",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isLevelLocked) 0.4f else 1f),
                        contentScale = ContentScale. Crop
                    )
                }

                // Lock overlay
                if (isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            . background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment. Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐", fontSize = 20. sp)
                            Text(
                                text = "Lvl $requiredLevel",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                } else if (! isPurchased) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            . background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔒", fontSize = 24.sp)
                    }
                }

                // Equipped badge
                if (isEquipped && !isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }
                }

                // Rarity indicator
                Box(
                    modifier = Modifier
                        . align(Alignment.TopStart)
                        . padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(rarity.color)
                )
            }

            // Info bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0F1A).copy(alpha = 0.9f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price/Status
                if (isLevelLocked) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onQuickBuy() }
                    ) {
                        Text(text = "⭐", fontSize = 10.sp)
                        Spacer(modifier = Modifier. width(3.dp))
                        Text(
                            text = "Lvl $requiredLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                } else if (isPurchased) {
                    Text(
                        text = "✓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onQuickBuy() }
                    ) {
                        Text(
                            text = "$price",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Image(
                            painter = painterResource(
                                id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                            ),
                            contentDescription = null,
                            modifier = Modifier. size(12.dp)
                        )
                    }
                }

                // Rarity label
                Text(
                    text = rarity.label,
                    fontSize = 9.sp,
                    color = rarity.color
                )
            }
        }
    }
}

// ==================== LEVEL LOCK DIALOG ====================

@Composable
private fun MMLevelLockDialog(
    requiredLevel: Int,
    currentLevel:  Int,
    itemName: String,
    onDismiss:  () -> Unit
) {
    val progress = (currentLevel. toFloat() / requiredLevel.toFloat()).coerceIn(0f, 1f)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier. fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "⭐", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Level $requiredLevel Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight. Bold,
                    color = Color(0xFFFFD700)
                )

                Spacer(modifier = Modifier. height(4.dp))

                Text(
                    text = itemName,
                    fontSize = 14.sp,
                    color = Color. White.copy(alpha = 0.8f)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment. CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Progress",
                            fontSize = 12.sp,
                            color = Color.White. copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Level display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$currentLevel",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF6D00)
                            )
                            Text(
                                text = " / $requiredLevel",
                                fontSize = 20.sp,
                                fontWeight = FontWeight. Bold,
                                color = Color. White.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                . fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color. White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(progress * 100).toInt()}% Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Spacer(modifier = Modifier. height(16.dp))

                Text(
                    text = "🎮 Keep playing games to earn XP and level up!",
                    fontSize = 12.sp,
                    color = Color(0xFF00E676),
                    textAlign = TextAlign. Center
                )

                Spacer(modifier = Modifier. height(8.dp))

                Text(
                    text = "This exclusive main menu can only be unlocked by reaching Level $requiredLevel.  It cannot be purchased with coins or gems.",
                    fontSize = 11.sp,
                    color = Color. White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                )
            ) {
                Text(
                    text = "Got it!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )
            }
        }
    )
}

// ==================== PURCHASE DIALOG ====================

@Composable
private fun MMPurchaseDialog(
    mainMenuId: Int,
    price: Int,
    isLuxPriced: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarity = getMainMenuRarity(mainMenuId)
    val mmName = getMainMenuName(mainMenuId)
    val drawableId = getMainMenuDrawable(mainMenuId)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛒 Confirm Purchase",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier. height(12.dp))

                // Preview
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(rarity.color.copy(alpha = 0.15f))
                        .border(
                            width = 2.dp,
                            color = rarity.color.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (drawableId != 0) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale. Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mmName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier.height(4.dp))

                MMRarityBadge(rarity = rarity)
            }
        },
        text = {
            Card(
                modifier = Modifier. fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        . padding(12.dp),
                    horizontalArrangement = Arrangement. Center,
                    verticalAlignment = Alignment. CenterVertically
                ) {
                    Text(
                        text = "Price: ",
                        fontSize = 14.sp,
                        color = Color.White. copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$price",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        painter = painterResource(
                            id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "✓ Purchase",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 14.sp,
                    color = Color.White. copy(alpha = 0.8f)
                )
            }
        }
    )
}