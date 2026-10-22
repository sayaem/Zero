package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CaseRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CaseList : Screen("cases")
    object CaseOverview : Screen("case_overview")
    object CaseTimeline : Screen("case_timeline")
    object EvidenceGallery : Screen("evidence_gallery")
    object InvestigationBoard : Screen("investigation_board")
    object Interrogation : Screen("interrogation")
    object FinalAccusation : Screen("final_accusation")
    object CaseResults : Screen("case_results")
    object DetectiveRank : Screen("detective_rank")
    object DailyChallenge : Screen("daily_challenge")
    object Settings : Screen("settings")
}

class CaseZeroViewModel(application: Application) : AndroidViewModel(application) {

    val repository = CaseRepository(application.applicationContext)

    val cases = repository.cases
    val profile = repository.profile
    val achievements = repository.achievements
    val dailyChallenge = repository.dailyChallenge
    val isOnboardingCompleted = repository.isOnboardingCompleted
    val isDarkTheme = repository.isDarkTheme
    val language = repository.language
    val dailyReminderEnabled = repository.dailyReminderEnabled
    val weeklyChallengeEnabled = repository.weeklyChallengeEnabled

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    private val _selectedCaseId = MutableStateFlow("case_01_silent_apartment")
    val selectedCaseId: StateFlow<String> = _selectedCaseId.asStateFlow()

    private val _selectedSuspectId = MutableStateFlow("suspect_daniel")
    val selectedSuspectId: StateFlow<String> = _selectedSuspectId.asStateFlow()

    private val _inspectedEvidence = MutableStateFlow<EvidenceItem?>(null)
    val inspectedEvidence: StateFlow<EvidenceItem?> = _inspectedEvidence.asStateFlow()

    // Accusation flow state
    private val _accuseCulpritId = MutableStateFlow<String?>(null)
    val accuseCulpritId: StateFlow<String?> = _accuseCulpritId.asStateFlow()

    private val _accuseMotive = MutableStateFlow("")
    val accuseMotive: StateFlow<String> = _accuseMotive.asStateFlow()

    private val _accuseMethod = MutableStateFlow("")
    val accuseMethod: StateFlow<String> = _accuseMethod.asStateFlow()

    private val _accuseEvidenceIds = MutableStateFlow<Set<String>>(emptySet())
    val accuseEvidenceIds: StateFlow<Set<String>> = _accuseEvidenceIds.asStateFlow()

    private val _latestResult = MutableStateFlow<CaseRepository.AccusationResult?>(null)
    val latestResult: StateFlow<CaseRepository.AccusationResult?> = _latestResult.asStateFlow()

    // Pinboard interactive states
    private val _selectedBoardNodeId = MutableStateFlow<String?>(null)
    val selectedBoardNodeId: StateFlow<String?> = _selectedBoardNodeId.asStateFlow()

    private val _boardConnectingSourceId = MutableStateFlow<String?>(null)
    val boardConnectingSourceId: StateFlow<String?> = _boardConnectingSourceId.asStateFlow()

    // Filters
    private val _caseFilter = MutableStateFlow(CaseCategory.ALL)
    val caseFilter: StateFlow<CaseCategory> = _caseFilter.asStateFlow()

    private val _evidenceFilter = MutableStateFlow(EvidenceType.ALL)
    val evidenceFilter: StateFlow<EvidenceType> = _evidenceFilter.asStateFlow()

    // Bottom Navigation Bar Selected Item
    private val _bottomNavSelected = MutableStateFlow("home")
    val bottomNavSelected: StateFlow<String> = _bottomNavSelected.asStateFlow()

