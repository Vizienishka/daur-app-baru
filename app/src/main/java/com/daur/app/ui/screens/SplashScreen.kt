package com.daur.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daur.app.ui.components.DaurLogo
import com.daur.app.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // ── Animasi masuk ────────────────────────────────────
    var visible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ), label = "logoScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Primary, Color(0xFF004D38))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            DaurLogo(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(alpha),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App name
            Text(
                text       = "DAUR",
                color      = Color.White,
                fontSize   = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
                modifier   = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text      = "Sampah Jadi Nilai",
                color     = Color.White.copy(alpha = 0.8f),
                fontSize  = 16.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(alpha)
            )
        }
    }
}
