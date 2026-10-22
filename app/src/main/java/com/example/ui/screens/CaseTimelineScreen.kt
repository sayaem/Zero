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
import com.example.model.TimelineItem
import com.example.model.TimelineStatus
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun CaseTimelineScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Timeline, 1: Location, 2: Forensics
    var expandedTimelineId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_case_timeline"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Case File",
                onBack = { viewModel.popBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs: Timeline, Location, Forensics
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = NoirSurface,
                contentColor = AccentAmber,
                divider = { HorizontalDivider(color = DividerColor) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Timeline", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Location", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Forensics", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            when (activeTab) {
                0 -> {
                    // Timeline tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "INCIDENT TIMELINE",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentAmber,
                                letterSpacing = 2.sp
                            )
                        }

                        items(caseModel.timeline) { item ->
                            TimelineNodeItem(
                                item = item,
                                isExpanded = expandedTimelineId == item.id,
                                onToggle = {
                                    expandedTimelineId = if (expandedTimelineId == item.id) null else item.id
                                }
                            )
                        }

                        // Victim Profile summary section at bottom
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Victim Profile",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(CrimeSceneRed.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = caseModel.victim.portraitInitials,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = CrimeSceneRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
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
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                1 -> {
                    // Location tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "CRIME SCENE BLUEPRINT",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentAmber,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = caseModel.location,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = NoirSurfaceElevated,
                                border = BorderStroke(1.dp, DividerColor)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Apartment 4B - Floor Layout",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = AccentBlue
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "• Main Entrance: High-security steel deadbolt (locked from inside when police breached).\n" +
                                                    "• Living Room: Body discovered on center sofa facing the TV.\n" +
                                                    "• Balcony: Sliding glass door locked from interior with latch.\n" +
                                                    "• Service Alley: Ground floor rear tea-house alley leads to fire escape adjacent to 4th floor.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.EvidenceGallery) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentAmber,
                                    contentColor = NoirBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Examine Crime Scene Evidence")
                            }
                        }
                    }
                }
                2 -> {
                    // Forensics tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "CORONER & FORENSIC LOG",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentAmber,
                                letterSpacing = 2.sp
                            )
                        }

                        item {
                            ForensicCard(
                                title = "Cause of Death",
                                badge = "CONFIRMED",
                                details = "Mechanical asphyxiation via strangulation. Narrow, woven ligature furrows corresponding strictly to cashmere fabric texture.",
                                badgeColor = SolvedGreen
                            )
                        }

                        item {
                            ForensicCard(
                                title = "Toxicology Screening",
                                badge = "NEGATIVE",
                                details = "No foreign sedatives, narcotics, or alcohol detected in bloodstream. Victim was alert prior to the assault.",
                                badgeColor = AccentBlue
                            )
                        }

                        item {
                            ForensicCard(
                                title = "Lock Mechanism Inspection",
                                badge = "CRITICAL",
                                details = "Deadbolt shows fresh faint microscopic abrasion marks on the cylinder cam, consistent with external rotational pull via monofilament thread.",
                                badgeColor = CrimeSceneRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineNodeItem(
    item: TimelineItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical indicator & line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        when (item.status) {
                            TimelineStatus.CONFIRMED -> AccentAmber
                            TimelineStatus.REPORTED -> AccentBlue
                            TimelineStatus.DISPUTED -> CrimeSceneRed
                        }
                    )
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(DividerColor)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Card Content
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            color = NoirSurfaceElevated,
            border = BorderStroke(1.dp, DividerColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentAmber,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (item.status) {
                            TimelineStatus.CONFIRMED -> SolvedGreen.copy(alpha = 0.2f)
                            TimelineStatus.REPORTED -> AccentBlue.copy(alpha = 0.2f)
                            TimelineStatus.DISPUTED -> CrimeSceneRed.copy(alpha = 0.2f)
                        }
                    ) {
                        Text(
                            text = item.status.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (item.status) {
                                TimelineStatus.CONFIRMED -> SolvedGreen
                                TimelineStatus.REPORTED -> AccentBlue
                                TimelineStatus.DISPUTED -> CrimeSceneRed
                            },
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = item.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ForensicCard(
    title: String,
    badge: String,
    details: String,
    badgeColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}
