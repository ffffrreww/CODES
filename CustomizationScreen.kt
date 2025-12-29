package com.appsdevs.popit

import androidx.compose. animation.core.EaseInOutSine
import androidx.compose.animation.core. RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation. core.rememberInfiniteTransition
import androidx.compose. animation.core.spring
import androidx.compose.animation.core. tween
import androidx. compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose. foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout. Box
import androidx. compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation. layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout. Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx. compose.foundation.layout.fillMaxWidth
import androidx.compose. foundation.layout.height
import androidx.compose.foundation.layout. padding
import androidx. compose.foundation.layout.size
import androidx.compose.foundation. layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose. foundation.lazy.grid.LazyVerticalGrid
import androidx. compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy. grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
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
import androidx. compose.material3.Text
import androidx. compose.runtime. Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime. getValue
import androidx. compose.runtime.mutableIntStateOf
import androidx.compose.runtime. mutableStateListOf
import androidx. compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose. runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text. style.TextAlign
import androidx.compose.ui.unit. Dp
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx. coroutines.launch
import kotlinx.coroutines.withContext

// ==================== CUSTOMIZATION SCREEN ====================

enum class CustomizeSection { BACKGROUND, BUBBLE, MAIN_MENU }

@Composable
fun CustomizationScreen() {
    val ctx = LocalContext.current
    val ds = remember { DataStoreManager(ctx) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val topReserved = 50.dp

    // Flows para backgrounds 1-10
    val equippedFlow by ds.equippedBackgroundFlow().collectAsState(initial = 0)
    val purchased1Flow by ds.isBackgroundPurchasedFlow(1).collectAsState(initial = false)
    val purchased2Flow by ds. isBackgroundPurchasedFlow(2).collectAsState(initial = false)
    val purchased3Flow by ds.isBackgroundPurchasedFlow(3).collectAsState(initial = false)
    val purchased4Flow by ds.isBackgroundPurchasedFlow(4).collectAsState(initial = false)
    val purchased5Flow by ds.isBackgroundPurchasedFlow(5).collectAsState(initial = false)
    val purchased6Flow by ds.isBackgroundPurchasedFlow(6).collectAsState(initial = false)
    val purchased7Flow by ds. isBackgroundPurchasedFlow(7).collectAsState(initial = false)
    val purchased8Flow by ds.isBackgroundPurchasedFlow(8).collectAsState(initial = false)
    val purchased9Flow by ds.isBackgroundPurchasedFlow(9).collectAsState(initial = false)
    val purchased10Flow by ds.isBackgroundPurchasedFlow(10).collectAsState(initial = false)

    // Flow para background 11 (Level 7 unlock)
    val purchased11Flow by ds. isBackgroundPurchasedFlow(11).collectAsState(initial = false)
    val isLevel7BgUnlocked by ds. isLevel7BackgroundUnlockedFlow().collectAsState(initial = false)

    // Flows for generated backgrounds 12-21
    val purchased12Flow by ds.isBackgroundPurchasedFlow(12).collectAsState(initial = false)
    val purchased13Flow by ds.isBackgroundPurchasedFlow(13).collectAsState(initial = false)
    val purchased14Flow by ds.isBackgroundPurchasedFlow(14).collectAsState(initial = false)
    val purchased15Flow by ds.isBackgroundPurchasedFlow(15).collectAsState(initial = false)
    val purchased16Flow by ds.isBackgroundPurchasedFlow(16).collectAsState(initial = false)
    val purchased17Flow by ds.isBackgroundPurchasedFlow(17).collectAsState(initial = false)
    val purchased18Flow by ds.isBackgroundPurchasedFlow(18).collectAsState(initial = false)
    val purchased19Flow by ds.isBackgroundPurchasedFlow(19).collectAsState(initial = false)
    val purchased20Flow by ds.isBackgroundPurchasedFlow(20).collectAsState(initial = false)
    val purchased21Flow by ds.isBackgroundPurchasedFlow(21).collectAsState(initial = false)

    // Get current player level
    val totalPops by ds.totalPopsFlow().collectAsState(initial = 0)
    val currentLevel = ds.calculateLevelFromPops(totalPops)

    val purchasedFlagsCollected = listOf(
        purchased1Flow, purchased2Flow, purchased3Flow, purchased4Flow, purchased5Flow,
        purchased6Flow, purchased7Flow, purchased8Flow, purchased9Flow, purchased10Flow,
        purchased11Flow || isLevel7BgUnlocked,
        purchased12Flow, purchased13Flow, purchased14Flow, purchased15Flow, purchased16Flow,
        purchased17Flow, purchased18Flow, purchased19Flow, purchased20Flow, purchased21Flow
    )

    val localPurchasedFlags = remember { mutableStateListOf<Boolean>().apply { repeat(21) { add(false) } } }
    LaunchedEffect(purchasedFlagsCollected) {
        purchasedFlagsCollected.forEachIndexed { i, v ->
            if (localPurchasedFlags. getOrNull(i) != v) localPurchasedFlags[i] = v
        }
    }

    var equipped by remember { mutableIntStateOf(equippedFlow) }
    LaunchedEffect(equippedFlow) { equipped = equippedFlow }

    var selectedSection by remember { mutableStateOf(CustomizeSection.BACKGROUND) }
    var selectedBg by remember { mutableIntStateOf(if (equipped > 0) equipped else 0) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmTargetId by remember { mutableIntStateOf(0) }

    var showLevelLockDialog by remember { mutableStateOf(false) }
    var levelLockTargetId by remember { mutableIntStateOf(0) }

    LaunchedEffect(equipped) {
        if (selectedBg == 0) selectedBg = equipped
    }

    // Prices - ID 11 has no price (level unlock only), IDs 12-21 are generated backgrounds
    val prices = remember {
        mapOf(
            1 to 300, 2 to 600, 3 to 1000, 4 to 1500, 5 to 2000, 6 to 4000,
            7 to 30, 8 to 45, 9 to 75, 10 to 120,
            11 to 0, // Level 7 unlock - no price
            12 to 600, 13 to 800, 14 to 50, 15 to 75, 16 to 1200,
            17 to 85, 18 to 1500, 19 to 1800, 20 to 100, 21 to 100
        )
    }

    Column(
        modifier = Modifier
            . fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topReserved))

        // Section Chips
        Row(
            modifier = Modifier
                . fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BGCustomSectionChip(
                label = "Back\nground",
                icon = "🖼️",
                selected = selectedSection == CustomizeSection. BACKGROUND,
                onClick = { selectedSection = CustomizeSection. BACKGROUND },
                modifier = Modifier.weight(1f)
            )
            BGCustomSectionChip(
                label = "Bubble",
                icon = "🫧",
                selected = selectedSection == CustomizeSection.BUBBLE,
                onClick = { selectedSection = CustomizeSection.BUBBLE },
                modifier = Modifier.weight(1f)
            )
            BGCustomSectionChip(
                label = "Main\nMenu",
                icon = "🏠",
                selected = selectedSection == CustomizeSection.MAIN_MENU,
                onClick = { selectedSection = CustomizeSection. MAIN_MENU },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWide = maxWidth > 760.dp

            when (selectedSection) {
                CustomizeSection.BACKGROUND -> {
                    val sectionScroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            . verticalScroll(sectionScroll)
                    ) {
                        if (isWide) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement. spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .height(700.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                                    ),
                                    elevation = CardDefaults. cardElevation(defaultElevation = 8.dp)
                                ) {
                                    val isSelectedPurchased = localPurchasedFlags. getOrNull(selectedBg - 1) ?: false
                                    val isLevelLocked = selectedBg == 11 && ! isLevel7BgUnlocked

                                    BGPreviewPanel(
                                        selectedBg = selectedBg,
                                        isPurchased = isSelectedPurchased,
                                        isEquipped = (equipped == selectedBg && selectedBg != 0),
                                        price = prices[selectedBg] ?: 0,
                                        isLevelLocked = isLevelLocked,
                                        requiredLevel = if (selectedBg == 11) 7 else 0,
                                        currentLevel = currentLevel,
                                        onBuyOrEquip = {
                                            if (selectedBg == 11) {
                                                if (isLevel7BgUnlocked) {
                                                    // Can equip
                                                    handleBackgroundAction(
                                                        selectedBg = selectedBg,
                                                        equipped = equipped,
                                                        localPurchasedFlags = localPurchasedFlags,
                                                        ds = ds,
                                                        coroutineScope = coroutineScope,
                                                        snackbarHostState = snackbarHostState,
                                                        onEquipChange = { equipped = it },
                                                        onShowConfirm = { }
                                                    )
                                                } else {
                                                    // Show level lock dialog
                                                    levelLockTargetId = 11
                                                    showLevelLockDialog = true
                                                }
                                            } else {
                                                handleBackgroundAction(
                                                    selectedBg = selectedBg,
                                                    equipped = equipped,
                                                    localPurchasedFlags = localPurchasedFlags,
                                                    ds = ds,
                                                    coroutineScope = coroutineScope,
                                                    snackbarHostState = snackbarHostState,
                                                    onEquipChange = { equipped = it },
                                                    onShowConfirm = { id ->
                                                        confirmTargetId = id
                                                        showConfirmDialog = true
                                                    }
                                                )
                                            }
                                        },
                                        onReset = {
                                            selectedBg = 0
                                            equipped = 0
                                            coroutineScope.launch {
                                                withContext(Dispatchers.IO) { ds.resetBackgroundToDefault() }
                                                snackbarHostState. showSnackbar("🔄 Reset to default")
                                            }
                                        }
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .weight(0.4f)
                                        .height(700.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults. cardColors(
                                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    BGSelectorPanel(
                                        selectedBg = selectedBg,
                                        equippedId = equipped,
                                        onSelect = { id -> selectedBg = id },
                                        onQuickBuy = { id ->
                                            if (id == 11) {
                                                levelLockTargetId = 11
                                                showLevelLockDialog = true
                                            } else {
                                                confirmTargetId = id
                                                showConfirmDialog = true
                                            }
                                        },
                                        purchasedFlags = localPurchasedFlags,
                                        prices = prices,
                                        isLevel7BgUnlocked = isLevel7BgUnlocked,
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
                                    colors = CardDefaults. cardColors(
                                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    val isSelectedPurchased = localPurchasedFlags.getOrNull(selectedBg - 1) ?: false
                                    val isLevelLocked = selectedBg == 11 && !isLevel7BgUnlocked

                                    BGPreviewPanel(
                                        selectedBg = selectedBg,
                                        isPurchased = isSelectedPurchased,
                                        isEquipped = (equipped == selectedBg && selectedBg != 0),
                                        price = prices[selectedBg] ?:  0,
                                        isLevelLocked = isLevelLocked,
                                        requiredLevel = if (selectedBg == 11) 7 else 0,
                                        currentLevel = currentLevel,
                                        onBuyOrEquip = {
                                            if (selectedBg == 11) {
                                                if (isLevel7BgUnlocked) {
                                                    handleBackgroundAction(
                                                        selectedBg = selectedBg,
                                                        equipped = equipped,
                                                        localPurchasedFlags = localPurchasedFlags,
                                                        ds = ds,
                                                        coroutineScope = coroutineScope,
                                                        snackbarHostState = snackbarHostState,
                                                        onEquipChange = { equipped = it },
                                                        onShowConfirm = { }
                                                    )
                                                } else {
                                                    levelLockTargetId = 11
                                                    showLevelLockDialog = true
                                                }
                                            } else {
                                                handleBackgroundAction(
                                                    selectedBg = selectedBg,
                                                    equipped = equipped,
                                                    localPurchasedFlags = localPurchasedFlags,
                                                    ds = ds,
                                                    coroutineScope = coroutineScope,
                                                    snackbarHostState = snackbarHostState,
                                                    onEquipChange = { equipped = it },
                                                    onShowConfirm = { id ->
                                                        confirmTargetId = id
                                                        showConfirmDialog = true
                                                    }
                                                )
                                            }
                                        },
                                        onReset = {
                                            selectedBg = 0
                                            equipped = 0
                                            coroutineScope.launch {
                                                withContext(Dispatchers. IO) { ds.resetBackgroundToDefault() }
                                                snackbarHostState.showSnackbar("🔄 Reset to default")
                                            }
                                        }
                                    )
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(340.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults. cardColors(
                                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    BGSelectorPanel(
                                        selectedBg = selectedBg,
                                        equippedId = equipped,
                                        onSelect = { id -> selectedBg = id },
                                        onQuickBuy = { id ->
                                            if (id == 11) {
                                                levelLockTargetId = 11
                                                showLevelLockDialog = true
                                            } else {
                                                confirmTargetId = id
                                                showConfirmDialog = true
                                            }
                                        },
                                        purchasedFlags = localPurchasedFlags,
                                        prices = prices,
                                        isLevel7BgUnlocked = isLevel7BgUnlocked,
                                        currentLevel = currentLevel
                                    )
                                }
                            }
                        }
                    }
                }

                CustomizeSection.BUBBLE -> {
                    val bubbleSectionScroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            . fillMaxWidth()
                            . verticalScroll(bubbleSectionScroll)
                    ) {
                        BubbleCustomizationScreen()
                    }
                }

                CustomizeSection.MAIN_MENU -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        MainMenuSection()
                    }
                }
            }
        }

        Spacer(modifier = Modifier. height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            SnackbarHost(hostState = snackbarHostState)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Purchase Dialog (for buyable items, excluding 11 which is level-locked)
    if (showConfirmDialog && isBuyableBackground(confirmTargetId)) {
        BGPurchaseDialog(
            backgroundId = confirmTargetId,
            price = prices[confirmTargetId] ?:  0,
            isLuxPriced = isLuxPricedBackground(confirmTargetId),
            onConfirm = {
                val id = confirmTargetId
                val price = prices[id] ?: 0
                val isLux = isLuxPricedBackground(id)


                if (id in 1..21) {
                    localPurchasedFlags[id - 1] = true
                    equipped = id
                    selectedBg = id
                }
                showConfirmDialog = false

                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val ok = if (isLux) ds.buyBackgroundWithLux(id, price) else ds.buyBackground(id, price)
                            if (ok) {
                                snackbarHostState. showSnackbar("🎉 Purchased!")
                            } else {
                                val actualPurchased = ds.isBackgroundPurchasedFlow(id).first()
                                val actualEquipped = ds.equippedBackgroundFlow().first()
                                localPurchasedFlags[id - 1] = actualPurchased
                                equipped = actualEquipped
                                snackbarHostState. showSnackbar("❌ Not enough resources")
                            }
                        } catch (e: Exception) {
                            val actualPurchased = ds.isBackgroundPurchasedFlow(id).first()
                            val actualEquipped = ds. equippedBackgroundFlow().first()
                            localPurchasedFlags[id - 1] = actualPurchased
                            equipped = actualEquipped
                            snackbarHostState.showSnackbar("Error: ${e.localizedMessage ?: "unknown"}")
                        }
                    }
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    // Level Lock Dialog (for level-gated items)
    if (showLevelLockDialog) {
        BGLevelLockDialog(
            requiredLevel = 7,
            currentLevel = currentLevel,
            itemName = getBackgroundName(levelLockTargetId),
            onDismiss = { showLevelLockDialog = false }
        )
    }
}

// ==================== HELPER FUNCTION ====================

private fun handleBackgroundAction(
    selectedBg:  Int,
    equipped: Int,
    localPurchasedFlags: SnapshotStateList<Boolean>,
    ds:  DataStoreManager,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState:  SnackbarHostState,
    onEquipChange: (Int) -> Unit,
    onShowConfirm: (Int) -> Unit
) {
    coroutineScope.launch {
        if (selectedBg == 0) {
            snackbarHostState. showSnackbar("Select a background first")
            return@launch
        }
        val purchased = localPurchasedFlags.getOrNull(selectedBg - 1) ?: false
        if (equipped == selectedBg) {
            onEquipChange(0)
            withContext(Dispatchers.IO) { ds.equipBackground(0) }
            snackbarHostState. showSnackbar("Unequipped")
        } else if (purchased) {
            onEquipChange(selectedBg)
            val ok = withContext(Dispatchers.IO) { ds.equipBackground(selectedBg) }
            if (ok) {
                snackbarHostState.showSnackbar("✅ Equipped!")
            } else {
                val actual = ds.equippedBackgroundFlow().first()
                onEquipChange(actual)
                snackbarHostState.showSnackbar("Cannot equip")
            }
        } else {
            onShowConfirm(selectedBg)
        }
    }
}

// ==================== STYLED TEXT ====================

@Composable
private fun BGStyledText(
    text: String,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.White,
    modifier:  Modifier = Modifier,
    textAlign:  TextAlign?  = null
) {
    Text(
        text = text,
        fontSize = fontSize. sp,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        textAlign = textAlign,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black. copy(alpha = 0.6f),
                offset = Offset(1f, 1f),
                blurRadius = 2f
            )
        )
    )
}

// ==================== SECTION CHIP ====================

@Composable
fun BGCustomSectionChip(
    label: String,
    icon: String,
    selected:  Boolean,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "chipScale"
    )

    Card(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFFF6D00) else Color(0xFF1A1A2E).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 6.dp else 2.dp
        )
    ) {
        Box(
            contentAlignment = Alignment. Center,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selected) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFF6D00))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else Modifier
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    color = if (selected) Color. White else Color.White.copy(alpha = 0.8f),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

