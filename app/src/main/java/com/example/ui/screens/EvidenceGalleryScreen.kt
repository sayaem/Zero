package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EvidenceItem
import com.example.model.EvidenceType
import com.example.ui.components.CaseZeroBottomNavBar
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun EvidenceGalleryScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val filter by viewModel.evidenceFilter.collectAsState()
    val inspectedItem by viewModel.inspectedEvidence.collectAsState()
    val bottomNavTab by viewModel.bottomNavSelected.collectAsState()

    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()

    val filteredEvidence = caseModel.evidence.filter { item ->
        when (filter) {
            EvidenceType.ALL -> true
            EvidenceType.PHOTOS -> item.type == EvidenceType.PHOTOS
            EvidenceType.DOCUMENTS -> item.type == EvidenceType.DOCUMENTS
            EvidenceType.FORENSICS -> item.type == EvidenceType.FORENSICS || item.type == EvidenceType.PHYSICAL
            EvidenceType.PHYSICAL -> item.type == EvidenceType.PHYSICAL
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_evidence_gallery"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(
                title = "Evidence",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    EvidenceType.ALL to "All",
                    EvidenceType.PHOTOS to "Photos",
                    EvidenceType.DOCUMENTS to "Documents",
                    EvidenceType.FORENSICS to "Forensics"
                )
                items(types) { (type, label) ->
                    val isSelected = filter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setEvidenceFilter(type) },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentAmber,
                            selectedLabelColor = NoirBackground,
                            containerColor = NoirSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (isSelected) AccentAmber else DividerColor),
                        modifier = Modifier.testTag("filter_evidence_${label.lowercase()}")
                    )
                }
            }

            // Evidence 2-column Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredEvidence) { evidence ->
                    EvidenceGridCard(
                        evidence = evidence,
                        onClick = { viewModel.inspectEvidence(evidence) }
                    )
                }
            }
        }
    }

    // Detail Inspection Modal
    if (inspectedItem != null) {
        EvidenceDetailDialog(
            evidence = inspectedItem!!,
            onDismiss = { viewModel.inspectEvidence(null) },
            onSaveNotes = { notes ->
                viewModel.updateEvidenceNote(inspectedItem!!.id, notes)
            },
            onOpenBoard = {
                viewModel.inspectEvidence(null)
                viewModel.navigateTo(Screen.InvestigationBoard)
            }
        )
    }
}

@Composable
private fun EvidenceGridCard(
    evidence: EvidenceItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("evidence_card_${evidence.id}"),
        shape = RoundedCornerShape(16.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(
            1.dp,
            if (evidence.isExamined) AccentAmber.copy(alpha = 0.5f) else DividerColor
        )
    ) {
        Column {
            // Simulated Thumbnail Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(NoirSurfaceInteractive),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (evidence.type) {
                        EvidenceType.PHOTOS -> Icons.Filled.PhotoCamera
                        EvidenceType.DOCUMENTS -> Icons.Filled.Description
                        EvidenceType.FORENSICS -> Icons.Filled.Fingerprint
                        EvidenceType.PHYSICAL -> Icons.Filled.Category
                        else -> Icons.Filled.Search
                    },
                    contentDescription = null,
                    tint = AccentAmber,
                    modifier = Modifier.size(36.dp)
                )

                // Top right status indicator
                if (evidence.isExamined) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(SolvedGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Examined",
                            tint = NoirBackground,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = evidence.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = evidence.shortDescription,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EvidenceDetailDialog(
    evidence: EvidenceItem,
    onDismiss: () -> Unit,
    onSaveNotes: (String) -> Unit,
    onOpenBoard: () -> Unit
) {
    var notesText by remember { mutableStateOf(evidence.playerNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag("dialog_evidence_detail"),
            shape = RoundedCornerShape(20.dp),
            color = NoirSurface,
            border = BorderStroke(1.dp, AccentAmberMuted)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentAmber.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = evidence.type.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = evidence.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Timestamp: ${evidence.timestamp} • Source: ${evidence.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = evidence.fullDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Player Notes Section
                Text(
                    text = "Detective Notes",
                    style = MaterialTheme.typography.titleSmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("evidence_notes_input"),
                    placeholder = {
                        Text(
                            text = "Write your deduction thoughts on this clue...",
                            color = TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentAmber,
                        unfocusedBorderColor = DividerColor,
                        focusedContainerColor = NoirSurfaceElevated,
                        unfocusedContainerColor = NoirSurfaceElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenBoard,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentBlue
                        ),
                        border = BorderStroke(1.dp, AccentBlueMuted),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("View On Board")
                    }

                    Button(
                        onClick = {
                            onSaveNotes(notesText)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("evidence_save_note_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Note", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
