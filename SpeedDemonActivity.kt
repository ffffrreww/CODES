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

// ==================== SPEED DEMON ACTIVITY ====================
// Challenge 4: Pop bubbles as fast as possible in a limited time
// Target: 5000 points in 60 seconds

class SpeedDemonActivity : ComponentActivity() {
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
                    SpeedDemonScreen(onExit = { finish() })
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
fun SpeedDemonScreen(onExit: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val ds = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var score by remember { mutableIntStateOf(0) }
    var timeRemaining by remember { mutableIntStateOf(60) }
    var isPlaying by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    
    val highScore by ds.highScoreSpeedDemonFlow().collectAsState(initial = 0)
    val targetScore = 5000
    
    // Simple timer
    LaunchedEffect(isPlaying) {
        if (isPlaying && timeRemaining > 0) {
            kotlinx.coroutines.delay(1000)
            timeRemaining--
            if (timeRemaining <= 0) {
                isPlaying = false
                showResults = true
                
                // Save score if it's a new high score
                if (score > highScore) {
                    coroutineScope.launch {
                        ds.saveHighScoreSpeedDemon(score)
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
                text = "⚡ SPEED DEMON",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 40.dp)
            )
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard("Score", score.toString(), Color(0xFFFFD700))
                StatCard("Time", "${timeRemaining}s", Color(0xFFFF6D00))
                StatCard("Target", targetScore.toString(), Color(0xFF4CAF50))
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
                            text = "🎯 Pop bubbles quickly!\nClick to earn points!",
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
                            score = 0
                            timeRemaining = 60
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
                challengeName = "Speed Demon",
                score = score,
                targetScore = targetScore,
                onClaim = { percentage ->
                    coroutineScope.launch {
                        ds.claimChallengeReward(4, percentage)
                    }
                },
                onDismiss = { showResults = false },
                ds = ds,
                challengeId = 4
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainOutlinedText(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            MainOutlinedText(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ChallengeResultsDialog(
    challengeName: String,
    score: Int,
    targetScore: Int,
    onClaim: (Int) -> Unit,
    onDismiss: () -> Unit,
    ds: DataStoreManager,
    challengeId: Int
) {
    val progress = (score.toFloat() / targetScore.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()
    
    val claimed30 by ds.isChallengeRewardClaimedFlow(challengeId, 30).collectAsState(initial = false)
    val claimed60 by ds.isChallengeRewardClaimedFlow(challengeId, 60).collectAsState(initial = false)
    val claimed100 by ds.isChallengeRewardClaimedFlow(challengeId, 100).collectAsState(initial = false)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MainOutlinedText(
                    text = "🏆 $challengeName",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                MainOutlinedText(
                    text = "Score: $score / $targetScore",
                    fontSize = 16.sp,
                    color = Color(0xFFFFD700)
                )
            }
        },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF6D00)
                )
                Spacer(modifier = Modifier.height(8.dp))
                MainOutlinedText(
                    text = "$percentage% Complete",
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rewards
                if (percentage >= 30 && !claimed30) {
                    Button(
                        onClick = { onClaim(30) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Claim 30% Reward")
                    }
                }
                if (percentage >= 60 && !claimed60) {
                    Button(
                        onClick = { onClaim(60) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Claim 60% Reward")
                    }
                }
                if (percentage >= 100 && !claimed100) {
                    Button(
                        onClick = { onClaim(100) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Claim 100% Reward")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
