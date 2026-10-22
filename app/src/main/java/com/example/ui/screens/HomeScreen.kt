package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaseCategory
import com.example.model.CaseModel
import com.example.model.CaseStatus
import com.example.ui.components.CaseZeroBottomNavBar
import com.example.ui.components.DifficultyStars
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val bottomNavTab by viewModel.bottomNavSelected.collectAsState()

    val greeting = rememberGreeting()
    val activeCase = cases.firstOrNull { it.id == profile.activeCaseId } ?: cases.firstOrNull()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_home"),
        containerColor = NoirBackground,
        bottomBar = {
            CaseZeroBottomNavBar(
                selectedTab = bottomNavTab,
                onTabSelected = { viewModel.onBottomNavSelected(it) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Bar
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Detective",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Detective Badge Quick Action
                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.navigateTo(Screen.DetectiveRank) }
                            .testTag("home_rank_badge_button"),
                        shape = CircleShape,
                        color = NoirSurfaceElevated,
                        border = BorderStroke(1.dp, AccentAmberMuted)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = "Detective Rank",
                                tint = AccentAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Featured Daily Case Card (Matching Mockup 1:1)
            item {
                if (activeCase != null) {
                    FeaturedCaseCard(
                        caseModel = activeCase,
                        onContinue = {
                            viewModel.selectCase(activeCase.id)
                            viewModel.navigateTo(Screen.CaseOverview)
                        }
                    )
                }
            }

            // Your Progress Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Rank Card
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(Screen.DetectiveRank) }
                            .testTag("home_progress_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = NoirSurfaceElevated,
                        border = BorderStroke(1.dp, DividerColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NoirSurfaceInteractive),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WorkspacePremium,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Detective Rank",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = profile.rank.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // XP Progress bar
                                val fraction = (profile.currentXp.toFloat() / profile.rank.maxXp).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { fraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = AccentAmber,
                                    trackColor = NoirSurfaceInteractive
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${profile.currentXp} / ${profile.rank.maxXp} XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentAmber
                                    )
                                    Text(
                                        text = "${profile.rank.maxXp - profile.currentXp} XP to next rank",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Stats Row: Cases Solved, Win Rate, Current Streak
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Cases Solved",
                            value = "${profile.casesSolved}",
                            modifier = Modifier.weight(1f),
                            highlight = true
                        )
                        StatCard(
                            title = "Win Rate",
                            value = "${profile.winRate}%",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Current Streak",
                            value = "${profile.currentStreak} days",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Quick Shortcut: Daily Challenge
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(Screen.DailyChallenge) }
                        .testTag("home_daily_challenge_shortcut"),
                    shape = RoundedCornerShape(16.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AccentAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Daily Challenge",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "The Missing Necklace • 5 min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open challenge",
                            tint = AccentAmber
                        )
                    }
                }
            }

            // Recent Cases Row Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Cases",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.CaseList) },
                        modifier = Modifier.testTag("home_view_all_cases")
                    ) {
                        Text(
                            text = "View All",
                            color = AccentAmber,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Recent cases list
            items(cases.size) { index ->
                val caseItem = cases[index]
                RecentCaseRowItem(
                    caseModel = caseItem,
                    onClick = {
                        viewModel.selectCase(caseItem.id)
                        viewModel.navigateTo(Screen.CaseOverview)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeaturedCaseCard(
    caseModel: CaseModel,
    onContinue: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onContinue)
            .testTag("home_featured_case_card"),
        shape = RoundedCornerShape(20.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(1.dp, AccentAmberMuted.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NoirSurfaceElevated,
                            NoirSurfaceInteractive,
                            Color(0xFF0F1E2A)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentAmber.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, AccentAmberMuted)
                    ) {
                        Text(
                            text = "DAILY CASE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    DifficultyStars(difficulty = caseModel.difficulty)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = caseModel.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = caseModel.synopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("featured_case_continue_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (caseModel.status == CaseStatus.SOLVED) "Review Case" else "Continue Investigation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentCaseRowItem(
    caseModel: CaseModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("recent_case_${caseModel.id}"),
        shape = RoundedCornerShape(14.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NoirSurfaceInteractive),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (caseModel.category) {
                        CaseCategory.DAILY -> Icons.Filled.DateRange
                        CaseCategory.LONG_TERM -> Icons.Filled.Folder
                        else -> Icons.Filled.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (caseModel.status == CaseStatus.SOLVED) SolvedGreen else AccentAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = caseModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (caseModel.status == CaseStatus.SOLVED) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Solved",
                            tint = SolvedGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = caseModel.stagesText ?: caseModel.solvedDate ?: "Difficulty: ${caseModel.difficulty} stars",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        in 17..21 -> "Good Evening,"
        else -> "Late Night,"
    }
}
