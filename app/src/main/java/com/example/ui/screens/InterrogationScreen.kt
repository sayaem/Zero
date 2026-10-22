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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EvidenceItem
import com.example.model.Suspect
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun InterrogationScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val selectedSuspectId by viewModel.selectedSuspectId.collectAsState()
    val feedbackMessage by viewModel.userFeedback.collectAsState()

    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()
    val suspect = caseModel.suspects.firstOrNull { it.id == selectedSuspectId }
        ?: caseModel.suspects.firstOrNull() ?: return

    var activeTab by remember { mutableIntStateOf(0) } // 0: Profile, 1: Statement, 2: Alibi
    var showEvidenceChallengePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_interrogation"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = suspect.name,
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
            // Suspect Hero Portrait
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(suspect.accentColor).copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color(suspect.accentColor)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = suspect.initials,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = suspect.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = suspect.roleAndAge,
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentAmber
                            )
                        }
                    }
                }
            }

            // Tabs: Profile, Statement, Alibi
            item {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = NoirSurface,
                    contentColor = AccentAmber,
                    divider = { HorizontalDivider(color = DividerColor) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Profile", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Statement", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Alibi", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (activeTab) {
                0 -> {
                    // Profile details
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                ProfileRow(
                                    icon = Icons.Filled.Work,
                                    label = "Occupation",
                                    value = suspect.occupation
                                )
                                ProfileRow(
                                    icon = Icons.Filled.Group,
                                    label = "Relationship with Victim",
                                    value = suspect.relationshipToVictim
                                )
                                ProfileRow(
                                    icon = Icons.Filled.Warning,
                                    label = "Possible Motive",
                                    value = suspect.potentialMotive
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Statement
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(
                                1.dp,
                                if (suspect.isBroken) CrimeSceneRed else DividerColor
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "OFFICIAL STATEMENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (suspect.isBroken) CrimeSceneRed else AccentAmber,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "\"${suspect.statement}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 24.sp
                                )

                                if (suspect.isBroken) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = CrimeSceneRed.copy(alpha = 0.4f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "BROKEN ALIBI / CONTRADICTION REVEALED:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CrimeSceneRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = suspect.confessionOrClue,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Alibi
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(1.dp, DividerColor)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "REPORTED ALIBI",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentBlue,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = suspect.alibi,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            // Challenge with Evidence Action Button
            item {
                Button(
                    onClick = { showEvidenceChallengePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("interrogation_challenge_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Balance,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compare Statement with Evidence",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Other suspects selector quick strip
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Question other suspects:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    caseModel.suspects.forEach { s ->
                        val isCurrent = s.id == suspect.id
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.selectSuspectForInterrogation(s.id) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) AccentAmber.copy(alpha = 0.2f) else NoirSurfaceElevated,
                            border = BorderStroke(1.dp, if (isCurrent) AccentAmber else DividerColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = s.initials,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isCurrent) AccentAmber else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = s.name.split(" ").first(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Evidence Challenge Picker Dialog
    if (showEvidenceChallengePicker) {
        Dialog(onDismissRequest = { showEvidenceChallengePicker = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("dialog_challenge_evidence"),
                shape = RoundedCornerShape(20.dp),
                color = NoirSurface,
                border = BorderStroke(1.dp, AccentAmberMuted)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Challenge Statement",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select an evidence item to expose a contradiction in ${suspect.name}'s alibi:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(caseModel.evidence) { ev ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.challengeSuspectWithEvidence(suspect.id, ev.id)
                                        showEvidenceChallengePicker = false
                                    }
                                    .testTag("challenge_with_${ev.id}"),
                                shape = RoundedCornerShape(10.dp),
                                color = NoirSurfaceElevated,
                                border = BorderStroke(1.dp, DividerColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = AccentAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = ev.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = ev.shortDescription,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { showEvidenceChallengePicker = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            }
        }
    }

    // Feedback message alert
    if (feedbackMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearFeedback() },
            title = { Text("Interrogation Result", color = TextPrimary) },
            text = { Text(feedbackMessage!!, color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearFeedback() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    )
                ) {
                    Text("Understood")
                }
            },
            containerColor = NoirSurface
        )
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(NoirSurfaceInteractive),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(18.dp)
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
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
