package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CaseCategory
import com.example.model.CaseModel
import com.example.model.CaseStatus
import com.example.ui.components.CaseZeroBottomNavBar
import com.example.ui.components.DifficultyStars
import com.example.ui.components.NoirTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CaseZeroViewModel
import com.example.viewmodel.Screen

@Composable
fun CaseListScreen(
    viewModel: CaseZeroViewModel
) {
    val cases by viewModel.cases.collectAsState()
    val filter by viewModel.caseFilter.collectAsState()
    val bottomNavTab by viewModel.bottomNavSelected.collectAsState()

    val filteredCases = cases.filter { caseItem ->
        when (filter) {
            CaseCategory.ALL -> true
            CaseCategory.DAILY -> caseItem.category == CaseCategory.DAILY
            CaseCategory.LONG_TERM -> caseItem.category == CaseCategory.LONG_TERM
            CaseCategory.COMPLETED -> caseItem.status == CaseStatus.SOLVED
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_case_list"),
        containerColor = NoirBackground,
        topBar = {
            NoirTopAppBar(title = "Cases")
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
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf(
                    CaseCategory.ALL to "All",
                    CaseCategory.DAILY to "Daily",
                    CaseCategory.LONG_TERM to "Long-term",
                    CaseCategory.COMPLETED to "Completed"
                )
                items(categories) { (cat, label) ->
                    val isSelected = filter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCaseFilter(cat) },
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
                        modifier = Modifier.testTag("filter_chip_${label.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredCases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No cases found in this category",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredCases) { caseItem ->
                        CaseLibraryCard(
                            caseModel = caseItem,
                            onClick = {
                                viewModel.selectCase(caseItem.id)
                                viewModel.navigateTo(Screen.CaseOverview)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CaseLibraryCard(
    caseModel: CaseModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("case_library_item_${caseModel.id}"),
        shape = RoundedCornerShape(16.dp),
        color = NoirSurfaceElevated,
        border = BorderStroke(
            1.dp,
            if (caseModel.status == CaseStatus.SOLVED) SolvedGreen.copy(alpha = 0.5f) else DividerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category / Status Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (caseModel.category) {
                            CaseCategory.DAILY -> AccentAmber.copy(alpha = 0.15f)
                            CaseCategory.LONG_TERM -> AccentBlue.copy(alpha = 0.15f)
                            else -> SolvedGreen.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (caseModel.category) {
                                CaseCategory.DAILY -> "Daily Case"
                                CaseCategory.LONG_TERM -> "Long-term"
                                else -> "Completed"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = when (caseModel.category) {
                                CaseCategory.DAILY -> AccentAmber
                                CaseCategory.LONG_TERM -> AccentBlue
                                else -> SolvedGreen
                            },
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (caseModel.status == CaseStatus.IN_PROGRESS) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AccentAmber
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = NoirBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                DifficultyStars(difficulty = caseModel.difficulty)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = caseModel.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = caseModel.synopsis,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        caseModel.status == CaseStatus.SOLVED -> "Solved on ${caseModel.solvedDate ?: "Oct 18, 2025"}"
                        caseModel.stagesText != null -> caseModel.stagesText
                        else -> "${caseModel.estimatedMinutes} mins est."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (caseModel.status == CaseStatus.SOLVED) SolvedGreen else TextMuted
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (caseModel.status == CaseStatus.SOLVED) "Review" else "Investigate",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