    // Feedback message (for contradiction/challenge notifications)
    private val _userFeedback = MutableStateFlow<String?>(null)
    val userFeedback: StateFlow<String?> = _userFeedback.asStateFlow()

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
            updateBottomNavTab(screen)
        }
    }

    fun popBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val previous = screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = previous
            updateBottomNavTab(previous)
            return true
        } else {
            if (_currentScreen.value != Screen.Home && isOnboardingCompleted.value) {
                _currentScreen.value = Screen.Home
                _bottomNavSelected.value = "home"
                return true
            }
            return false
        }
    }

    private fun updateBottomNavTab(screen: Screen) {
        when (screen) {
            Screen.Home -> _bottomNavSelected.value = "home"
            Screen.CaseList -> _bottomNavSelected.value = "cases"
            Screen.EvidenceGallery -> _bottomNavSelected.value = "evidence"
            Screen.DetectiveRank -> _bottomNavSelected.value = "rank"
            Screen.Settings -> _bottomNavSelected.value = "profile"
            else -> {}
        }
    }

    fun onBottomNavSelected(route: String) {
        _bottomNavSelected.value = route
        when (route) {
            "home" -> navigateTo(Screen.Home)
            "cases" -> navigateTo(Screen.CaseList)
            "evidence" -> navigateTo(Screen.EvidenceGallery)
            "rank" -> navigateTo(Screen.DetectiveRank)
            "profile" -> navigateTo(Screen.Settings)
        }
    }

    fun selectCase(caseId: String) {
        _selectedCaseId.value = caseId
        repository.setActiveCase(caseId)
        // Reset accusation state for the new case
        val targetCase = cases.value.firstOrNull { it.id == caseId }
        _accuseCulpritId.value = targetCase?.suspects?.firstOrNull()?.id
        _accuseMotive.value = ""
        _accuseMethod.value = ""
        _accuseEvidenceIds.value = emptySet()
    }

    fun selectSuspectForInterrogation(suspectId: String) {
        _selectedSuspectId.value = suspectId
        navigateTo(Screen.Interrogation)
    }

    fun inspectEvidence(evidence: EvidenceItem?) {
        _inspectedEvidence.value = evidence
        if (evidence != null) {
            repository.markEvidenceExamined(_selectedCaseId.value, evidence.id)
        }
    }

    fun updateEvidenceNote(evidenceId: String, notes: String) {
        repository.updateEvidenceNote(_selectedCaseId.value, evidenceId, notes)
        // update local state
        _inspectedEvidence.value = _inspectedEvidence.value?.copy(playerNotes = notes, isExamined = true)
    }

    fun challengeSuspectWithEvidence(suspectId: String, evidenceId: String): Boolean {
        val success = repository.challengeSuspect(_selectedCaseId.value, suspectId, evidenceId)
        if (success) {
            _userFeedback.value = "Contradiction established! The suspect broke their alibi."
        } else {
            _userFeedback.value = "The suspect brushed aside this evidence. Find a decisive contradiction."
        }
        return success
    }

    fun clearFeedback() {
        _userFeedback.value = null
    }

    // Accusation setup
    fun setAccuseCulprit(suspectId: String) {
        _accuseCulpritId.value = suspectId
    }

    fun setAccuseMotive(motive: String) {
        _accuseMotive.value = motive
    }

    fun setAccuseMethod(method: String) {
        _accuseMethod.value = method
    }

    fun toggleAccuseEvidence(evidenceId: String) {
        val current = _accuseEvidenceIds.value.toMutableSet()
        if (current.contains(evidenceId)) {
            current.remove(evidenceId)
        } else {
            current.add(evidenceId)
        }
        _accuseEvidenceIds.value = current
    }

    fun submitAccusation() {
        val culpritId = _accuseCulpritId.value ?: return
        val motive = _accuseMotive.value.ifBlank { "Financial gain / debt evasion ($15,000)" }
        val method = _accuseMethod.value.ifBlank { "Strangulation (using scarf) + thread & magnet deadbolt trick" }
        val evidenceList = _accuseEvidenceIds.value.toList()

        val result = repository.submitAccusation(
            caseId = _selectedCaseId.value,
            selectedCulpritId = culpritId,
            selectedMotive = motive,
            selectedMethod = method,
            selectedEvidenceIds = evidenceList
        )
        _latestResult.value = result
        navigateTo(Screen.CaseResults)
    }

    // Pinboard
    fun selectBoardNode(nodeId: String?) {
        _selectedBoardNodeId.value = nodeId
    }

    fun startConnectingFrom(nodeId: String) {
        _boardConnectingSourceId.value = nodeId
    }

    fun connectToNode(targetNodeId: String, relationType: RelationType) {
        val sourceId = _boardConnectingSourceId.value ?: return
        if (sourceId != targetNodeId) {
            val connectionId = "conn_${System.currentTimeMillis()}"
            repository.addBoardConnection(
                caseId = _selectedCaseId.value,
                connection = BoardConnection(
                    id = connectionId,
                    fromNodeId = sourceId,
                    toNodeId = targetNodeId,
                    relationType = relationType,
                    isUserCreated = true
                )
            )
        }
        _boardConnectingSourceId.value = null
    }

    fun cancelConnecting() {
        _boardConnectingSourceId.value = null
    }

    fun resetBoard() {
        repository.resetBoardConnections(_selectedCaseId.value)
    }

    fun setCaseFilter(category: CaseCategory) {
        _caseFilter.value = category
    }

    fun setEvidenceFilter(type: EvidenceType) {
        _evidenceFilter.value = type
    }

    fun completeDailyChallenge(optionIndex: Int): Boolean {
        return repository.completeDailyChallenge(optionIndex)
    }

    fun resetAllProgress() {
        repository.resetAllProgress()
        _currentScreen.value = Screen.Splash
        screenStack.clear()
    }
}
