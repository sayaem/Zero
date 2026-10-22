package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirBackground)
            .testTag("screen_splash"),
        contentAlignment = Alignment.Center
    ) {
        // Atmospheric rain and lamppost silhouette canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Wet asphalt street reflection
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF071019),
                        Color(0xFF0B1722),
                        Color(0xFF142432)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Streetlamp light glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentAmber.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.7f
                )
            )

            // Rain lines
            val rainStep = 45f
            for (x in 0 until w.toInt() step rainStep.toInt()) {
                val startY = ((x * 37) % h.toInt()).toFloat()
                drawLine(
                    color = AccentBlue.copy(alpha = 0.15f),
                    start = Offset(x.toFloat(), startY),
                    end = Offset(x.toFloat() - 15f, startY + 60f),
                    strokeWidth = 1.5f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Detective fedora / trench coat emblem badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(NoirSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.displayLarge,
                    color = CrimeSceneRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CASE ZERO",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TRUTH LIES IN DETAILS",
                style = MaterialTheme.typography.labelMedium,
                color = AccentAmber,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Subtle progress pill indicator
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NoirSurfaceInteractive)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(40.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AccentAmber.copy(alpha = alphaAnim))
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            pageIndex = 0,
            badge = "OBSERVE",
            title = "Investigate\nReal Mysteries",
            description = "Dive into detailed crime scenes, analyze evidence, and uncover the truth hidden beneath the surface."
        ),
        OnboardingPageData(
            pageIndex = 1,
            badge = "INTERROGATE",
            title = "Question\nEvery Suspect",
            description = "Every person has a story. But some hide the truth. Challenge statements with cold, hard proof."
        ),
        OnboardingPageData(
            pageIndex = 2,
            badge = "DEDUCE",
            title = "Connect\nthe Clues",
            description = "Build your theory, find contradictions on your investigation board, and piece together the real story."
        )
    )

    val currentData = pages[currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("screen_onboarding")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CASE ZERO",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    letterSpacing = 2.sp
                )

                if (currentPage < pages.size - 1) {
                    TextButton(
                        onClick = onFinished,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Center Illustration & Content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bespoke atmospheric illustration per step
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw backdrop vignette
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        when (currentPage) {
                                            0 -> AccentAmber.copy(alpha = 0.2f)
                                            1 -> AccentBlue.copy(alpha = 0.2f)
                                            else -> CrimeSceneRed.copy(alpha = 0.2f)
                                        },
                                        Color.Transparent
                                    ),
                                    center = Offset(w * 0.5f, h * 0.45f),
                                    radius = w * 0.65f
                                )
                            )

                            // Decorative noir lines & nodes
                            when (currentPage) {
                                0 -> {
                                    // Desk, magnifying glass, document lines
                                    drawCircle(AccentAmber, radius = 48f, center = Offset(w * 0.5f, h * 0.42f), style = Stroke(width = 4f))
                                    drawLine(AccentAmber, start = Offset(w * 0.5f + 34f, h * 0.42f + 34f), end = Offset(w * 0.5f + 70f, h * 0.42f + 70f), strokeWidth = 8f)
                                    // Crime scene file lines
                                    for (i in 0..3) {
                                        drawLine(TextMuted.copy(alpha = 0.5f), start = Offset(w * 0.35f, h * 0.68f + i * 16f), end = Offset(w * 0.65f, h * 0.68f + i * 16f), strokeWidth = 2f)
                                    }
                                }
                                1 -> {
                                    // Suspect portraits grid with pins
                                    val centers = listOf(
                                        Offset(w * 0.33f, h * 0.35f),
                                        Offset(w * 0.67f, h * 0.35f),
                                        Offset(w * 0.33f, h * 0.62f),
                                        Offset(w * 0.67f, h * 0.62f)
                                    )
                                    centers.forEach { center ->
                                        drawCircle(NoirSurfaceInteractive, radius = 34f, center = center)
                                        drawCircle(AccentBlue, radius = 34f, center = center, style = Stroke(width = 2f))
                                        drawCircle(AccentAmber, radius = 4f, center = Offset(center.x, center.y - 30f)) // pin
                                    }
                                }
                                2 -> {
                                    // Corkboard with red string links
                                    val p1 = Offset(w * 0.28f, h * 0.32f)
                                    val p2 = Offset(w * 0.72f, h * 0.32f)
                                    val center = Offset(w * 0.5f, h * 0.52f)
                                    val p3 = Offset(w * 0.35f, h * 0.72f)
                                    val p4 = Offset(w * 0.65f, h * 0.72f)

                                    // Red strings
                                    drawLine(RedStringColor, start = p1, end = center, strokeWidth = 3f)
                                    drawLine(RedStringColor, start = p2, end = center, strokeWidth = 3f)
                                    drawLine(RedStringColor, start = p3, end = center, strokeWidth = 3f)
                                    drawLine(RedStringColor, start = p4, end = center, strokeWidth = 3f)

                                    listOf(p1, p2, p3, p4, center).forEach { pt ->
                                        drawCircle(NoirSurfaceInteractive, radius = 22f, center = pt)
                                        drawCircle(if (pt == center) CrimeSceneRed else AccentAmber, radius = 5f, center = pt)
                                    }
                                }
                            }
                        }

                        // Badge at top of card
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = NoirSurfaceInteractive
                        ) {
                            Text(
                                text = currentData.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentAmber,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = currentData.title,
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentData.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Bottom Navigation Dots & CTA
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in pages.indices) {
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (i == currentPage) 24.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i == currentPage) AccentAmber else NoirSurfaceInteractive)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag(if (currentPage == pages.size - 1) "onboarding_get_started" else "onboarding_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Next" else "Get Started",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private data class OnboardingPageData(
    val pageIndex: Int,
    val badge: String,
    val title: String,
    val description: String
)
