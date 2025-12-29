package com.appsdevs.popit

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appsdevs.popit.ui.theme.PopITTheme
import kotlinx.coroutines.launch

// ==================== ENDURANCE MASTER ACTIVITY ====================
// Challenge 6: Survive as long as possible without missing
// Target: 3 minutes surviving (maximum 5 misses)

class EnduranceMasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } catch (_: Exception) { }

        enableEdgeToEdge()

        MusicController.initIfNeeded(applicationContext)
        SoundManager.init(applicationContext)

        setContent {
            PopITTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EnduranceMasterScreen(onExit = { finish() })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicController.stopGameMusic()
    }
}

@Composable
fun EnduranceMasterScreen(onExit: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ds = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var timeElapsed by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    
    val highScore by ds.highScoreEnduranceMasterFlow().collectAsState(initial = 0)
    val targetTime = 180 // 3 minutes in seconds
    val maxMisses = 5
    
    // Simple timer
    LaunchedEffect(isPlaying) {
        if (isPlaying && misses < maxMisses) {
            kotlinx.coroutines.delay(1000)
            timeElapsed++
            if (timeElapsed >= targetTime || misses >= maxMisses) {
                isPlaying = false
                showResults = true
                
                // Save score if it's a new high score
                if (timeElapsed > highScore) {
                    coroutineScope.launch {
                        ds.saveHighScoreEnduranceMaster(timeElapsed)
                    }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            MainOutlinedText(
                text = "🛡️ ENDURANCE MASTER",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 40.dp)
            )
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Time", "${timeElapsed}s", Color(0xFFFFD700))
                StatCard("Misses", "$misses / $maxMisses", 
                    if (misses >= 4) Color(0xFFFF5252) else Color(0xFF4CAF50))
                StatCard("Target", "${targetTime}s", Color(0xFFFF6D00))
            }
            
            // Best score
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MainOutlinedText(text = "Best Time:", fontSize = 16.sp)
                    MainOutlinedText(
                        text = "${highScore}s",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Play area placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPlaying && !showResults) {
                        MainOutlinedText(
                            text = "Tap START to begin!",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    } else if (isPlaying) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            MainOutlinedText(
                                text = "🛡️ Survive!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            MainOutlinedText(
                                text = "Don't miss more than $maxMisses bubbles!",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Buttons
            if (!isPlaying) {
                Button(
                    onClick = {
                        if (showResults) {
                            timeElapsed = 0
                            misses = 0
                            showResults = false
                        }
                        isPlaying = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00)
                    )
                ) {
                    MainOutlinedText(
                        text = if (showResults) "PLAY AGAIN" else "START",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Button(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF424242)
                )
            ) {
                MainOutlinedText(
                    text = "EXIT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Results dialog
        if (showResults) {
            ChallengeResultsDialog(
                challengeName = "Endurance Master",
                score = timeElapsed,
                targetScore = targetTime,
                onClaim = { percentage ->
                    coroutineScope.launch {
                        ds.claimChallengeReward(6, percentage)
                    }
                },
                onDismiss = { showResults = false },
                ds = ds,
                challengeId = 6
            )
        }
    }
}