// ==================== BACKGROUND HELPERS ====================

private fun getBackgroundDrawable(id: Int): Int = when (id) {
    1 -> R.drawable.background1
    2 -> R.drawable.background2
    3 -> R.drawable. background3
    4 -> R.drawable.background4
    5 -> R. drawable.background5
    6 -> R.drawable.background6
    7 -> R.drawable.background7
    8 -> R.drawable. background8
    9 -> R.drawable.background9
    10 -> R. drawable.background10
    11 -> R.drawable.background11 // Level 7 exclusive
    else -> 0
}

private fun getBackgroundName(id: Int): String = when (id) {
    1 -> "Decorations"
    2 -> "Waves"
    3 -> "Beach Dreams"
    4 -> "Old Street"
    5 -> "Neon Girl"
    6 -> "Cyberpunk Future"
    7 -> "Magic Sunset"
    8 -> "Old Dreams"
    9 -> "Peak"
    10 -> "Mountains"
    11 -> "🌟 Level 7 Exclusive"
    12 -> "✨ Starfield"
    13 -> "✨ Ocean Waves"
    14 -> "✨ Forest"
    15 -> "✨ Aurora Borealis"
    16 -> "✨ Volcanic"
    17 -> "✨ Cyberpunk City"
    18 -> "✨ Underwater"
    19 -> "✨ Desert Dunes"
    20 -> "✨ Candy Land"
    21 -> "✨ Retro Grid"
    else -> "Default"
}

