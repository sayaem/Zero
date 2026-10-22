package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaseModel
import com.example.model.CaseStatus
import com.example.ui.components.DifficultyStars
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun CaseOverviewScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Case Info, 1: Suspects

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_case_overview"),
        containerColor = NoirBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hero Banner Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F2536),
                                    NoirSurfaceElevated,
                                    NoirBackground
                                )
                            )
                        )
                ) {
                    // Top App Bar Icons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.popBack() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NoirBackground.copy(alpha = 0.6f))
                                .testTag("overview_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = { /* info / share */ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(NoirBackground.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Bottom info in Hero Header
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentAmber.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, AccentAmberMuted)
                            ) {
                                Text(
                                    text = "DAILY CASE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentAmber,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            DifficultyStars(difficulty = caseModel.difficulty)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = caseModel.title,
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Case Synopsis
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = caseModel.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Big CTA: "Start Investigation" / "Continue"
                    Button(
                        onClick = { viewModel.navigateTo(Screen.CaseTimeline) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("overview_start_investigation_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (caseModel.status == CaseStatus.SOLVED) "Review Investigation" else "Start Investigation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Investigation Hub Navigation Shortcuts
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Investigation Files",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InvestigationShortcutCard(
                            title = "Timeline",
                            subtitle = "Crime Events",
                            icon = Icons.Filled.AccessTime,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(Screen.CaseTimeline) }
                        )
                        InvestigationShortcutCard(
                            title = "Evidence",
                            subtitle = "${caseModel.evidence.size} Clues",
                            icon = Icons.Filled.Search,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(Screen.EvidenceGallery) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InvestigationShortcutCard(
                            title = "Pinboard",
                            subtitle = "Connect Clues",
                            icon = Icons.Filled.Dashboard,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(Screen.InvestigationBoard) }
                        )
                        InvestigationShortcutCard(
                            title = "Accusation",
                            subtitle = "Name Killer",
                            icon = Icons.Filled.Gavel,
                            modifier = Modifier.weight(1f),
                            highlight = true,
                            onClick = { viewModel.navigateTo(Screen.FinalAccusation) }
                        )
                    }
                }
            }

            // Tabs: Case Info vs Suspects
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NoirSurface,
                    contentColor = AccentAmber,
                    divider = { HorizontalDivider(color = DividerColor) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Case Info",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Suspects (${caseModel.suspects.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (selectedTab == 0) {
                // Case Info items
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        InfoDetailRow(
                            icon = Icons.Filled.LocationOn,
                            label = "Location",
                            value = caseModel.location
                        )
                        InfoDetailRow(
                            icon = Icons.Filled.Person,
                            label = "Victim",
                            value = "${caseModel.victim.name} (${caseModel.victim.age})"
                        )
                        InfoDetailRow(
                            icon = Icons.Filled.AccessTime,
                            label = "Time",
                            value = caseModel.incidentTime
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Victim Profile Card
                        Text(
                            text = "Victim Profile",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(1.dp, DividerColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(CrimeSceneRed.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = caseModel.victim.portraitInitials,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = CrimeSceneRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = caseModel.victim.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Age: ${caseModel.victim.age} • ${caseModel.victim.occupation}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = caseModel.victim.relationship,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AccentBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = caseModel.victim.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(Screen.CaseTimeline) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = AccentAmber
                                    ),
                                    border = BorderStroke(1.dp, AccentAmberMuted)
                                ) {
                                    Text("View Crime Timeline")
                                }
                            }
                        }
                    }
                }
            } else {
                // Suspects tab items
                items(caseModel.suspects) { suspect ->
                    SuspectListItem(
                        suspect = suspect,
                        onInterrogate = {
                            viewModel.selectSuspectForInterrogation(suspect.id)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun InfoDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(NoirSurfaceInteractive),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InvestigationShortcutCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (highlight) NoirSurfaceInteractive else NoirSurfaceElevated,
        border = BorderStroke(1.dp, if (highlight) AccentAmberMuted else DividerColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlight) AccentAmber else AccentBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (highlight) AccentAmber else TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SuspectListItem(
    suspect: com.example.model.Suspect,
    onInterrogate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onInterrogate)
            .testTag("suspect_item_${suspect.id}"),
        shape = RoundedCornerShape(14.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(1.dp, if (suspect.isBroken) CrimeSceneRed.copy(alpha = 0.5f) else DividerColor)
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
                    .clip(CircleShape)
                    .background(Color(suspect.accentColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suspect.initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = suspect.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    if (suspect.isBroken) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CrimeSceneRed.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ALIBI BROKEN",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrimeSceneRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${suspect.roleAndAge} • ${suspect.occupation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "Motive: ${suspect.potentialMotive}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
