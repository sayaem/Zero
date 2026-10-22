package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Achievement
import com.example.model.DetectiveRank
import com.example.ui.components.CaseZeroBottomNavBar
import com.example.ui.components.DifficultyStars
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun DetectiveRankScreen(
    viewModel: CaseZeroViewModel
) {
    val profile by viewModel.profile.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val bottomNavTab by viewModel.bottomNavSelected.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_detective_rank"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Detective Rank",
                onBack = { viewModel.popBack() }
            )
        },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rank Badge Card
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(NoirSurfaceElevated)
                        .border(2.dp, AccentAmberMuted, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WorkspacePremium,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = profile.rank.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${profile.currentXp} / ${profile.rank.maxXp} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentAmber
                )

                Spacer(modifier = Modifier.height(12.dp))

                // XP Progress Bar
                val fraction = (profile.currentXp.toFloat() / profile.rank.maxXp).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentAmber,
                    trackColor = NoirSurfaceInteractive
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Next Rank: ${if (profile.rank != DetectiveRank.CHIEF) DetectiveRank.values()[profile.rank.ordinal + 1].title else "Max Rank"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "${(profile.rank.maxXp - profile.currentXp).coerceAtLeast(0)} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Achievements Header
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Achievements List
            items(achievements) { ach ->
                AchievementRowItem(achievement = ach)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AchievementRowItem(achievement: Achievement) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(
            1.dp,
            if (achievement.isUnlocked) AccentAmber.copy(alpha = 0.5f) else DividerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked) AccentAmber.copy(alpha = 0.2f) else NoirSurfaceInteractive
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (achievement.isUnlocked) Icons.Filled.MilitaryTech else Icons.Filled.Lock,
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) AccentAmber else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (achievement.unlockedDate != null) {
                    Text(
                        text = "Unlocked: ${achievement.unlockedDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SolvedGreen
                    )
                }
            }

            if (achievement.isUnlocked) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = SolvedGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DailyChallengeScreen(
    viewModel: CaseZeroViewModel
) {
    val challenge by viewModel.dailyChallenge.collectAsState()
    val profile by viewModel.profile.collectAsState()
    var selectedOption by remember { mutableStateOf<Int?>(challenge.selectedOption) }
    var showResult by remember { mutableStateOf(challenge.isCompleted) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_daily_challenge"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Daily Challenge",
                onBack = { viewModel.popBack() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "TODAY'S DEDUCTION PUZZLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentAmber,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                DifficultyStars(difficulty = challenge.difficulty)
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = challenge.premise,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoirSurfaceInteractive
                        ) {
                            Text(
                                text = "CLUE: ${challenge.clueText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentAmber,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Who took the necklace?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(challenge.options.size) { index ->
                val option = challenge.options[index]
                val isSelected = selectedOption == index
                val isCorrect = challenge.isCompleted && index == challenge.correctIndex
                val isWrong = challenge.isCompleted && isSelected && index != challenge.correctIndex

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !challenge.isCompleted) {
                            selectedOption = index
                        }
                        .testTag("challenge_option_$index"),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NoirSurfaceInteractive else NoirSurfaceElevated,
                    border = BorderStroke(
                        1.dp,
                        when {
                            isCorrect -> SolvedGreen
                            isWrong -> CrimeSceneRed
                            isSelected -> AccentAmber
                            else -> DividerColor
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { if (!challenge.isCompleted) selectedOption = index },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentAmber),
                            enabled = !challenge.isCompleted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                isCorrect -> SolvedGreen
                                isWrong -> CrimeSceneRed
                                else -> TextPrimary
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            if (!challenge.isCompleted) {
                item {
                    Button(
                        onClick = {
                            if (selectedOption != null) {
                                viewModel.completeDailyChallenge(selectedOption!!)
                                showResult = true
                            }
                        },
                        enabled = selectedOption != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("daily_challenge_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Submit Deduction", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = if (challenge.selectedOption == challenge.correctIndex) SolvedGreen.copy(alpha = 0.15f) else CrimeSceneRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (challenge.selectedOption == challenge.correctIndex) SolvedGreen else CrimeSceneRed)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (challenge.selectedOption == challenge.correctIndex) "Solved! +50 XP" else "Deduction Missed",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (challenge.selectedOption == challenge.correctIndex) SolvedGreen else CrimeSceneRed,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = challenge.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            // Streak card matching Mockup 1:1
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${profile.currentStreak} Days Streak",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Keep your detective streak alive!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Days of week indicator
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            days.forEachIndexed { i, day ->
                                val isActive = i < 4
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isActive) AccentAmber else NoirSurfaceInteractive),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isActive) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = NoirBackground,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isActive) AccentAmber else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: CaseZeroViewModel
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val language by viewModel.language.collectAsState()
    val dailyReminder by viewModel.dailyReminderEnabled.collectAsState()
    val weeklyChallenge by viewModel.weeklyChallengeEnabled.collectAsState()
    val bottomNavTab by viewModel.bottomNavSelected.collectAsState()

    var showLegalDialog by remember { mutableStateOf<String?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_settings"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Settings",
                onBack = { viewModel.popBack() }
            )
        },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.repository.setLanguage("English") }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("English", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                            if (language == "English") {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = AccentAmber)
                            }
                        }
                    }
                }
            }

            // Notifications
            item {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Daily Case Reminder", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                                Text("Notify when today's case is available", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = dailyReminder,
                                onCheckedChange = { viewModel.repository.setDailyReminder(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NoirBackground,
                                    checkedTrackColor = AccentAmber
                                )
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Weekly Challenge", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                                Text("New investigative puzzle alerts", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = weeklyChallenge,
                                onCheckedChange = { viewModel.repository.setWeeklyChallenge(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NoirBackground,
                                    checkedTrackColor = AccentAmber
                                )
                            )
                        }
                    }
                }
            }

            // Theme Appearance
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Noir Dark Theme", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                            Text("Cinematic high-contrast obsidian", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { viewModel.repository.setDarkTheme(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NoirBackground,
                                checkedTrackColor = AccentAmber
                            )
                        )
                    }
                }
            }

            // About & Legal
            item {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column {
                        SettingsNavRow(title = "About Case Zero", onClick = { showLegalDialog = "about" })
                        HorizontalDivider(color = DividerColor)
                        SettingsNavRow(title = "Privacy Policy", onClick = { showLegalDialog = "privacy" })
                        HorizontalDivider(color = DividerColor)
                        SettingsNavRow(title = "Terms & Conditions", onClick = { showLegalDialog = "terms" })
                    }
                }
            }

            // Danger Zone: Reset Progress
            item {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("settings_reset_progress_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CrimeSceneRed
                    ),
                    border = BorderStroke(1.dp, CrimeSceneRedDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All Progress", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Legal & About Dialog
    if (showLegalDialog != null) {
        val (dialogTitle, dialogContent) = when (showLegalDialog) {
            "about" -> Pair(
                "About Case Zero",
                "CASE ZERO is an offline-first fictional detective mystery game built with Jetpack Compose.\n\nAll cases, characters, and events are entirely fictional works of deductive mystery logic. No personal user data is collected or transmitted."
            )
            "privacy" -> Pair(
                "Privacy Policy",
                "Case Zero respects your privacy. All game progress, case notes, and investigation board pins are stored locally on your device.\n\nNo analytics trackers, personal identifiers, or cloud databases are used. Your data remains strictly private."
            )
            else -> Pair(
                "Terms & Conditions",
                "By using Case Zero, you agree to fair-play deductive reasoning and enjoying fictional narrative crime puzzles for entertainment.\n\nAll authored case materials and game logic are protected original works."
            )
        }

        Dialog(onDismissRequest = { showLegalDialog = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NoirSurface,
                border = BorderStroke(1.dp, AccentAmberMuted),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = dialogContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showLegalDialog = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        )
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Confirm Reset
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset All Progress?", color = TextPrimary) },
            text = { Text("This will erase all solved cases, evidence notes, pinboard connections, and detective XP. This cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllProgress()
                        showResetConfirm = false
                    }
                ) {
                    Text("Erase Everything", color = CrimeSceneRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = NoirSurface
        )
    }
}

@Composable
private fun SettingsNavRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
