package com.appsdevs.popit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core. Preferences
import androidx. datastore.preferences. core.edit
import androidx.datastore. preferences.core.intPreferencesKey
import androidx.datastore.preferences.core. longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx. datastore.preferences. preferencesDataStore
import kotlinx. coroutines. CoroutineScope
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow. first
import kotlinx. coroutines.flow.map
import kotlinx. coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

// ==================== TOURNAMENT ENTRY CON PERFIL COMPLETO ====================

data class TournamentEntry(
    val name: String,
    val score: Int,
    val avatarRes: Int,
    val updatedAt: Long,
    val oduserId: String = "",
    val generatedAvatarId: Int = -1,
    val bannerColorId: Int = 0,
    val highScore:  Int = 0,
    val totalPops: Int = 0,
    val bestClickPercent:  Int = 0,
    val challengesCompleted: Int = 0,
    val level: Int = 1,
    val bestStreak:  Int = 0,
    val maxConsecutiveDays:  Int = 0
)

data class TournamentRewardTier(
    val rankRange: IntRange,
    val coins: Int,
    val lux: Int,
    val label: String,
    val emoji: String
)

class DataStoreManager(private val context: Context) {

    companion object {
        // ====== TOURNAMENT CONFIG ======
        const val TOURNAMENT_TEST_MODE = true

        const val DEFAULT_TOURNAMENT_DURATION_DAYS = 7
        const val TEST_TOURNAMENT_ACTIVE_MINUTES = 2  // 2 minutos activo
        const val TEST_TOURNAMENT_TOTAL_MINUTES = 3   // 3 minutos total (1 min para claim)

        @JvmStatic
        var DEV_OVERRIDE_TOURNAMENT_DAYS:  Int?  = null

        // User unique ID
        val USER_UNIQUE_ID = stringPreferencesKey("user_unique_id")

        // Tournament keys
        val TOURNAMENT_EPOCH_MILLIS = longPreferencesKey("tournament_epoch_millis")
        val TOURNAMENT_SCORE = intPreferencesKey("tournament_score")
        val TOURNAMENT_LAST_CLAIM_EPOCH = longPreferencesKey("tournament_last_claim_epoch")
        val TOURNAMENT_LAST_CLAIM_RANK = intPreferencesKey("tournament_last_claim_rank")
        val TOURNAMENT_LEADERBOARD_JSON = stringPreferencesKey("tournament_leaderboard_json")
        val TOURNAMENT_REWARDS_CLAIMED_EPOCHS = stringPreferencesKey("tournament_rewards_claimed_epochs")
        val TOURNAMENT_PENDING_REWARD = stringPreferencesKey("tournament_pending_reward")

        // ====== TOURNAMENT STATS FOR BADGES ======
        val TOURNAMENT_BEST_RANK = intPreferencesKey("tournament_best_rank")
        val TOURNAMENT_WINS = intPreferencesKey("tournament_wins")
        val TOURNAMENT_PODIUMS = intPreferencesKey("tournament_podiums")
        val TOURNAMENTS_PARTICIPATED = intPreferencesKey("tournaments_participated")

        val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        val HIGH_SCORE_BUBBLE_KING = intPreferencesKey("high_score_bubble_king")
        val HIGH_SCORE_PERFECT_STREAK = intPreferencesKey("high_score_perfect_streak")
        val HIGH_SCORE_TIME_MASTER = intPreferencesKey("high_score_time_master")
        val HIGH_SCORE_SPEED_DEMON = intPreferencesKey("high_score_speed_demon")
        val HIGH_SCORE_ACCURACY_KING = intPreferencesKey("high_score_accuracy_king")
        val HIGH_SCORE_ENDURANCE_MASTER = intPreferencesKey("high_score_endurance_master")

        val COINS_KEY = intPreferencesKey("coins")
        val LUX_KEY = intPreferencesKey("lux")

        val PURCHASE_BG_1 = intPreferencesKey("purchase_bg_1")
        val PURCHASE_BG_2 = intPreferencesKey("purchase_bg_2")
        val PURCHASE_BG_3 = intPreferencesKey("purchase_bg_3")
        val PURCHASE_BG_4 = intPreferencesKey("purchase_bg_4")
        val PURCHASE_BG_5 = intPreferencesKey("purchase_bg_5")
        val PURCHASE_BG_6 = intPreferencesKey("purchase_bg_6")
        val PURCHASE_BG_7 = intPreferencesKey("purchase_bg_7")
        val PURCHASE_BG_8 = intPreferencesKey("purchase_bg_8")
        val PURCHASE_BG_9 = intPreferencesKey("purchase_bg_9")
        val PURCHASE_BG_10 = intPreferencesKey("purchase_bg_10")
        val PURCHASE_BG_11 = intPreferencesKey("purchase_bg_11")
        val PURCHASE_BG_12 = intPreferencesKey("purchase_bg_12")
        val PURCHASE_BG_13 = intPreferencesKey("purchase_bg_13")
        val PURCHASE_BG_14 = intPreferencesKey("purchase_bg_14")
        val PURCHASE_BG_15 = intPreferencesKey("purchase_bg_15")
        val PURCHASE_BG_16 = intPreferencesKey("purchase_bg_16")
        val PURCHASE_BG_17 = intPreferencesKey("purchase_bg_17")
        val PURCHASE_BG_18 = intPreferencesKey("purchase_bg_18")
        val EQUIPPED_BG = intPreferencesKey("equipped_bg")

        val PURCHASE_BUBBLE_1 = intPreferencesKey("purchase_bubble_1")
        val PURCHASE_BUBBLE_2 = intPreferencesKey("purchase_bubble_2")
        val PURCHASE_BUBBLE_3 = intPreferencesKey("purchase_bubble_3")
        val PURCHASE_BUBBLE_4 = intPreferencesKey("purchase_bubble_4")
        val PURCHASE_BUBBLE_5 = intPreferencesKey("purchase_bubble_5")
        val PURCHASE_BUBBLE_6 = intPreferencesKey("purchase_bubble_6")
        val PURCHASE_BUBBLE_7 = intPreferencesKey("purchase_bubble_7")
        val PURCHASE_BUBBLE_8 = intPreferencesKey("purchase_bubble_8")
        val PURCHASE_BUBBLE_9 = intPreferencesKey("purchase_bubble_9")
        val PURCHASE_BUBBLE_10 = intPreferencesKey("purchase_bubble_10")
        val PURCHASE_BUBBLE_11 = intPreferencesKey("purchase_bubble_11")
        val PURCHASE_BUBBLE_12 = intPreferencesKey("purchase_bubble_12")
        val PURCHASE_BUBBLE_13 = intPreferencesKey("purchase_bubble_13")
        val PURCHASE_BUBBLE_14 = intPreferencesKey("purchase_bubble_14")
        val PURCHASE_BUBBLE_15 = intPreferencesKey("purchase_bubble_15")
        val PURCHASE_BUBBLE_16 = intPreferencesKey("purchase_bubble_16")
        val PURCHASE_BUBBLE_17 = intPreferencesKey("purchase_bubble_17")
        val PURCHASE_BUBBLE_18 = intPreferencesKey("purchase_bubble_18")
        val PURCHASE_BUBBLE_19 = intPreferencesKey("purchase_bubble_19")
        val PURCHASE_BUBBLE_20 = intPreferencesKey("purchase_bubble_20")
        val PURCHASE_BUBBLE_21 = intPreferencesKey("purchase_bubble_21")
        val PURCHASE_BUBBLE_22 = intPreferencesKey("purchase_bubble_22")
        val PURCHASE_BUBBLE_23 = intPreferencesKey("purchase_bubble_23")
        val PURCHASE_BUBBLE_24 = intPreferencesKey("purchase_bubble_24")
        val PURCHASE_BUBBLE_25 = intPreferencesKey("purchase_bubble_25")
        val PURCHASE_BUBBLE_26 = intPreferencesKey("purchase_bubble_26")
        val PURCHASE_BUBBLE_27 = intPreferencesKey("purchase_bubble_27")
        val PURCHASE_BUBBLE_28 = intPreferencesKey("purchase_bubble_28")
        val PURCHASE_BUBBLE_29 = intPreferencesKey("purchase_bubble_29")
        val PURCHASE_BUBBLE_30 = intPreferencesKey("purchase_bubble_30")
        val EQUIPPED_BUBBLE = intPreferencesKey("equipped_bubble")

        val PURCHASE_MAINMENU_1 = intPreferencesKey("purchase_mainmenu_1")
        val PURCHASE_MAINMENU_2 = intPreferencesKey("purchase_mainmenu_2")
        val PURCHASE_MAINMENU_3 = intPreferencesKey("purchase_mainmenu_3")
        val PURCHASE_MAINMENU_4 = intPreferencesKey("purchase_mainmenu_4")
        val PURCHASE_MAINMENU_5 = intPreferencesKey("purchase_mainmenu_5")
        val PURCHASE_MAINMENU_6 = intPreferencesKey("purchase_mainmenu_6")
        val PURCHASE_MAINMENU_7 = intPreferencesKey("purchase_mainmenu_7")
        val PURCHASE_MAINMENU_8 = intPreferencesKey("purchase_mainmenu_8")
        val PURCHASE_MAINMENU_9 = intPreferencesKey("purchase_mainmenu_9")
        val PURCHASE_MAINMENU_10 = intPreferencesKey("purchase_mainmenu_10")
        val PURCHASE_MAINMENU_11 = intPreferencesKey("purchase_mainmenu_11")
        val PURCHASE_MAINMENU_12 = intPreferencesKey("purchase_mainmenu_12")
        val PURCHASE_MAINMENU_13 = intPreferencesKey("purchase_mainmenu_13")
        val PURCHASE_MAINMENU_14 = intPreferencesKey("purchase_mainmenu_14")
        val PURCHASE_MAINMENU_15 = intPreferencesKey("purchase_mainmenu_15")
        val PURCHASE_MAINMENU_16 = intPreferencesKey("purchase_mainmenu_16")
        val EQUIPPED_MAINMENU = intPreferencesKey("equipped_mainmenu")

        val PROFILE_DRAWABLE = intPreferencesKey("profile_drawable")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
        val BANNER_COLOR = intPreferencesKey("banner_color")
        val GENERATED_AVATAR_ID = intPreferencesKey("generated_avatar_id")
        val BEST_CLICK_PERCENT = intPreferencesKey("best_click_percent")
        val CHALLENGES_COMPLETED_COUNT = intPreferencesKey("challenges_completed_count")
        val TOTAL_POPS = intPreferencesKey("total_pops")

        val LEVEL_REWARDS_CLAIMED = stringPreferencesKey("level_rewards_claimed")
        val LEVEL_UNLOCK_BUBBLE = intPreferencesKey("level_unlock_bubble_10")
        val LEVEL_UNLOCK_BG = intPreferencesKey("level_unlock_bg_11")
        val LEVEL_UNLOCK_MAINMENU = intPreferencesKey("level_unlock_mainmenu_10")

        // ====== CONSECUTIVE DAYS TRACKING ======
        val LAST_PLAY_DATE = longPreferencesKey("last_play_date")
        val CONSECUTIVE_DAYS = intPreferencesKey("consecutive_days")
        val MAX_CONSECUTIVE_DAYS = intPreferencesKey("max_consecutive_days")

        // ====== FIRST INSTALL DATE (FOR OG BADGE) ======
        val FIRST_INSTALL_DATE = longPreferencesKey("first_install_date")
    }

