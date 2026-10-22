package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun InvestigationBoardScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val connectingSourceId by viewModel.boardConnectingSourceId.collectAsState()
    val selectedNodeId by viewModel.selectedBoardNodeId.collectAsState()

    val caseModel = cases.firstOrNull { it.id == selectedCaseId } ?: cases.first()

    var isListView by remember { mutableStateOf(false) }
    var showConnectDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_investigation_board"),
        containerColor = CorkboardColor,
        topBar = {
            NoirTopAppBar(
                title = "Investigation Board",
                onBack = { viewModel.popBack() },
                actions = {
                    IconButton(
                        onClick = { isListView = !isListView },
                        modifier = Modifier.testTag("board_toggle_view_button")
                    ) {
                        Icon(
                            imageVector = if (isListView) Icons.Filled.Dashboard else Icons.Filled.List,
                            contentDescription = "Toggle view",
                            tint = AccentAmber
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Bottom Action Bar: + Add, Connect, Notes, Reset
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = NoirSurface,
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BoardBottomAction(
                        icon = Icons.Filled.Add,
                        label = "Add",
                        onClick = { viewModel.navigateTo(Screen.EvidenceGallery) }
                    )
                    BoardBottomAction(
                        icon = Icons.Filled.Timeline,
                        label = if (connectingSourceId != null) "Cancel" else "Connect",
                        isActive = connectingSourceId != null,
                        onClick = {
                            if (connectingSourceId != null) {
                                viewModel.cancelConnecting()
                            } else if (selectedNodeId != null) {
                                viewModel.startConnectingFrom(selectedNodeId!!)
                            }
                        }
                    )
                    BoardBottomAction(
                        icon = Icons.Filled.EditNote,
                        label = "Notes",
                        onClick = { showNotesDialog = true }
                    )
                    BoardBottomAction(
                        icon = Icons.Filled.Refresh,
                        label = "Reset",
                        onClick = { showResetConfirm = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (connectingSourceId != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = AccentAmber,
                    contentColor = NoirBackground
                ) {
                    Text(
                        text = "Tap a second node to connect red string",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            if (!isListView) {
                // Interactive Visual Pinboard Canvas
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val boardWidth = maxWidth
                    val boardHeight = maxHeight

                    // Canvas drawing the red connection strings
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw corkboard texture grid
                        for (x in 0 until w.toInt() step 60) {
                            drawLine(
                                color = Color(0xFF1B2936),
                                start = Offset(x.toFloat(), 0f),
                                end = Offset(x.toFloat(), h),
                                strokeWidth = 0.8f
                            )
                        }

                        // Draw each red string connection
                        caseModel.boardConnections.forEach { conn ->
                            val fromNode = caseModel.boardNodes.firstOrNull { it.id == conn.fromNodeId }
                            val toNode = caseModel.boardNodes.firstOrNull { it.id == conn.toNodeId }
                            if (fromNode != null && toNode != null) {
                                val p1 = Offset(fromNode.x * w, fromNode.y * h)
                                val p2 = Offset(toNode.x * w, toNode.y * h)

                                drawLine(
                                    color = Color(conn.relationType.colorHex),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 3.5f
                                )

                                // Center connection pin
                                val mid = Offset((p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                                drawCircle(
                                    color = Color(conn.relationType.colorHex),
                                    radius = 5f,
                                    center = mid
                                )
                            }
                        }
                    }

                    // Render pinned nodes
                    caseModel.boardNodes.forEach { node ->
                        val isSource = connectingSourceId == node.id
                        val isSelected = selectedNodeId == node.id

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (boardWidth * node.x) - 44.dp,
                                    y = (boardHeight * node.y) - 44.dp
                                )
                                .size(88.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NoirSurfaceElevated)
                                .border(
                                    width = if (isSource) 2.5.dp else if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSource) AccentAmber else if (isSelected) AccentBlue else DividerColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (connectingSourceId != null) {
                                        if (connectingSourceId != node.id) {
                                            showConnectDialog = Pair(connectingSourceId!!, node.id)
                                        }
                                    } else {
                                        viewModel.selectBoardNode(node.id)
                                    }
                                }
                                .padding(6.dp)
                                .testTag("board_node_${node.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Red pushpin icon
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (node.type == NodeType.VICTIM) CrimeSceneRed else AccentAmber)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = node.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = node.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            } else {
                // List-based accessible alternative view (PRD requirement)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Established Pinboard Connections (${caseModel.boardConnections.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(caseModel.boardConnections) { conn ->
                        val fromNode = caseModel.boardNodes.firstOrNull { it.id == conn.fromNodeId }
                        val toNode = caseModel.boardNodes.firstOrNull { it.id == conn.toNodeId }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(1.dp, Color(conn.relationType.colorHex).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${fromNode?.title ?: conn.fromNodeId} ⟷ ${toNode?.title ?: conn.toNodeId}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Relation: ${conn.relationType.label}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(conn.relationType.colorHex)
                                    )
                                }

                                if (conn.isUserCreated) {
                                    IconButton(
                                        onClick = { viewModel.repository.removeBoardConnection(selectedCaseId, conn.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Remove",
                                            tint = CrimeSceneRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Connect Relationship Selection Dialog
    if (showConnectDialog != null) {
        val (fromId, toId) = showConnectDialog!!
        val fromNode = caseModel.boardNodes.firstOrNull { it.id == fromId }
        val toNode = caseModel.boardNodes.firstOrNull { it.id == toId }

        Dialog(onDismissRequest = {
            showConnectDialog = null
            viewModel.cancelConnecting()
        }) {
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
                        text = "Connect Clues",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Link: ${fromNode?.title} with ${toNode?.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentAmber
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Select deduction relation:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    RelationType.values().forEach { relation ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.connectToNode(toId, relation)
                                    showConnectDialog = null
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = NoirSurfaceElevated,
                            border = BorderStroke(1.dp, Color(relation.colorHex).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = relation.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(relation.colorHex),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Notes Dialog
    if (showNotesDialog) {
        Dialog(onDismissRequest = { showNotesDialog = false }) {
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
                        text = "Investigation Theory",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Key Working Deductions:\n" +
                                "1. Elena received an encrypted call from neighbor Daniel's router at 10:32 PM.\n" +
                                "2. Daniel was filmed entering the service alleyway at 10:45 PM in his reflective jacket.\n" +
                                "3. The nylon monofilament thread and magnet found in Daniel's trash chute explain how the deadbolt was engaged from outside.\n" +
                                "4. Cashmere scarf was used to strangle Elena after she demanded $15,000 back.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showNotesDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber,
                            contentColor = NoirBackground
                        )
                    ) {
                        Text("Close Theory", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Board?", color = TextPrimary) },
            text = { Text("This will clear custom pinboard strings. Clues and evidence will not be lost.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetBoard()
                        showResetConfirm = false
                    }
                ) {
                    Text("Reset", color = CrimeSceneRed)
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
private fun BoardBottomAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) AccentAmber else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) AccentAmber else TextMuted
        )
    }
}
