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

// ==================== ACCURACY KING ACTIVITY ====================
// Challenge 5: Pop bubbles with perfect accuracy
// Target: 100 bubbles with 95% accuracy

class AccuracyKingActivity : ComponentActivity() {
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
                    AccuracyKingScreen(onExit = { finish() })
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
fun AccuracyKingScreen(onExit: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ds = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var bubblesPopped by remember { mutableIntStateOf(0) }
    var totalClicks by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    
    val highScore by ds.highScoreAccuracyKingFlow().collectAsState(initial = 0)
    val targetBubbles = 100
    val accuracy = if (totalClicks > 0) ((bubblesPopped.toFloat() / totalClicks.toFloat()) * 100).toInt() else 0
    
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
                text = "🎪 ACCURACY KING",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 40.dp)
            )
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Popped", bubblesPopped.toString(), Color(0xFFFFD700))
                StatCard("Accuracy", "$accuracy%", Color(0xFF4CAF50))
                StatCard("Target", "$targetBubbles", Color(0xFFFF6D00))
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
                    MainOutlinedText(text = "Best Score:", fontSize = 16.sp)
                    MainOutlinedText(
                        text = "$highScore",
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
                        MainOutlinedText(
                            text = "🎯 Perfect Accuracy!\nMaintain 95%+ accuracy!",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Buttons
            if (!isPlaying) {
                Button(
                    onClick = {
                        if (showResults) {
                            bubblesPopped = 0
                            totalClicks = 0
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
            } else {
                Button(
                    onClick = {
                        isPlaying = false
                        showResults = true
                        
                        // Calculate final score (bubbles popped with accuracy bonus)
                        val finalScore = if (accuracy >= 95) bubblesPopped * 2 else bubblesPopped
                        
                        // Save score if it's a new high score
                        if (finalScore > highScore) {
                            coroutineScope.launch {
                                ds.saveHighScoreAccuracyKing(finalScore)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    MainOutlinedText(
                        text = "FINISH",
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
            val finalScore = if (accuracy >= 95) bubblesPopped * 2 else bubblesPopped
            ChallengeResultsDialog(
                challengeName = "Accuracy King",
                score = finalScore,
                targetScore = targetBubbles,
                onClaim = { percentage ->
                    coroutineScope.launch {
                        ds.claimChallengeReward(5, percentage)
                    }
                },
                onDismiss = { showResults = false },
                ds = ds,
                challengeId = 5
            )
        }
    }
}