private fun getBackgroundRarity(id: Int): BGRarity = when (id) {
    1, 2 -> BGRarity. COMMON
    3, 4 -> BGRarity. RARE
    5, 6 -> BGRarity.EPIC
    7, 8 -> BGRarity.LEGENDARY
    9, 10 -> BGRarity.MYTHIC
    11 -> BGRarity. EXCLUSIVE
    12, 13 -> BGRarity.RARE
    14, 15 -> BGRarity.EPIC
    16, 17 -> BGRarity.LEGENDARY
    18, 19, 20 -> BGRarity.MYTHIC
    21 -> BGRarity.EXCLUSIVE
    else -> BGRarity.COMMON
}

private fun isGeneratedBackground(id: Int): Boolean = id in 12..21

private fun isLuxPricedBackground(id: Int): Boolean = when(id) {
    7, 8, 9, 10 -> true  // Original lux items
    14, 15, 17, 20, 21 -> true  // New generated lux items
    else -> false
}

private fun isBuyableBackground(id: Int): Boolean = id in 1..21 && id != 11

@Composable
private fun GeneratedBackgroundPreview(id: Int, modifier: Modifier = Modifier) {
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

private enum class BGRarity(val color: Color, val label: String) {
    COMMON(Color(0xFF9E9E9E), "Common"),
    RARE(Color(0xFF2196F3), "Rare"),
    EPIC(Color(0xFF9C27B0), "Epic"),
    LEGENDARY(Color(0xFFFFD700), "Legendary"),
    MYTHIC(Color(0xFFFF1744), "Mythic"),
    EXCLUSIVE(Color(0xFF00E676), "Exclusive")
}
// ==================== PREVIEW PANEL ====================

@Composable
private fun BGPreviewPanel(
    selectedBg: Int,
    isPurchased: Boolean,
    isEquipped: Boolean,
    price: Int,
    isLevelLocked: Boolean = false,
    requiredLevel: Int = 0,
    currentLevel: Int = 1,
    onBuyOrEquip: () -> Unit,
    onReset: () -> Unit
) {
    val rarity = getBackgroundRarity(selectedBg)
    val bgName = getBackgroundName(selectedBg)

    val infiniteTransition = rememberInfiniteTransition(label = "bgFloat")
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
            .fillMaxSize()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                BGStyledText(
                    text = "🖼️ Preview",
                    fontSize = 12,
                    color = Color.White. copy(alpha = 0.7f)
                )
                BGStyledText(
                    text = bgName,
                    fontSize = 18,
                    fontWeight = FontWeight. Bold
                )
            }

            if (selectedBg > 0) {
                BGRarityBadge(rarity = rarity)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preview Image
        Box(
            modifier = Modifier
                . fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush. radialGradient(
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
            BGParticlesBackground(rarityColor = rarity.color)

            val drawableId = getBackgroundDrawable(selectedBg)
            val isGenerated = isGeneratedBackground(selectedBg)
            if (selectedBg == 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌌", fontSize = 48. sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    BGStyledText(
                        text = "Default Background",
                        fontSize = 14,
                        color = Color.White. copy(alpha = 0.6f)
                    )
                }
            } else if (isGenerated) {
                // Show generated/animated background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (isLevelLocked) 0.5f else 1f)
                ) {
                    GeneratedBackgroundPreview(id = selectedBg, modifier = Modifier.fillMaxSize())
                }
            } else if (drawableId != 0) {
                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "bg_preview",
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
                        Spacer(modifier = Modifier.height(8.dp))
                        BGStyledText(
                            text = "Reach Level $requiredLevel",
                            fontSize = 16,
                            fontWeight = FontWeight. Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BGStyledText(
                            text = "Current:  Level $currentLevel",
                            fontSize = 12,
                            color = Color. White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Equipped badge
            if (isEquipped && ! isLevelLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
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

            // Animated badge for generated backgrounds
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

        Spacer(modifier = Modifier.height(8.dp))

        // Status Info
        if (selectedBg > 0) {
            if (isLevelLocked) {
                BGLevelLockInfo(requiredLevel = requiredLevel, currentLevel = currentLevel)
            } else {
                BGStatusInfo(
                    isPurchased = isPurchased,
                    isEquipped = isEquipped,
                    price = price,
                    isLuxPriced = selectedBg >= 7 && selectedBg <= 10,
                    isLevelUnlock = selectedBg == 11
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement. spacedBy(8.dp)
        ) {
            Button(
                onClick = onBuyOrEquip,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        selectedBg == 0 -> Color(0xFF666666)
                        isLevelLocked -> Color(0xFF9C27B0)
                        ! isPurchased -> Color(0xFFFF6D00)
                        isEquipped -> Color(0xFFFF5252)
                        else -> Color(0xFF4CAF50)
                    }
                ),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 4.dp)
            ) {
                when {
                    selectedBg == 0 -> {
                        BGStyledText(text = "Select", fontSize = 14, fontWeight = FontWeight. Bold)
                    }
                    isLevelLocked -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            BGStyledText(text = "Level $requiredLevel Required", fontSize = 12, fontWeight = FontWeight.Bold)
                        }
                    }
                    !isPurchased && selectedBg != 11 -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BGStyledText(text = "BUY $price", fontSize = 14, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(
                                    id = if (selectedBg >= 7) R.drawable.gemgame else R.drawable. coin
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    isEquipped -> {
                        BGStyledText(text = "UNEQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                    else -> {
                        BGStyledText(text = "✓ EQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
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
                BGStyledText(text = "↺", fontSize = 20, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== LEVEL LOCK INFO ====================

@Composable
private fun BGLevelLockInfo(requiredLevel: Int, currentLevel: Int) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⭐", fontSize = 16. sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Level $requiredLevel Exclusive",
                    fontSize = 14.sp,
                    fontWeight = FontWeight. Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White. copy(alpha = 0.1f))
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
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

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
private fun BGRarityBadge(rarity: BGRarity) {
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
            .background(rarity.color.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = rarity.color.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = rarity. label. uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight. Bold,
            color = rarity.color
        )
    }
}

// ==================== STATUS INFO ====================

@Composable
private fun BGStatusInfo(
    isPurchased: Boolean,
    isEquipped: Boolean,
    price:  Int,
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
                    fontWeight = FontWeight.Bold,
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
                    color = Color.White.copy(alpha = 0.6f)
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
private fun BGParticlesBackground(rarityColor: Color) {
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
            center = Offset(size.width * 0.75f, size.height * particle2Y)
        )

        drawCircle(
            color = particleColor,
            radius = 10f,
            center = Offset(size.width * 0.45f, size.height * ((particle1Y + particle2Y) / 2))
        )
    }
}

// ==================== SELECTOR PANEL ====================

@Composable
private fun BGSelectorPanel(
    selectedBg: Int,
    equippedId: Int,
    onSelect: (Int) -> Unit,
    onQuickBuy: (Int) -> Unit,
    purchasedFlags: SnapshotStateList<Boolean>,
    prices: Map<Int, Int>,
    isLevel7BgUnlocked: Boolean = false,
    currentLevel: Int = 1
) {
    val bgList = (1..21).toList() // Now includes IDs 1-21
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
            BGStyledText(
                text = "🎯 Collection",
                fontSize = 16,
                fontWeight = FontWeight. Bold
            )

            Text(
                text = "${purchasedFlags.count { it }}/${bgList.size}",
                fontSize = 12. sp,
                color = Color.White. copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier. fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bgList) { id ->
                val isLevelLockItem = id == 11
                val isPurchased = if (isLevelLockItem) isLevel7BgUnlocked else (purchasedFlags.getOrNull(id - 1) ?: false)

                BGSelectorItem(
                    id = id,
                    name = getBackgroundName(id),
                    drawableId = getBackgroundDrawable(id),
                    rarity = getBackgroundRarity(id),
                    isPurchased = isPurchased,
                    isEquipped = equippedId == id,
                    isSelected = selectedBg == id,
                    price = prices[id] ?: 0,
                    isLevelLocked = isLevelLockItem && !isLevel7BgUnlocked,
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
private fun BGSelectorItem(
    id: Int,
    name: String,
    drawableId: Int,
    rarity: BGRarity,
    isPurchased: Boolean,
    isEquipped: Boolean,
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
    val isLuxPriced = id in 7.. 10

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
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
                val isGenerated = isGeneratedBackground(id)

                if (isGenerated) {
                    // Show generated background preview
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isLevelLocked) 0.4f else 1f)
                    ) {
                        GeneratedBackgroundPreview(id = id, modifier = Modifier.fillMaxSize())
                    }
                } else if (drawableId != 0) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "bg_$id",
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
                            .background(Color. Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Text(
                                text = "Lvl $requiredLevel",
                                fontSize = 10.sp,
                                fontWeight = FontWeight. Bold,
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
                if (isEquipped && ! isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            . padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Animated badge for generated backgrounds
                if (isGenerated && !isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF6D00))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
                // Rarity indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
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
                            fontWeight = FontWeight. Bold,
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
private fun BGLevelLockDialog(
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

                Spacer(modifier = Modifier.height(4.dp))

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
                horizontalAlignment = Alignment.CenterHorizontally
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
                            . fillMaxWidth()
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
                                fontWeight = FontWeight.Bold,
                                color = Color. White.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier. height(12.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🎮 Keep playing games to earn XP and level up!",
                    fontSize = 12.sp,
                    color = Color(0xFF00E676),
                    textAlign = TextAlign. Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This exclusive item can only be unlocked by reaching Level $requiredLevel.  It cannot be purchased with coins or gems.",
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
private fun BGPurchaseDialog(
    backgroundId: Int,
    price: Int,
    isLuxPriced: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarity = getBackgroundRarity(backgroundId)
    val bgName = getBackgroundName(backgroundId)
    val drawableId = getBackgroundDrawable(backgroundId)

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
                    text = bgName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier. height(4.dp))

                BGRarityBadge(rarity = rarity)
            }
        },
        text = {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Price:  ",
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

// ==================== LEGACY SUPPORT ====================

@Composable
fun CustomSectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BGCustomSectionChip(
        label = label,
        icon = "",
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}