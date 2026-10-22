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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Suspect
import com.example.ui.components.CircularScoreGauge
import com.example.ui.components.NoirTopAppBar
import com.example.ui.components.SolvedRubberStamp
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun FinalAccusationScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val accuseCulpritId by viewModel.accuseCulpritId.collectAsState()
    val accuseMotive by viewModel.accuseMotive.collectAsState()
    val accuseMethod by viewModel.accuseMethod.collectAsState()
    val selectedEvidenceIds by viewModel.accuseEvidenceIds.collectAsState()

    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()

    var showConfirmationDialog by remember { mutableStateOf(false) }

    val motivesList = listOf(
        "Financial gain / debt evasion ($15,000)",
        "Crime of passion / jealousy",
        "Silencing a commercial leak",
        "Accidental confrontation"
    )

    val methodsList = listOf(
        "Strangulation (using scarf) + thread & magnet deadbolt trick",
        "Blunt force trauma with heavy object",
        "Poisoning of evening tea",
        "Forced fall from balcony railing"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_final_accusation"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Final Accusation",
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "THE MOMENT OF TRUTH",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentAmber,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Who is the killer?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Suspect selection
            items(caseModel.suspects) { suspect ->
                val isSelected = accuseCulpritId == suspect.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.setAccuseCulprit(suspect.id) }
                        .testTag("accuse_suspect_${suspect.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) NoirSurfaceInteractive else NoirSurfaceElevated,
                    border = BorderStroke(1.5.dp, if (isSelected) AccentAmber else DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setAccuseCulprit(suspect.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AccentAmber,
                                unselectedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = suspect.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = suspect.roleAndAge,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) AccentAmber else TextSecondary
                            )
                        }
                    }
                }
            }

            // Motive selection
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "What was the motive?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(motivesList) { motive ->
                val isSelected = accuseMotive == motive
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setAccuseMotive(motive) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NoirSurfaceInteractive else NoirSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSelected) AccentAmber else DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setAccuseMotive(motive) },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = motive,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Method selection
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "How was the crime committed?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(methodsList) { method ->
                val isSelected = accuseMethod == method
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.setAccuseMethod(method) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NoirSurfaceInteractive else NoirSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSelected) AccentAmber else DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setAccuseMethod(method) },
                            colors = RadioButtonDefaults.colors(selectedColor = AccentAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Decisive evidence selection
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Select decisive supporting clues:",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pick the clues that conclusively prove guilt (${selectedEvidenceIds.size} selected)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            items(caseModel.evidence) { ev ->
                val isChecked = selectedEvidenceIds.contains(ev.id)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleAccuseEvidence(ev.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isChecked) NoirSurfaceInteractive else NoirSurfaceElevated,
                    border = BorderStroke(1.dp, if (isChecked) AccentAmber else DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleAccuseEvidence(ev.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentAmber,
                                checkmarkColor = NoirBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = ev.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ev.shortDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            // Submit Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showConfirmationDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_accusation_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Present Accusation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Confirmation dialog before ending the case
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Accusation", color = TextPrimary) },
            text = {
                Text(
                    text = "Accusations are evaluated strictly against evidence and deductive logic. Are you ready to submit your theory?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.submitAccusation()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    )
                ) {
                    Text("Confirm Theory", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Review Evidence", color = TextMuted)
                }
            },
            containerColor = NoirSurface
        )
    }
}

@Composable
fun CaseResultsScreen(
    viewModel: CaseZeroViewModel
) {
    val result by viewModel.latestResult.collectAsState()
    var showFullExplanation by remember { mutableStateOf(false) }

    val data = result ?: return

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_case_results"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Case Results",
                onBack = { viewModel.navigateTo(Screen.Home) }
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
            item {
                Spacer(modifier = Modifier.height(10.dp))
                if (data.isCorrectCulprit) {
                    SolvedRubberStamp()
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CrimeSceneRed.copy(alpha = 0.2f),
                        border = BorderStroke(2.dp, CrimeSceneRed)
                    ) {
                        Text(
                            text = "WRONG ACCUSATION",
                            style = MaterialTheme.typography.titleLarge,
                            color = CrimeSceneRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = if (data.isCorrectCulprit) "You got it right!" else "The investigation missed the mark.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "The killer was ${data.canonicalCulpritName}.",
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentAmber
                )
            }

            // Circular Gauge Score
            item {
                CircularScoreGauge(
                    scorePercent = data.accuracyPercent,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    text = "Evidence Accuracy",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (data.accuracyPercent >= 80) "Excellent deduction!" else "Good effort, continue analyzing clues.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (data.accuracyPercent >= 80) SolvedGreen else AccentAmber
                )
            }

            // Stat Boxes: Clues Found, Mistakes, Time Taken
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Clues Found",
                        value = "${data.cluesFoundCount}/${data.totalCluesCount}",
                        modifier = Modifier.weight(1f),
                        highlight = true
                    )
                    StatCard(
                        title = "Mistakes",
                        value = "${data.mistakesCount}",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Time Taken",
                        value = "18m 32s",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Breakdown card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = NoirSurfaceElevated,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Deduction Breakdown",
                            style = MaterialTheme.typography.titleSmall,
                            color = AccentAmber,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Motive: ${data.canonicalMotive}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )

                        Text(
                            text = "Method: ${data.canonicalMethod}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )

                        Text(
                            text = "How you knew: ${data.howYouKnewSummary}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // CTAs: Next Case & View Full Solution
            item {
                Button(
                    onClick = { viewModel.navigateTo(Screen.CaseList) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("results_next_case_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = NoirBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Next Case",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showFullExplanation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("results_view_solution_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AccentBlue
                    ),
                    border = BorderStroke(1.dp, AccentBlueMuted),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "View Full Solution",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showFullExplanation) {
        Dialog(onDismissRequest = { showFullExplanation = false }) {
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
                        text = "The Full Canonical Solution",
                        style = MaterialTheme.typography.titleLarge,
                        color = AccentAmber,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = data.fullExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showFullExplanation = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        )
                    ) {
                        Text("Understood", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