    // Scope para operaciones en background
    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    // ==================== TOURNAMENT REWARD TIERS ====================

    fun getTournamentRewardTiers(): List<TournamentRewardTier> = listOf(
        TournamentRewardTier(1.. 1, 100_000, 500, "Champion", "🏆"),
        TournamentRewardTier(2..3, 75_000, 350, "Elite", "🥇"),
        TournamentRewardTier(4..10, 50_000, 250, "Master", "🥈"),
        TournamentRewardTier(11..25, 30_000, 150, "Expert", "🥉"),
        TournamentRewardTier(26..50, 20_000, 100, "Skilled", "⭐"),
        TournamentRewardTier(51..100, 10_000, 50, "Competitor", "🎮"),
        TournamentRewardTier(101..200, 5_000, 25, "Participant", "👍")
    )

    fun getRewardForRank(rank: Int): TournamentRewardTier? {
        return getTournamentRewardTiers().find { rank in it.rankRange }
    }

    // ==================== TOURNAMENT TIMING ====================

    fun getTournamentDurationMs(): Long {
        return if (TOURNAMENT_TEST_MODE) {
            TEST_TOURNAMENT_TOTAL_MINUTES * 60L * 1000L  // 3 minutos
        } else {
            val days = DEV_OVERRIDE_TOURNAMENT_DAYS ?: DEFAULT_TOURNAMENT_DURATION_DAYS
            days * 24L * 60L * 60L * 1000L
        }
    }

    fun getTournamentActiveMs(): Long {
        return if (TOURNAMENT_TEST_MODE) {
            TEST_TOURNAMENT_ACTIVE_MINUTES * 60L * 1000L  // 2 minutos
        } else {
            4L * 24L * 60L * 60L * 1000L
        }
    }

    // ==================== USER ID ====================

    fun userIdFlow(): Flow<String> =
        context.dataStore.data.map { prefs -> prefs[USER_UNIQUE_ID] ?: "" }

    suspend fun ensureUserId(): String {
        var oduserId = ""
        context.dataStore.edit { prefs ->
            val current = prefs[USER_UNIQUE_ID] ?: ""
            if (current. isBlank()) {
                val newId = "user_${System.currentTimeMillis()}_${(1000..9999).random()}"
                prefs[USER_UNIQUE_ID] = newId
                oduserId = newId
            } else {
                oduserId = current
            }
        }
        return oduserId
    }

    suspend fun getUserId(): String {
        val current = userIdFlow().first()
        return if (current.isBlank()) ensureUserId() else current
    }

    // ==================== HIGH SCORES ====================

    fun highScoreFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[HIGH_SCORE_KEY] ?: 0 }

    suspend fun saveHighScore(score:  Int) {
        context.dataStore. edit { prefs -> prefs[HIGH_SCORE_KEY] = score }
        // Actualizar en Firebase en background
        triggerFirebaseProfileUpdate()
    }

    fun highScoreBubbleKingFlow(): Flow<Int> =
        context.dataStore. data.map { prefs -> prefs[HIGH_SCORE_BUBBLE_KING] ?: 0 }

    suspend fun saveHighScoreBubbleKing(score: Int) {
        context. dataStore.edit { prefs -> prefs[HIGH_SCORE_BUBBLE_KING] = score }
    }

    fun highScorePerfectStreakFlow(): Flow<Int> =
        context.dataStore. data.map { prefs -> prefs[HIGH_SCORE_PERFECT_STREAK] ?: 0 }

    suspend fun saveHighScorePerfectStreak(score: Int) {
        context.dataStore.edit { prefs -> prefs[HIGH_SCORE_PERFECT_STREAK] = score }
        triggerFirebaseProfileUpdate()
    }

    fun highScoreTimeMasterFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[HIGH_SCORE_TIME_MASTER] ?: 0 }

    suspend fun saveHighScoreTimeMaster(score: Int) {
        context.dataStore.edit { prefs -> prefs[HIGH_SCORE_TIME_MASTER] = score }
    }

    fun highScoreSpeedDemonFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[HIGH_SCORE_SPEED_DEMON] ?: 0 }

    suspend fun saveHighScoreSpeedDemon(score: Int) {
        context.dataStore.edit { prefs -> prefs[HIGH_SCORE_SPEED_DEMON] = score }
    }

    fun highScoreAccuracyKingFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[HIGH_SCORE_ACCURACY_KING] ?: 0 }

    suspend fun saveHighScoreAccuracyKing(score: Int) {
        context.dataStore.edit { prefs -> prefs[HIGH_SCORE_ACCURACY_KING] = score }
    }

    fun highScoreEnduranceMasterFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[HIGH_SCORE_ENDURANCE_MASTER] ?: 0 }

    suspend fun saveHighScoreEnduranceMaster(score: Int) {
        context.dataStore.edit { prefs -> prefs[HIGH_SCORE_ENDURANCE_MASTER] = score }
    }

    // ==================== COINS ====================

    fun coinsFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[COINS_KEY] ?: 0 }

    suspend fun saveCoins(coins:  Int) {
        context.dataStore. edit { prefs -> prefs[COINS_KEY] = coins }
    }

    suspend fun addCoins(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[COINS_KEY] ?:  0
            prefs[COINS_KEY] = current + amount
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.dataStore. edit { prefs ->
            val current = prefs[COINS_KEY] ?:  0
            if (current >= amount) {
                prefs[COINS_KEY] = current - amount
                success = true
            }
        }
        return success
    }

    // ==================== LUX ====================

    fun luxFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[LUX_KEY] ?: 0 }

    suspend fun saveLux(value: Int) {
        context.dataStore.edit { prefs -> prefs[LUX_KEY] = value }
    }

    suspend fun addLux(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[LUX_KEY] ?: 0
            prefs[LUX_KEY] = current + amount
        }
    }

    suspend fun spendLux(amount: Int): Boolean {
        var success = false
        context. dataStore.edit { prefs ->
            val current = prefs[LUX_KEY] ?: 0
            if (current >= amount) {
                prefs[LUX_KEY] = current - amount
                success = true
            }
        }
        return success
    }

    // ==================== BACKGROUNDS ====================

    private fun purchaseBgKeyForId(id: Int) = when (id) {
        1 -> PURCHASE_BG_1; 2 -> PURCHASE_BG_2; 3 -> PURCHASE_BG_3
        4 -> PURCHASE_BG_4; 5 -> PURCHASE_BG_5; 6 -> PURCHASE_BG_6
        7 -> PURCHASE_BG_7; 8 -> PURCHASE_BG_8; 9 -> PURCHASE_BG_9
        10 -> PURCHASE_BG_10; 11 -> PURCHASE_BG_11; 12 -> PURCHASE_BG_12
        13 -> PURCHASE_BG_13; 14 -> PURCHASE_BG_14; 15 -> PURCHASE_BG_15
        16 -> PURCHASE_BG_16; 17 -> PURCHASE_BG_17; 18 -> PURCHASE_BG_18
        else -> PURCHASE_BG_1
    }

    fun isBackgroundPurchasedFlow(id: Int): Flow<Boolean> {
        val key = purchaseBgKeyForId(id)
        return context.dataStore.data.map { prefs -> (prefs[key] ?: 0) == 1 }
    }

    suspend fun buyBackground(id: Int, price: Int): Boolean {
        var result = false
        val key = purchaseBgKeyForId(id)
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[COINS_KEY] ?: 0
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                prefs[EQUIPPED_BG] = id
                result = true
            } else if (currentCoins >= price) {
                prefs[COINS_KEY] = currentCoins - price
                prefs[key] = 1
                prefs[EQUIPPED_BG] = id
                result = true
            }
        }
        return result
    }

    suspend fun buyBackgroundWithLux(id: Int, priceLux: Int): Boolean {
        var result = false
        val key = purchaseBgKeyForId(id)
        context.dataStore.edit { prefs ->
            val currentLux = prefs[LUX_KEY] ?: 0
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                prefs[EQUIPPED_BG] = id
                result = true
            } else if (currentLux >= priceLux) {
                prefs[LUX_KEY] = currentLux - priceLux
                prefs[key] = 1
                prefs[EQUIPPED_BG] = id
                result = true
            }
        }
        return result
    }

    suspend fun equipBackground(id: Int): Boolean {
        var result = false
        val key = purchaseBgKeyForId(id)
        context.dataStore. edit { prefs ->
            if (id == 0) {
                prefs[EQUIPPED_BG] = 0
                result = true
            } else {
                val purchased = (prefs[key] ?: 0) == 1
                if (purchased) {
                    prefs[EQUIPPED_BG] = id
                    result = true
                }
            }
        }
        return result
    }

    fun equippedBackgroundFlow(): Flow<Int> =
        context. dataStore.data. map { prefs -> prefs[EQUIPPED_BG] ?: 0 }

    suspend fun resetBackgroundToDefault() {
        context. dataStore.edit { prefs -> prefs[EQUIPPED_BG] = 0 }
    }

    // ==================== BUBBLES ====================

    private fun purchaseBubbleKeyForId(id: Int) = when (id) {
        1 -> PURCHASE_BUBBLE_1; 2 -> PURCHASE_BUBBLE_2; 3 -> PURCHASE_BUBBLE_3
        4 -> PURCHASE_BUBBLE_4; 5 -> PURCHASE_BUBBLE_5; 6 -> PURCHASE_BUBBLE_6
        7 -> PURCHASE_BUBBLE_7; 8 -> PURCHASE_BUBBLE_8; 9 -> PURCHASE_BUBBLE_9
        10 -> PURCHASE_BUBBLE_10; 11 -> PURCHASE_BUBBLE_11; 12 -> PURCHASE_BUBBLE_12
        13 -> PURCHASE_BUBBLE_13; 14 -> PURCHASE_BUBBLE_14; 15 -> PURCHASE_BUBBLE_15
        16 -> PURCHASE_BUBBLE_16; 17 -> PURCHASE_BUBBLE_17; 18 -> PURCHASE_BUBBLE_18
        19 -> PURCHASE_BUBBLE_19; 20 -> PURCHASE_BUBBLE_20; 21 -> PURCHASE_BUBBLE_21
        22 -> PURCHASE_BUBBLE_22; 23 -> PURCHASE_BUBBLE_23; 24 -> PURCHASE_BUBBLE_24
        25 -> PURCHASE_BUBBLE_25; 26 -> PURCHASE_BUBBLE_26; 27 -> PURCHASE_BUBBLE_27
        28 -> PURCHASE_BUBBLE_28; 29 -> PURCHASE_BUBBLE_29; 30 -> PURCHASE_BUBBLE_30
        else -> PURCHASE_BUBBLE_1
    }

    fun isBubblePurchasedFlow(id: Int): Flow<Boolean> {
        val key = purchaseBubbleKeyForId(id)
        return context.dataStore.data.map { prefs -> (prefs[key] ?: 0) == 1 }
    }

    suspend fun buyBubble(id: Int, price:  Int): Boolean {
        var result = false
        val key = purchaseBubbleKeyForId(id)
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[COINS_KEY] ?:  0
            val already = (prefs[key] ?:  0) == 1
            if (already) {
                prefs[EQUIPPED_BUBBLE] = id
                result = true
            } else if (currentCoins >= price) {
                prefs[COINS_KEY] = currentCoins - price
                prefs[key] = 1
                prefs[EQUIPPED_BUBBLE] = id
                result = true
            }
        }
        return result
    }

    suspend fun buyBubbleWithLux(id:  Int, priceLux: Int): Boolean {
        var result = false
        val key = purchaseBubbleKeyForId(id)
        context.dataStore. edit { prefs ->
            val currentLux = prefs[LUX_KEY] ?: 0
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                prefs[EQUIPPED_BUBBLE] = id
                result = true
            } else if (currentLux >= priceLux) {
                prefs[LUX_KEY] = currentLux - priceLux
                prefs[key] = 1
                prefs[EQUIPPED_BUBBLE] = id
                result = true
            }
        }
        return result
    }

    suspend fun equipBubble(id: Int): Boolean {
        var result = false
        val key = purchaseBubbleKeyForId(id)
        context.dataStore. edit { prefs ->
            if (id == 0) {
                prefs[EQUIPPED_BUBBLE] = 0
                result = true
            } else {
                val purchased = (prefs[key] ?: 0) == 1
                if (purchased) {
                    prefs[EQUIPPED_BUBBLE] = id
                    result = true
                }
            }
        }
        return result
    }

    fun equippedBubbleFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[EQUIPPED_BUBBLE] ?: 0 }

    suspend fun resetBubbleToDefault() {
        context.dataStore. edit { prefs -> prefs[EQUIPPED_BUBBLE] = 0 }
    }

    // ==================== MAIN MENU ====================

    private fun purchaseMainMenuKeyForId(id: Int) = when (id) {
        1 -> PURCHASE_MAINMENU_1; 2 -> PURCHASE_MAINMENU_2; 3 -> PURCHASE_MAINMENU_3
        4 -> PURCHASE_MAINMENU_4; 5 -> PURCHASE_MAINMENU_5; 6 -> PURCHASE_MAINMENU_6
        7 -> PURCHASE_MAINMENU_7; 8 -> PURCHASE_MAINMENU_8; 9 -> PURCHASE_MAINMENU_9
        10 -> PURCHASE_MAINMENU_10; 11 -> PURCHASE_MAINMENU_11; 12 -> PURCHASE_MAINMENU_12
        13 -> PURCHASE_MAINMENU_13; 14 -> PURCHASE_MAINMENU_14; 15 -> PURCHASE_MAINMENU_15
        16 -> PURCHASE_MAINMENU_16
        else -> PURCHASE_MAINMENU_1
    }

    fun isMainMenuPurchasedFlow(id:  Int): Flow<Boolean> {
        val key = purchaseMainMenuKeyForId(id)
        return context.dataStore. data.map { prefs -> (prefs[key] ?:  0) == 1 }
    }

    suspend fun buyMainMenu(id: Int, price:  Int): Boolean {
        var result = false
        val key = purchaseMainMenuKeyForId(id)
        context.dataStore.edit { prefs ->
            val currentCoins = prefs[COINS_KEY] ?: 0
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                prefs[EQUIPPED_MAINMENU] = id
                result = true
            } else if (currentCoins >= price) {
                prefs[COINS_KEY] = currentCoins - price
                prefs[key] = 1
                prefs[EQUIPPED_MAINMENU] = id
                result = true
            }
        }
        return result
    }

    suspend fun buyMainMenuWithLux(id: Int, priceLux: Int): Boolean {
        var result = false
        val key = purchaseMainMenuKeyForId(id)
        context.dataStore.edit { prefs ->
            val currentLux = prefs[LUX_KEY] ?: 0
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                prefs[EQUIPPED_MAINMENU] = id
                result = true
            } else if (currentLux >= priceLux) {
                prefs[LUX_KEY] = currentLux - priceLux
                prefs[key] = 1
                prefs[EQUIPPED_MAINMENU] = id
                result = true
            }
        }
        return result
    }

    suspend fun equipMainMenu(id: Int): Boolean {
        var result = false
        val key = purchaseMainMenuKeyForId(id)
        context.dataStore.edit { prefs ->
            if (id == 0) {
                prefs[EQUIPPED_MAINMENU] = 0
                result = true
            } else {
                val purchased = (prefs[key] ?: 0) == 1
                if (purchased) {
                    prefs[EQUIPPED_MAINMENU] = id
                    result = true
                }
            }
        }
        return result
    }

    fun equippedMainMenuFlow(): Flow<Int> =
        context. dataStore.data. map { prefs -> prefs[EQUIPPED_MAINMENU] ?: 0 }

    suspend fun resetMainMenuToDefault() {
        context.dataStore. edit { prefs -> prefs[EQUIPPED_MAINMENU] = 0 }
    }

    // ==================== PROFILE ====================

    fun profileDrawableFlow(): Flow<Int> =
        context.dataStore. data.map { prefs -> prefs[PROFILE_DRAWABLE] ?: 0 }

    suspend fun saveProfileDrawable(resId: Int) {
        context. dataStore.edit { prefs -> prefs[PROFILE_DRAWABLE] = resId }
        // Actualizar en Firebase inmediatamente
        triggerFirebaseProfileUpdate()
    }

    suspend fun resetProfileToDefault() {
        context.dataStore. edit { prefs -> prefs[PROFILE_DRAWABLE] = 0 }
        triggerFirebaseProfileUpdate()
    }

    fun profileNameFlow(): Flow<String> =
        context.dataStore.data.map { prefs -> prefs[PROFILE_NAME] ?: "" }

    suspend fun saveProfileName(name:  String) {
        context.dataStore. edit { prefs -> prefs[PROFILE_NAME] = name }
        // Actualizar en Firebase inmediatamente
        triggerFirebaseProfileUpdate()
    }

    // ==================== BANNER COLOR ====================

    fun bannerColorFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[BANNER_COLOR] ?: 0 }

    suspend fun saveBannerColor(colorId: Int) {
        context.dataStore.edit { prefs -> prefs[BANNER_COLOR] = colorId }
        // Actualizar en Firebase inmediatamente
        triggerFirebaseProfileUpdate()
    }

    // ==================== GENERATED AVATAR ====================

    fun generatedAvatarIdFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[GENERATED_AVATAR_ID] ?: 0 }

    suspend fun saveGeneratedAvatarId(avatarId: Int) {
        context.dataStore.edit { prefs -> prefs[GENERATED_AVATAR_ID] = avatarId }
        // Actualizar en Firebase inmediatamente
        triggerFirebaseProfileUpdate()
    }

    suspend fun resetGeneratedAvatarToDefault() {
        context. dataStore.edit { prefs -> prefs[GENERATED_AVATAR_ID] = 0 }
        triggerFirebaseProfileUpdate()
    }

    // ==================== FIREBASE PROFILE UPDATE (TIEMPO REAL) ====================

    /**
     * Dispara una actualización del perfil en Firebase en background
     * Esta es la función clave para la sincronización en tiempo real
     */
    private fun triggerFirebaseProfileUpdate() {
        backgroundScope.launch {
            try {
                updateProfileInFirebase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Actualiza campos específicos del perfil en Firebase
     */
    suspend fun updateSpecificProfileFieldInFirebase(fieldName: String, value: Any): Boolean {
        return try {
            val oduserId = getUserId()
            if (oduserId. isBlank()) return false

            firebaseRepository.updateProfileField(oduserId, fieldName, value)
        } catch (e:  Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==================== STATS ====================

    fun bestClickPercentFlow(): Flow<Int> =
        context. dataStore.data. map { prefs -> prefs[BEST_CLICK_PERCENT] ?: 0 }

    suspend fun saveBestClickPercent(pct: Int) {
        context.dataStore.edit { prefs -> prefs[BEST_CLICK_PERCENT] = pct }
        triggerFirebaseProfileUpdate()
    }

    fun challengesCompletedCountFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[CHALLENGES_COMPLETED_COUNT] ?: 0 }

    suspend fun incrementChallengesCompletedCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
            prefs[CHALLENGES_COMPLETED_COUNT] = current + 1
        }
        triggerFirebaseProfileUpdate()
    }

    fun totalPopsFlow(): Flow<Int> =
        context. dataStore.data. map { prefs -> prefs[TOTAL_POPS] ?: 0 }

    suspend fun addTotalPops(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[TOTAL_POPS] ?: 0
            prefs[TOTAL_POPS] = current + amount
        }
        // No actualizar Firebase en cada pop por rendimiento
        // Se actualizará cuando se guarde el score o el perfil
    }

    suspend fun resetTotalPops() {
        context.dataStore.edit { prefs -> prefs[TOTAL_POPS] = 0 }
    }

    // ==================== TOURNAMENT STATS FOR BADGES ====================

    fun tournamentBestRankFlow(): Flow<Int> =
        context.dataStore. data.map { prefs -> prefs[TOURNAMENT_BEST_RANK] ?: 0 }

    suspend fun updateTournamentBestRank(rank:  Int) {
        context.dataStore. edit { prefs ->
            val current = prefs[TOURNAMENT_BEST_RANK] ?: Int.MAX_VALUE
            if (current == 0 || rank < current) {
                prefs[TOURNAMENT_BEST_RANK] = rank
            }
        }
    }

    fun tournamentWinsFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[TOURNAMENT_WINS] ?: 0 }

    suspend fun incrementTournamentWins() {
        context.dataStore.edit { prefs ->
            val current = prefs[TOURNAMENT_WINS] ?:  0
            prefs[TOURNAMENT_WINS] = current + 1
        }
    }

    fun tournamentPodiumsFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[TOURNAMENT_PODIUMS] ?: 0 }

    suspend fun incrementTournamentPodiums() {
        context.dataStore.edit { prefs ->
            val current = prefs[TOURNAMENT_PODIUMS] ?: 0
            prefs[TOURNAMENT_PODIUMS] = current + 1
        }
    }

    fun tournamentsParticipatedFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[TOURNAMENTS_PARTICIPATED] ?: 0 }

    suspend fun incrementTournamentsParticipated() {
        context. dataStore.edit { prefs ->
            val current = prefs[TOURNAMENTS_PARTICIPATED] ?: 0
            prefs[TOURNAMENTS_PARTICIPATED] = current + 1
        }
    }

    suspend fun recordTournamentResult(rank: Int) {
        updateTournamentBestRank(rank)
        if (rank == 1) {
            incrementTournamentWins()
        }
        if (rank in 1..3) {
            incrementTournamentPodiums()
        }
    }

    // ==================== CONSECUTIVE DAYS TRACKING ====================

    fun consecutiveDaysFlow(): Flow<Int> =
        context. dataStore.data. map { prefs -> prefs[CONSECUTIVE_DAYS] ?: 0 }

    fun maxConsecutiveDaysFlow(): Flow<Int> =
        context.dataStore.data.map { prefs -> prefs[MAX_CONSECUTIVE_DAYS] ?: 0 }

    suspend fun updateConsecutiveDays() {
        var shouldUpdateFirebase = false
        context.dataStore.edit { prefs ->
            val lastPlayDate = prefs[LAST_PLAY_DATE] ?: 0L
            val now = System.currentTimeMillis()
            val today = now / (24 * 60 * 60 * 1000)
            val lastDay = lastPlayDate / (24 * 60 * 60 * 1000)

            when {
                lastDay == today -> {
                    // Ya jugó hoy, no hacer nada
                }
                lastDay == today - 1 -> {
                    val currentStreak = (prefs[CONSECUTIVE_DAYS] ?: 0) + 1
                    prefs[CONSECUTIVE_DAYS] = currentStreak
                    val maxStreak = prefs[MAX_CONSECUTIVE_DAYS] ?: 0
                    if (currentStreak > maxStreak) {
                        prefs[MAX_CONSECUTIVE_DAYS] = currentStreak
                        shouldUpdateFirebase = true
                    }
                    prefs[LAST_PLAY_DATE] = now
                }
                else -> {
                    prefs[CONSECUTIVE_DAYS] = 1
                    prefs[LAST_PLAY_DATE] = now
                    val maxStreak = prefs[MAX_CONSECUTIVE_DAYS] ?: 0
                    if (maxStreak == 0) {
                        prefs[MAX_CONSECUTIVE_DAYS] = 1
                        shouldUpdateFirebase = true
                    }
                }
            }
        }

        if (shouldUpdateFirebase) {
            triggerFirebaseProfileUpdate()
        }
    }

    // ==================== FIRST INSTALL DATE (FOR OG BADGE) ====================

    fun firstInstallDateFlow(): Flow<Long> =
        context.dataStore.data.map { prefs -> prefs[FIRST_INSTALL_DATE] ?: 0L }

    suspend fun ensureFirstInstallDate() {
        context. dataStore.edit { prefs ->
            if (prefs[FIRST_INSTALL_DATE] == null || prefs[FIRST_INSTALL_DATE] == 0L) {
                prefs[FIRST_INSTALL_DATE] = System.currentTimeMillis()
            }
        }
    }

    suspend fun isOgPlayer(): Boolean {
        val installDate = firstInstallDateFlow().first()
        if (installDate == 0L) return false
        val ogCutoffDate = 1735689600000L
        return installDate < ogCutoffDate
    }

    // ==================== CHALLENGE REWARDS ====================

    private fun challengeRewardKey(challengeId:  Int, pct: Int) =
        intPreferencesKey("challenge_${challengeId}_reward_${pct}")

    fun isChallengeRewardClaimedFlow(challengeId: Int, pct: Int): Flow<Boolean> {
        val key = challengeRewardKey(challengeId, pct)
        return context. dataStore.data. map { prefs -> (prefs[key] ?: 0) == 1 }
    }

    suspend fun claimChallengeReward(challengeId: Int, pct: Int): Boolean {
        var success = false
        val key = challengeRewardKey(challengeId, pct)
        context.dataStore.edit { prefs ->
            val already = (prefs[key] ?: 0) == 1
            if (already) {
                success = false
                return@edit
            }
            prefs[key] = 1
            
            // New reward logic based on problem statement
            when (challengeId) {
                1 -> { // Bubble King
                    when (pct) {
                        30 -> {
                            // Unlock Fire Bubble (ID 11)
                            prefs[PURCHASE_BUBBLE_11] = 1
                            prefs[EQUIPPED_BUBBLE] = 11
                        }
                        60 -> {
                            // Unlock Starfield Background (ID 12)
                            prefs[PURCHASE_BG_12] = 1
                            prefs[EQUIPPED_BG] = 12
                        }
                        100 -> {
                            // Unlock Neon Pulse MainMenu (ID 11)
                            prefs[PURCHASE_MAINMENU_11] = 1
                            prefs[EQUIPPED_MAINMENU] = 11
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
                2 -> { // Perfect Streak
                    when (pct) {
                        30 -> {
                            // Unlock Ice Bubble (ID 12)
                            prefs[PURCHASE_BUBBLE_12] = 1
                            prefs[EQUIPPED_BUBBLE] = 12
                        }
                        60 -> {
                            // Unlock Ocean Waves Background (ID 13)
                            prefs[PURCHASE_BG_13] = 1
                            prefs[EQUIPPED_BG] = 13
                        }
                        100 -> {
                            // Unlock Geometric Waves MainMenu (ID 12)
                            prefs[PURCHASE_MAINMENU_12] = 1
                            prefs[EQUIPPED_MAINMENU] = 12
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
                3 -> { // Time Master
                    when (pct) {
                        30 -> {
                            // Unlock Electric Bubble (ID 13)
                            prefs[PURCHASE_BUBBLE_13] = 1
                            prefs[EQUIPPED_BUBBLE] = 13
                        }
                        60 -> {
                            // Unlock Aurora Borealis Background (ID 15)
                            prefs[PURCHASE_BG_15] = 1
                            prefs[EQUIPPED_BG] = 15
                        }
                        100 -> {
                            // Unlock Particle Field MainMenu (ID 13)
                            prefs[PURCHASE_MAINMENU_13] = 1
                            prefs[EQUIPPED_MAINMENU] = 13
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
                4 -> { // Speed Demon
                    when (pct) {
                        30 -> {
                            // Unlock Nature Bubble (ID 14)
                            prefs[PURCHASE_BUBBLE_14] = 1
                            prefs[EQUIPPED_BUBBLE] = 14
                        }
                        60 -> {
                            // Unlock Volcanic Background (ID 16)
                            prefs[PURCHASE_BG_16] = 1
                            prefs[EQUIPPED_BG] = 16
                        }
                        100 -> {
                            // Unlock Gradient Mesh MainMenu (ID 14)
                            prefs[PURCHASE_MAINMENU_14] = 1
                            prefs[EQUIPPED_MAINMENU] = 14
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
                5 -> { // Accuracy King
                    when (pct) {
                        30 -> {
                            // Unlock Galaxy Bubble (ID 15)
                            prefs[PURCHASE_BUBBLE_15] = 1
                            prefs[EQUIPPED_BUBBLE] = 15
                        }
                        60 -> {
                            // Unlock Cyberpunk City Background (ID 17)
                            prefs[PURCHASE_BG_17] = 1
                            prefs[EQUIPPED_BG] = 17
                        }
                        100 -> {
                            // Unlock Matrix Rain MainMenu (ID 15)
                            prefs[PURCHASE_MAINMENU_15] = 1
                            prefs[EQUIPPED_MAINMENU] = 15
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
                6 -> { // Endurance Master
                    when (pct) {
                        30 -> {
                            // Unlock Lava Bubble (ID 16)
                            prefs[PURCHASE_BUBBLE_16] = 1
                            prefs[EQUIPPED_BUBBLE] = 16
                        }
                        60 -> {
                            // Unlock Underwater Background (ID 18)
                            prefs[PURCHASE_BG_18] = 1
                            prefs[EQUIPPED_BG] = 18
                        }
                        100 -> {
                            // Unlock Cosmic Nebula MainMenu (ID 16)
                            prefs[PURCHASE_MAINMENU_16] = 1
                            prefs[EQUIPPED_MAINMENU] = 16
                            val currentCount = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
                            prefs[CHALLENGES_COMPLETED_COUNT] = currentCount + 1
                        }
                    }
                }
            }
            success = true
        }

        if (success && pct == 100) {
            triggerFirebaseProfileUpdate()
        }

        return success
    }

    // ==================== LEVEL SYSTEM ====================

    data class LevelReward(
        val level: Int,
        val coins: Int = 0,
        val lux: Int = 0,
        val unlockBubble: Int = 0,
        val unlockBackground: Int = 0,
        val unlockMainMenu: Int = 0,
        val description: String = ""
    )

    private val levelThresholds = listOf(
        0, 100, 500, 1000, 2500, 5000, 10000, 25000, 50000, 100000,
        150000, 200000, 300000, 400000, 500000, 650000, 800000, 1000000, 1250000, 1500000
    )

    fun getLevelRewards(): List<LevelReward> = listOf(
        LevelReward(1, coins = 50, lux = 0, description = "Welcome bonus! "),
        LevelReward(2, coins = 100, lux = 5, description = "Getting started!"),
        LevelReward(3, coins = 150, lux = 10, description = "Nice progress!"),
        LevelReward(4, coins = 200, lux = 15, description = "Keep popping!"),
        LevelReward(5, coins = 300, lux = 20, description = "Halfway to special!"),
        LevelReward(6, coins = 400, lux = 25, description = "Almost there!"),
        LevelReward(7, coins = 500, lux = 50, unlockBubble = 10, unlockBackground = 11, unlockMainMenu = 10,
            description = "🎉 SPECIAL UNLOCK!  Exclusive items! "),
        LevelReward(8, coins = 600, lux = 30, description = "Excellent! "),
        LevelReward(9, coins = 700, lux = 35, description = "Amazing skills!"),
        LevelReward(10, coins = 1000, lux = 50, description = "Double digits!"),
        LevelReward(11, coins = 800, lux = 40, description = "Keep going!"),
        LevelReward(12, coins = 900, lux = 45, description = "Unstoppable!"),
        LevelReward(13, coins = 1000, lux = 50, description = "Lucky 13!"),
        LevelReward(14, coins = 1100, lux = 55, description = "Pro player!"),
        LevelReward(15, coins = 1500, lux = 75, description = "Milestone reached!"),
        LevelReward(16, coins = 1200, lux = 60, description = "Expert mode!"),
        LevelReward(17, coins = 1300, lux = 65, description = "Almost master!"),
        LevelReward(18, coins = 1400, lux = 70, description = "Nearly there!"),
        LevelReward(19, coins = 1600, lux = 80, description = "One more level!"),
        LevelReward(20, coins = 2500, lux = 150, description = "🏆 MAX LEVEL!  LEGENDARY!")
    )

    fun calculateLevelFromPops(totalPops: Int): Int {
        for (i in levelThresholds.indices. reversed()) {
            if (totalPops >= levelThresholds[i]) {
                return (i + 1).coerceAtMost(20)
            }
        }
        return 1
    }

    fun claimedLevelRewardsFlow(): Flow<Set<Int>> =
        context.dataStore.data.map { prefs ->
            val claimedStr = prefs[LEVEL_REWARDS_CLAIMED] ?: ""
            if (claimedStr.isEmpty()) emptySet()
            else claimedStr.split(",").mapNotNull { it. toIntOrNull() }.toSet()
        }

    suspend fun claimLevelReward(level: Int): Boolean {
        val rewards = getLevelRewards().find { it.level == level } ?: return false

        var success = false
        context.dataStore.edit { prefs ->
            val claimedStr = prefs[LEVEL_REWARDS_CLAIMED] ?: ""
            val claimedSet = if (claimedStr.isEmpty()) mutableSetOf()
            else claimedStr.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()

            if (level in claimedSet) {
                success = false
                return@edit
            }

            val totalPops = prefs[TOTAL_POPS] ?: 0
            val currentLevel = calculateLevelFromPops(totalPops)
            if (currentLevel < level) {
                success = false
                return@edit
            }

            if (rewards.coins > 0) {
                val currentCoins = prefs[COINS_KEY] ?: 0
                prefs[COINS_KEY] = currentCoins + rewards.coins
            }

            if (rewards.lux > 0) {
                val currentLux = prefs[LUX_KEY] ?: 0
                prefs[LUX_KEY] = currentLux + rewards.lux
            }

            if (rewards.unlockBubble > 0) {
                prefs[PURCHASE_BUBBLE_10] = 1
                prefs[LEVEL_UNLOCK_BUBBLE] = 1
            }
            if (rewards. unlockBackground > 0) {
                prefs[PURCHASE_BG_11] = 1
                prefs[LEVEL_UNLOCK_BG] = 1
            }
            if (rewards.unlockMainMenu > 0) {
                prefs[PURCHASE_MAINMENU_10] = 1
                prefs[LEVEL_UNLOCK_MAINMENU] = 1
            }

            claimedSet.add(level)
            prefs[LEVEL_REWARDS_CLAIMED] = claimedSet.joinToString(",")
            success = true
        }

        if (success) {
            triggerFirebaseProfileUpdate()
        }

        return success
    }

    fun isLevel7BubbleUnlockedFlow(): Flow<Boolean> =
        context. dataStore.data. map { prefs -> (prefs[LEVEL_UNLOCK_BUBBLE] ?: 0) == 1 }

    fun isLevel7BackgroundUnlockedFlow(): Flow<Boolean> =
        context.dataStore.data.map { prefs -> (prefs[LEVEL_UNLOCK_BG] ?: 0) == 1 }

    fun isLevel7MainMenuUnlockedFlow(): Flow<Boolean> =
        context.dataStore.data.map { prefs -> (prefs[LEVEL_UNLOCK_MAINMENU] ?: 0) == 1 }

    // ==================== TOURNAMENT ====================

    fun tournamentEpochMillisFlow(): Flow<Long> =
        context.dataStore.data.map { prefs -> prefs[TOURNAMENT_EPOCH_MILLIS] ?: 0L }

    suspend fun ensureTournamentEpoch(nowMillis: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[TOURNAMENT_EPOCH_MILLIS] ?: 0L
            if (current == 0L) {
                prefs[TOURNAMENT_EPOCH_MILLIS] = nowMillis
            }
        }
    }

    suspend fun resetTournamentEpochToNow() {
        context.dataStore.edit { prefs ->
            prefs[TOURNAMENT_EPOCH_MILLIS] = System. currentTimeMillis()
        }
    }

    suspend fun startNewTournament() {
        context.dataStore.edit { prefs ->
            prefs[TOURNAMENT_EPOCH_MILLIS] = System. currentTimeMillis()
            prefs[TOURNAMENT_LEADERBOARD_JSON] = ""
            prefs[TOURNAMENT_SCORE] = 0
        }
    }

    fun tournamentScoreFlow(): Flow<Int> =
        context.dataStore. data.map { prefs -> prefs[TOURNAMENT_SCORE] ?: 0 }

    suspend fun saveTournamentScore(score: Int) {
        context.dataStore.edit { prefs -> prefs[TOURNAMENT_SCORE] = score }
    }

    suspend fun addToTournamentScore(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[TOURNAMENT_SCORE] ?: 0
            prefs[TOURNAMENT_SCORE] = current + amount
        }
    }

    // ==================== TOURNAMENT LEADERBOARD (LOCAL BACKUP) ====================

    fun getTournamentLeaderboardFlow(): Flow<List<TournamentEntry>> =
        context.dataStore.data.map { prefs ->
            val json = prefs[TOURNAMENT_LEADERBOARD_JSON] ?: ""
            parseLeaderboardJson(json)
        }

    private fun parseLeaderboardJson(json: String): List<TournamentEntry> {
        if (json.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<TournamentEntry>()
            for (i in 0 until arr. length()) {
                val obj = arr.getJSONObject(i)
                list.add(TournamentEntry(
                    oduserId = obj.optString("oduserId", ""),
                    name = obj.optString("name", "Player"),
                    score = obj.optInt("score", 0),
                    avatarRes = obj.optInt("avatarRes", 0),
                    updatedAt = obj. optLong("updatedAt", 0L),
                    generatedAvatarId = obj.optInt("generatedAvatarId", -1),
                    bannerColorId = obj.optInt("bannerColorId", 0),
                    highScore = obj.optInt("highScore", 0),
                    totalPops = obj.optInt("totalPops", 0),
                    bestClickPercent = obj.optInt("bestClickPercent", 0),
                    challengesCompleted = obj. optInt("challengesCompleted", 0),
                    level = obj.optInt("level", 1),
                    bestStreak = obj.optInt("bestStreak", 0),
                    maxConsecutiveDays = obj.optInt("maxConsecutiveDays", 0)
                ))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun toLeaderboardJson(list: List<TournamentEntry>): String {
        val arr = JSONArray()
        for (e in list) {
            val obj = JSONObject()
            obj.put("oduserId", e.oduserId)
            obj.put("name", e.name)
            obj.put("score", e.score)
            obj.put("avatarRes", e.avatarRes)
            obj.put("updatedAt", e.updatedAt)
            obj.put("generatedAvatarId", e.generatedAvatarId)
            obj.put("bannerColorId", e.bannerColorId)
            obj.put("highScore", e.highScore)
            obj.put("totalPops", e.totalPops)
            obj.put("bestClickPercent", e. bestClickPercent)
            obj.put("challengesCompleted", e.challengesCompleted)
            obj.put("level", e.level)
            obj.put("bestStreak", e.bestStreak)
            obj.put("maxConsecutiveDays", e.maxConsecutiveDays)
            arr.put(obj)
        }
        return arr.toString()
    }

    suspend fun submitTournamentScoreWithUserId(
        oduserId: String,
        name: String,
        avatarRes: Int,
        score: Int,
        nowMillis: Long,
        generatedAvatarId: Int = -1
    ) {
        context. dataStore.edit { prefs ->
            val currentBanner = prefs[BANNER_COLOR] ?: 0
            val currentHighScore = prefs[HIGH_SCORE_KEY] ?: 0
            val currentTotalPops = prefs[TOTAL_POPS] ?: 0
            val currentBestClick = prefs[BEST_CLICK_PERCENT] ?:  0
            val currentChallenges = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
            val currentLevel = calculateLevelFromPops(currentTotalPops)
            val currentBestStreak = prefs[HIGH_SCORE_PERFECT_STREAK] ?: 0
            val currentMaxConsecutiveDays = prefs[MAX_CONSECUTIVE_DAYS] ?: 0

            val json = prefs[TOURNAMENT_LEADERBOARD_JSON] ?: ""
            val list = parseLeaderboardJson(json).toMutableList()

            val idx = list.indexOfFirst { it. oduserId == oduserId }
            if (idx >= 0) {
                val existing = list[idx]
                val bestScore = max(existing.score, score)
                list[idx] = TournamentEntry(
                    name = name,
                    score = bestScore,
                    avatarRes = avatarRes,
                    updatedAt = nowMillis,
                    oduserId = oduserId,
                    generatedAvatarId = generatedAvatarId,
                    bannerColorId = currentBanner,
                    highScore = currentHighScore,
                    totalPops = currentTotalPops,
                    bestClickPercent = currentBestClick,
                    challengesCompleted = currentChallenges,
                    level = currentLevel,
                    bestStreak = currentBestStreak,
                    maxConsecutiveDays = currentMaxConsecutiveDays
                )
            } else {
                list.add(TournamentEntry(
                    name = name,
                    score = score,
                    avatarRes = avatarRes,
                    updatedAt = nowMillis,
                    oduserId = oduserId,
                    generatedAvatarId = generatedAvatarId,
                    bannerColorId = currentBanner,
                    highScore = currentHighScore,
                    totalPops = currentTotalPops,
                    bestClickPercent = currentBestClick,
                    challengesCompleted = currentChallenges,
                    level = currentLevel,
                    bestStreak = currentBestStreak,
                    maxConsecutiveDays = currentMaxConsecutiveDays
                ))
            }

            val sorted = list.sortedByDescending { it. score }. take(200)
            prefs[TOURNAMENT_LEADERBOARD_JSON] = toLeaderboardJson(sorted)
        }
    }

    // ==================== TOURNAMENT REWARDS ====================

    fun hasClaimedRewardForEpoch(epochMillis: Long): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            val claimedEpochs = prefs[TOURNAMENT_REWARDS_CLAIMED_EPOCHS] ?: ""
            claimedEpochs.split(",").contains(epochMillis. toString())
        }

    fun getPendingRewardFlow(): Flow<PendingTournamentReward? > =
        context.dataStore.data.map { prefs ->
            val data = prefs[TOURNAMENT_PENDING_REWARD] ?: ""
            if (data.isBlank()) null
            else {
                try {
                    val parts = data.split(",")
                    if (parts.size >= 4) {
                        PendingTournamentReward(
                            epochMillis = parts[0].toLong(),
                            rank = parts[1]. toInt(),
                            coins = parts[2]. toInt(),
                            lux = parts[3].toInt()
                        )
                    } else null
                } catch (_: Exception) { null }
            }
        }

    suspend fun setPendingTournamentReward(epochMillis: Long, rank: Int, coins: Int, lux: Int) {
        context.dataStore.edit { prefs ->
            prefs[TOURNAMENT_PENDING_REWARD] = "$epochMillis,$rank,$coins,$lux"
        }
    }

    suspend fun claimPendingTournamentReward(): Boolean {
        var success = false
        var claimedRank = 0

        context.dataStore.edit { prefs ->
            val data = prefs[TOURNAMENT_PENDING_REWARD] ?:  ""
            if (data.isBlank()) {
                success = false
                return@edit
            }

            try {
                val parts = data.split(",")
                if (parts.size >= 4) {
                    val epochMillis = parts[0]. toLong()
                    val rank = parts[1].toInt()
                    val coins = parts[2].toInt()
                    val lux = parts[3]. toInt()

                    val currentCoins = prefs[COINS_KEY] ?: 0
                    val currentLux = prefs[LUX_KEY] ?: 0
                    prefs[COINS_KEY] = currentCoins + coins
                    prefs[LUX_KEY] = currentLux + lux

                    val claimedEpochs = prefs[TOURNAMENT_REWARDS_CLAIMED_EPOCHS] ?: ""
                    val newClaimed = if (claimedEpochs.isBlank()) epochMillis. toString()
                    else "$claimedEpochs,$epochMillis"
                    prefs[TOURNAMENT_REWARDS_CLAIMED_EPOCHS] = newClaimed

                    prefs[TOURNAMENT_PENDING_REWARD] = ""

                    claimedRank = rank
                    success = true
                }
            } catch (_: Exception) {
                success = false
            }
        }

        if (success && claimedRank > 0) {
            recordTournamentResult(claimedRank)
        }

        return success
    }

    suspend fun checkAndPrepareTournamentRewards(currentEpoch: Long, nowMillis: Long): Boolean {
        val activeMs = getTournamentActiveMs()
        val elapsedMs = nowMillis - currentEpoch

        if (elapsedMs >= activeMs) {
            val pending = getPendingRewardFlow().first()
            if (pending != null && pending.epochMillis == currentEpoch) {
                return true
            }

            val hasClaimed = hasClaimedRewardForEpoch(currentEpoch).first()
            if (hasClaimed) {
                return false
            }

            val oduserId = getUserId()

            // Usar Firebase para obtener el leaderboard
            val leaderboard = try {
                firebaseRepository.getLeaderboardOnce()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

            val userEntry = leaderboard. find { it.oduserId == oduserId }

            if (userEntry != null && userEntry.score > 0) {
                val rank = leaderboard.indexOfFirst { it. oduserId == oduserId } + 1
                val reward = getRewardForRank(rank)

                if (reward != null) {
                    setPendingTournamentReward(currentEpoch, rank, reward.coins, reward.lux)
                    return true
                }
            }
        }
        return false
    }

    // Método para verificar y auto-reiniciar torneo
    suspend fun checkAndAutoRestartTournament(): Boolean {
        val now = System.currentTimeMillis()
        val epoch = tournamentEpochMillisFlow().first()

        if (epoch == 0L) {
            return startNewTournamentWithFirebase()
        }

        val totalMs = getTournamentDurationMs()
        val elapsedMs = now - epoch

        if (elapsedMs >= totalMs) {
            return startNewTournamentWithFirebase()
        }

        return false
    }

    suspend fun getTournamentStatus(): TournamentStatus {
        val now = System.currentTimeMillis()
        val epoch = tournamentEpochMillisFlow().first()

        if (epoch == 0L) {
            return TournamentStatus.NO_TOURNAMENT
        }

        val activeMs = getTournamentActiveMs()
        val totalMs = getTournamentDurationMs()
        val elapsedMs = now - epoch

        return when {
            elapsedMs < activeMs -> TournamentStatus.ACTIVE
            elapsedMs < totalMs -> TournamentStatus. ENDED_REWARDS_PENDING
            else -> TournamentStatus. CYCLE_COMPLETE
        }
    }

    enum class TournamentStatus {
        NO_TOURNAMENT,
        ACTIVE,
        ENDED_REWARDS_PENDING,
        CYCLE_COMPLETE
    }

    data class PendingTournamentReward(
        val epochMillis:  Long,
        val rank: Int,
        val coins: Int,
        val lux: Int
    )

    // ==================== FIREBASE INTEGRATION ====================

    private val firebaseRepository = TournamentFirebaseRepository()

    /**
     * Envía el score al servidor Firebase
     */
    suspend fun submitScoreToFirebase(score: Int): Boolean {
        return try {
            // Asegurar auth anónimo
            firebaseRepository.ensureAnonymousAuth()

            val oduserId = getUserId()
            val firebaseUid = firebaseRepository.getCurrentFirebaseUid() ?: return false

            val prefs = context.dataStore.data.first()
            val name = prefs[PROFILE_NAME]?. ifBlank { "Player" } ?: "Player"
            val avatarRes = prefs[PROFILE_DRAWABLE] ?: 0
            val generatedAvatarId = prefs[GENERATED_AVATAR_ID] ?: 0
            val bannerColorId = prefs[BANNER_COLOR] ?: 0
            val highScore = prefs[HIGH_SCORE_KEY] ?: 0
            val totalPops = prefs[TOTAL_POPS] ?:  0
            val bestClickPercent = prefs[BEST_CLICK_PERCENT] ?: 0
            val challengesCompleted = prefs[CHALLENGES_COMPLETED_COUNT] ?: 0
            val level = calculateLevelFromPops(totalPops)
            val bestStreak = prefs[HIGH_SCORE_PERFECT_STREAK] ?: 0
            val maxConsecutiveDays = prefs[MAX_CONSECUTIVE_DAYS] ?: 0

            firebaseRepository.submitScore(
                oduserId = oduserId,
                firebaseUid = firebaseUid,
                name = name,
                avatarRes = if (avatarRes != 0) avatarRes else R.drawable.profileuser0,
                score = score,
                generatedAvatarId = generatedAvatarId,
                bannerColorId = bannerColorId,
                highScore = highScore,
                totalPops = totalPops,
                bestClickPercent = bestClickPercent,
                challengesCompleted = challengesCompleted,
                level = level,
                bestStreak = bestStreak,
                maxConsecutiveDays = maxConsecutiveDays
            )
        } catch (e:  Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualiza el perfil del usuario en Firebase (TIEMPO REAL)
     * Esta función se llama automáticamente cuando cambia cualquier dato del perfil
     */
    suspend fun updateProfileInFirebase(): Boolean {
        return try {
            val oduserId = getUserId()
            if (oduserId. isBlank()) return false

            // Asegurar que tenemos autenticación
            try {
                firebaseRepository.ensureAnonymousAuth()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

            val prefs = context. dataStore.data. first()
            val name = prefs[PROFILE_NAME]?.ifBlank { "Player" } ?: "Player"
            val avatarRes = prefs[PROFILE_DRAWABLE] ?: 0
            val generatedAvatarId = prefs[GENERATED_AVATAR_ID] ?: 0
            val bannerColorId = prefs[BANNER_COLOR] ?: 0
            val highScore = prefs[HIGH_SCORE_KEY] ?: 0
            val totalPops = prefs[TOTAL_POPS] ?: 0
            val bestClickPercent = prefs[BEST_CLICK_PERCENT] ?: 0
            val challengesCompleted = prefs[CHALLENGES_COMPLETED_COUNT] ?:  0
            val level = calculateLevelFromPops(totalPops)
            val bestStreak = prefs[HIGH_SCORE_PERFECT_STREAK] ?: 0
            val maxConsecutiveDays = prefs[MAX_CONSECUTIVE_DAYS] ?: 0

            firebaseRepository.updatePlayerProfile(
                oduserId = oduserId,
                name = name,
                avatarRes = if (avatarRes != 0) avatarRes else R.drawable.profileuser0,
                generatedAvatarId = generatedAvatarId,
                bannerColorId = bannerColorId,
                highScore = highScore,
                totalPops = totalPops,
                bestClickPercent = bestClickPercent,
                challengesCompleted = challengesCompleted,
                level = level,
                bestStreak = bestStreak,
                maxConsecutiveDays = maxConsecutiveDays
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene el leaderboard de Firebase en tiempo real
     */
    fun getFirebaseLeaderboardFlow(): Flow<List<FirebaseTournamentEntry>> {
        return firebaseRepository.getLeaderboardFlow()
    }

    /**
     * Obtiene la info del torneo de Firebase
     */
    suspend fun getFirebaseTournamentInfo(): TournamentInfo?  {
        return firebaseRepository.getTournamentInfo()
    }

    /**
     * Sincroniza el epoch del torneo con Firebase
     * Si no existe en Firebase, crea uno nuevo
     * Si existe, usa el de Firebase
     */
    suspend fun syncTournamentEpochWithFirebase(): Long {
        return try {
            firebaseRepository.ensureAnonymousAuth()

            val firebaseInfo = firebaseRepository. getTournamentInfo()
            val now = System.currentTimeMillis()

            if (firebaseInfo != null && firebaseInfo.epochMillis > 0) {
                val totalMs = getTournamentDurationMs()
                val elapsedMs = now - firebaseInfo. epochMillis

                // Verificar si el torneo expiró completamente
                if (elapsedMs >= totalMs) {
                    // Auto-reiniciar torneo
                    val newEpoch = now
                    firebaseRepository.resetTournament(newEpoch)
                    context.dataStore.edit { prefs ->
                        prefs[TOURNAMENT_EPOCH_MILLIS] = newEpoch
                        prefs[TOURNAMENT_LEADERBOARD_JSON] = ""
                        prefs[TOURNAMENT_SCORE] = 0
                    }
                    newEpoch
                } else {
                    // Usar el epoch de Firebase
                    context.dataStore.edit { prefs ->
                        prefs[TOURNAMENT_EPOCH_MILLIS] = firebaseInfo. epochMillis
                    }
                    firebaseInfo.epochMillis
                }
            } else {
                // No hay torneo en Firebase, crear uno nuevo
                val newEpoch = now
                firebaseRepository.createOrUpdateTournamentInfo(newEpoch)
                context.dataStore.edit { prefs ->
                    prefs[TOURNAMENT_EPOCH_MILLIS] = newEpoch
                }
                newEpoch
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tournamentEpochMillisFlow().first().let {
                if (it == 0L) System.currentTimeMillis() else it
            }
        }
    }

    /**
     * Inicia un nuevo torneo (solo para admin/testing)
     */
    suspend fun startNewTournamentWithFirebase(): Boolean {
        return try {
            val newEpoch = System.currentTimeMillis()

            // Resetear en Firebase
            val success = firebaseRepository. resetTournament(newEpoch)

            if (success) {
                // Resetear local también
                context.dataStore.edit { prefs ->
                    prefs[TOURNAMENT_EPOCH_MILLIS] = newEpoch
                    prefs[TOURNAMENT_LEADERBOARD_JSON] = ""
                    prefs[TOURNAMENT_SCORE] = 0
                }
            }

            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene el rank del jugador actual en Firebase
     */
    suspend fun getMyRankFromFirebase(): Int {
        return try {
            val oduserId = getUserId()
            if (oduserId.isBlank()) return 0
            firebaseRepository.getPlayerRank(oduserId)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Verifica si el jugador existe en el leaderboard de Firebase
     */
    suspend fun isPlayerInFirebaseLeaderboard(): Boolean {
        return try {
            val oduserId = getUserId()
            if (oduserId. isBlank()) return false
            firebaseRepository.playerExistsInLeaderboard(oduserId)
        } catch (e: Exception) {
            e. printStackTrace()
            false
        }
    }

    /**
     * Fuerza una sincronización completa del perfil con Firebase
     * Útil para llamar al abrir el TournamentLobby
     */
    suspend fun forceSyncProfileToFirebase(): Boolean {
        return try {
            val oduserId = getUserId()
            if (oduserId.isBlank()) return false

            // Verificar si ya existe en Firebase
            val exists = firebaseRepository. playerExistsInLeaderboard(oduserId)

            if (exists) {
                // Solo actualizar perfil
                updateProfileInFirebase()
            } else {
                // No existe, se creará cuando envíe su primer score
                // Pero podemos pre-crear con score 0 para que aparezca
                firebaseRepository.ensureAnonymousAuth()

                val prefs = context. dataStore.data. first()
                val name = prefs[PROFILE_NAME]?.ifBlank { "Player" } ?: "Player"
                val avatarRes = prefs[PROFILE_DRAWABLE] ?: 0
                val generatedAvatarId = prefs[GENERATED_AVATAR_ID] ?: 0
                val bannerColorId = prefs[BANNER_COLOR] ?: 0
                val highScore = prefs[HIGH_SCORE_KEY] ?: 0
                val totalPops = prefs[TOTAL_POPS] ?: 0
                val bestClickPercent = prefs[BEST_CLICK_PERCENT] ?: 0
                val challengesCompleted = prefs[CHALLENGES_COMPLETED_COUNT] ?:  0
                val level = calculateLevelFromPops(totalPops)
                val bestStreak = prefs[HIGH_SCORE_PERFECT_STREAK] ?: 0
                val maxConsecutiveDays = prefs[MAX_CONSECUTIVE_DAYS] ?: 0

                firebaseRepository.updatePlayerProfile(
                    oduserId = oduserId,
                    name = name,
                    avatarRes = if (avatarRes != 0) avatarRes else R.drawable. profileuser0,
                    generatedAvatarId = generatedAvatarId,
                    bannerColorId = bannerColorId,
                    highScore = highScore,
                    totalPops = totalPops,
                    bestClickPercent = bestClickPercent,
                    challengesCompleted = challengesCompleted,
                    level = level,
                    bestStreak = bestStreak,
                    maxConsecutiveDays = maxConsecutiveDays
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}