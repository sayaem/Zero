package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CaseRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("case_zero_player_data", Context.MODE_PRIVATE)

    private val _cases = MutableStateFlow<List<CaseModel>>(emptyList())
    val cases: StateFlow<List<CaseModel>> = _cases.asStateFlow()

    private val _profile = MutableStateFlow(DetectiveProfile())
    val profile: StateFlow<DetectiveProfile> = _profile.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _dailyChallenge = MutableStateFlow(createDefaultDailyChallenge())
    val dailyChallenge: StateFlow<DailyChallenge> = _dailyChallenge.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _dailyReminderEnabled = MutableStateFlow(true)
    val dailyReminderEnabled: StateFlow<Boolean> = _dailyReminderEnabled.asStateFlow()

    private val _weeklyChallengeEnabled = MutableStateFlow(true)
    val weeklyChallengeEnabled: StateFlow<Boolean> = _weeklyChallengeEnabled.asStateFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        _isOnboardingCompleted.value = prefs.getBoolean("onboarding_completed", false)
        _isDarkTheme.value = prefs.getBoolean("dark_theme", true)
        _language.value = prefs.getString("language", "English") ?: "English"
        _dailyReminderEnabled.value = prefs.getBoolean("daily_reminder", true)
        _weeklyChallengeEnabled.value = prefs.getBoolean("weekly_challenge", true)

        val xp = prefs.getInt("profile_xp", 120)
        val solvedCount = prefs.getInt("profile_solved", 1)
        val winRate = prefs.getInt("profile_win_rate", 83)
        val streak = prefs.getInt("profile_streak", 4)
        val activeCaseId = prefs.getString("active_case_id", "case_01_silent_apartment") ?: "case_01_silent_apartment"

        val rank = when {
            xp >= DetectiveRank.CHIEF.minXp -> DetectiveRank.CHIEF
            xp >= DetectiveRank.SENIOR.minXp -> DetectiveRank.SENIOR
            xp >= DetectiveRank.FIELD.minXp -> DetectiveRank.FIELD
            xp >= DetectiveRank.JUNIOR.minXp -> DetectiveRank.JUNIOR
            else -> DetectiveRank.ROOKIE
        }

        _profile.value = DetectiveProfile(
            rank = rank,
            currentXp = xp,
            casesSolved = solvedCount,
            winRate = winRate,
            currentStreak = streak,
            activeCaseId = activeCaseId
        )

        _achievements.value = loadAchievements()
        _cases.value = loadAuthoredCases()
        _dailyChallenge.value = loadDailyChallenge()
    }

    private fun loadAchievements(): List<Achievement> {
        val defAchievements = listOf(
            Achievement("ach_first_case", "First Case Solved", "Completed your first case", true, "Oct 16, 2025"),
            Achievement("ach_sharp_eye", "Sharp Eye", "Found and examined all evidence in a case", true, "Oct 18, 2025"),
            Achievement("ach_perfect_deduction", "Perfect Deduction", "100% accuracy in a case accusation", false),
            Achievement("ach_master_connector", "Master Connector", "Discovered 5 correct connections on the pinboard", false),
            Achievement("ach_streak_3", "Iron Routine", "Kept an active streak for 3 consecutive days", true, "Oct 20, 2025")
        )
        return defAchievements.map { ach ->
            val unlocked = prefs.getBoolean("ach_${ach.id}", ach.isUnlocked)
            ach.copy(isUnlocked = unlocked)
        }
    }

    private fun loadDailyChallenge(): DailyChallenge {
        val def = createDefaultDailyChallenge()
        val isDone = prefs.getBoolean("daily_challenge_done", def.isCompleted)
        val selected = if (prefs.contains("daily_challenge_selected")) prefs.getInt("daily_challenge_selected", -1) else null
        return def.copy(isCompleted = isDone, selectedOption = selected)
    }

    fun completeDailyChallenge(selectedIndex: Int): Boolean {
        val current = _dailyChallenge.value
        val isCorrect = selectedIndex == current.correctIndex
        _dailyChallenge.value = current.copy(
            isCompleted = true,
            selectedOption = selectedIndex
        )
        prefs.edit()
            .putBoolean("daily_challenge_done", true)
            .putInt("daily_challenge_selected", selectedIndex)
            .apply()

        if (isCorrect) {
            addXp(50)
        }
        return isCorrect
    }

    fun setOnboardingCompleted(completed: Boolean) {
        _isOnboardingCompleted.value = completed
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        prefs.edit().putBoolean("dark_theme", isDark).apply()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        prefs.edit().putString("language", lang).apply()
    }

    fun setDailyReminder(enabled: Boolean) {
        _dailyReminderEnabled.value = enabled
        prefs.edit().putBoolean("daily_reminder", enabled).apply()
    }

    fun setWeeklyChallenge(enabled: Boolean) {
        _weeklyChallengeEnabled.value = enabled
        prefs.edit().putBoolean("weekly_challenge", enabled).apply()
    }

    fun setActiveCase(caseId: String) {
        _profile.value = _profile.value.copy(activeCaseId = caseId)
        prefs.edit().putString("active_case_id", caseId).apply()
    }

    fun addXp(amount: Int) {
        val newXp = _profile.value.currentXp + amount
        val rank = when {
            newXp >= DetectiveRank.CHIEF.minXp -> DetectiveRank.CHIEF
            newXp >= DetectiveRank.SENIOR.minXp -> DetectiveRank.SENIOR
            newXp >= DetectiveRank.FIELD.minXp -> DetectiveRank.FIELD
            newXp >= DetectiveRank.JUNIOR.minXp -> DetectiveRank.JUNIOR
            else -> DetectiveRank.ROOKIE
        }
        _profile.value = _profile.value.copy(currentXp = newXp, rank = rank)
        prefs.edit().putInt("profile_xp", newXp).apply()
    }

    fun markEvidenceExamined(caseId: String, evidenceId: String) {
        val updatedCases = _cases.value.map { c ->
            if (c.id == caseId) {
                val updatedEvidence = c.evidence.map { ev ->
                    if (ev.id == evidenceId) ev.copy(isExamined = true) else ev
                }
                c.copy(evidence = updatedEvidence)
            } else c
        }
        _cases.value = updatedCases
        saveCaseEvidenceState(caseId)
    }

    fun updateEvidenceNote(caseId: String, evidenceId: String, notes: String) {
        val updatedCases = _cases.value.map { c ->
            if (c.id == caseId) {
                val updatedEvidence = c.evidence.map { ev ->
                    if (ev.id == evidenceId) ev.copy(playerNotes = notes, isExamined = true) else ev
                }
                c.copy(evidence = updatedEvidence)
            } else c
        }
        _cases.value = updatedCases
        prefs.edit().putString("notes_${caseId}_${evidenceId}", notes).apply()
    }

    fun challengeSuspect(caseId: String, suspectId: String, evidenceId: String): Boolean {
        val targetCase = _cases.value.firstOrNull { it.id == caseId } ?: return false
        val suspect = targetCase.suspects.firstOrNull { it.id == suspectId } ?: return false
        val isContradiction = suspect.contradictingEvidenceId == evidenceId

        if (isContradiction) {
            val updatedCases = _cases.value.map { c ->
                if (c.id == caseId) {
                    val updatedSuspects = c.suspects.map { s ->
                        if (s.id == suspectId) s.copy(isBroken = true) else s
                    }
                    c.copy(suspects = updatedSuspects)
                } else c
            }
            _cases.value = updatedCases
            prefs.edit().putBoolean("suspect_broken_${caseId}_${suspectId}", true).apply()
            addXp(30)
        }
        return isContradiction
    }

    fun addBoardConnection(caseId: String, connection: BoardConnection): Boolean {
        val targetCase = _cases.value.firstOrNull { it.id == caseId } ?: return false
        val exists = targetCase.boardConnections.any {
            (it.fromNodeId == connection.fromNodeId && it.toNodeId == connection.toNodeId) ||
            (it.fromNodeId == connection.toNodeId && it.toNodeId == connection.fromNodeId)
        }
        if (exists) return false

        // Check if matching deduction rule exists
        val isVerified = targetCase.deductions.any {
            (it.fromId == connection.fromNodeId && it.toId == connection.toNodeId) ||
            (it.fromId == connection.toNodeId && it.toId == connection.fromNodeId)
        }

        val updatedConn = connection.copy(isVerified = isVerified)
        val updatedCases = _cases.value.map { c ->
            if (c.id == caseId) {
                c.copy(boardConnections = c.boardConnections + updatedConn)
            } else c
        }
        _cases.value = updatedCases
        saveBoardConnections(caseId, updatedCases.first { it.id == caseId }.boardConnections)
        if (isVerified) {
            addXp(20)
        }
        return true
    }

    fun removeBoardConnection(caseId: String, connectionId: String) {
        val updatedCases = _cases.value.map { c ->
            if (c.id == caseId) {
                c.copy(boardConnections = c.boardConnections.filterNot { it.id == connectionId })
            } else c
        }
        _cases.value = updatedCases
        saveBoardConnections(caseId, updatedCases.first { it.id == caseId }.boardConnections)
    }

    fun resetBoardConnections(caseId: String) {
        val updatedCases = _cases.value.map { c ->
            if (c.id == caseId) {
                // Keep default seed connections if any, remove user-created ones
                c.copy(boardConnections = c.boardConnections.filterNot { it.isUserCreated })
            } else c
        }
        _cases.value = updatedCases
        saveBoardConnections(caseId, updatedCases.first { it.id == caseId }.boardConnections)
    }

    data class AccusationResult(
        val isCorrectCulprit: Boolean,
        val isCorrectMotive: Boolean,
        val isCorrectMethod: Boolean,
        val decisiveCount: Int,
        val totalDecisiveNeeded: Int,
        val accuracyPercent: Int,
        val mistakesCount: Int,
        val cluesFoundCount: Int,
        val totalCluesCount: Int,
        val canonicalCulpritName: String,
        val canonicalMotive: String,
        val canonicalMethod: String,
        val howYouKnewSummary: String,
        val fullExplanation: String
    )

    fun submitAccusation(
        caseId: String,
        selectedCulpritId: String,
        selectedMotive: String,
        selectedMethod: String,
        selectedEvidenceIds: List<String>
    ): AccusationResult {
        val targetCase = _cases.value.firstOrNull { it.id == caseId }
            ?: throw IllegalArgumentException("Case not found")

        val isCulprit = selectedCulpritId == targetCase.canonicalCulpritId
        val isMotive = selectedMotive.trim().equals(targetCase.canonicalMotive.trim(), ignoreCase = true)
        val isMethod = selectedMethod.trim().equals(targetCase.canonicalMethod.trim(), ignoreCase = true)

        val matchedDecisive = selectedEvidenceIds.intersect(targetCase.decisiveEvidenceIds.toSet()).size
        val totalDecisiveNeeded = targetCase.decisiveEvidenceIds.size

        var mistakes = 0
        if (!isCulprit) mistakes += 2
        if (!isMotive) mistakes += 1
        if (!isMethod) mistakes += 1
        val irrelevantEvidenceCount = selectedEvidenceIds.size - matchedDecisive
        if (irrelevantEvidenceCount > 0) mistakes += irrelevantEvidenceCount

        // Score Calculation according to PRD rubric:
        // Culprit 35%, Decisive evidence 25%, Motive 10%, Method 10%, Timeline/board reasoning 20%
        var score = 0
        if (isCulprit) score += 40
        if (isMotive) score += 15
        if (isMethod) score += 15
        val evidenceFraction = if (totalDecisiveNeeded > 0) matchedDecisive.toFloat() / totalDecisiveNeeded else 1f
        score += (evidenceFraction * 30).toInt()

        val accuracyPercent = score.coerceIn(0, 100)
        val cluesFound = targetCase.evidence.count { it.isExamined }

        if (isCulprit) {
            val updatedCases = _cases.value.map { c ->
                if (c.id == caseId) {
                    c.copy(
                        status = CaseStatus.SOLVED,
                        solvedDate = "Today",
                        score = score,
                        accuracy = accuracyPercent
                    )
                } else c
            }
            _cases.value = updatedCases

            // Save solved status in prefs
            prefs.edit()
                .putString("case_status_${caseId}", CaseStatus.SOLVED.name)
                .putInt("case_score_${caseId}", score)
                .putInt("case_accuracy_${caseId}", accuracyPercent)
                .apply()

            val newSolvedCount = _profile.value.casesSolved + 1
            val newXp = _profile.value.currentXp + (score * 2)
            _profile.value = _profile.value.copy(
                casesSolved = newSolvedCount,
                currentXp = newXp
            )
            prefs.edit()
                .putInt("profile_solved", newSolvedCount)
                .putInt("profile_xp", newXp)
                .apply()

            // Unlock achievements
            unlockAchievement("ach_first_case")
            if (cluesFound >= targetCase.evidence.size) {
                unlockAchievement("ach_sharp_eye")
            }
            if (accuracyPercent >= 95) {
                unlockAchievement("ach_perfect_deduction")
            }
        }

        return AccusationResult(
            isCorrectCulprit = isCulprit,
            isCorrectMotive = isMotive,
            isCorrectMethod = isMethod,
            decisiveCount = matchedDecisive,
            totalDecisiveNeeded = totalDecisiveNeeded,
            accuracyPercent = accuracyPercent,
            mistakesCount = mistakes,
            cluesFoundCount = cluesFound,
            totalCluesCount = targetCase.evidence.size,
            canonicalCulpritName = targetCase.canonicalCulpritName,
            canonicalMotive = targetCase.canonicalMotive,
            canonicalMethod = targetCase.canonicalMethod,
            howYouKnewSummary = targetCase.howYouKnewSummary,
            fullExplanation = targetCase.fullSolutionExplanation
        )
    }

    private fun unlockAchievement(id: String) {
        val updated = _achievements.value.map { ach ->
            if (ach.id == id && !ach.isUnlocked) {
                prefs.edit().putBoolean("ach_$id", true).apply()
                ach.copy(isUnlocked = true, unlockedDate = "Today")
            } else ach
        }
        _achievements.value = updated
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        loadSavedData()
    }

    private fun saveCaseEvidenceState(caseId: String) {
        val c = _cases.value.firstOrNull { it.id == caseId } ?: return
        val array = JSONArray()
        c.evidence.forEach { ev ->
            if (ev.isExamined) {
                array.put(ev.id)
            }
        }
        prefs.edit().putString("examined_evidence_${caseId}", array.toString()).apply()
    }

    private fun saveBoardConnections(caseId: String, connections: List<BoardConnection>) {
        val array = JSONArray()
        connections.forEach { conn ->
            val obj = JSONObject()
            obj.put("id", conn.id)
            obj.put("from", conn.fromNodeId)
            obj.put("to", conn.toNodeId)
            obj.put("type", conn.relationType.name)
            obj.put("userCreated", conn.isUserCreated)
            obj.put("verified", conn.isVerified)
            array.put(obj)
        }
        prefs.edit().putString("connections_${caseId}", array.toString()).apply()
    }

    private fun loadAuthoredCases(): List<CaseModel> {
        return listOf(
            createSilentApartmentCase(),
            createRiversideMurderCase(),
            createMuseumHeistCase(),
            createPoisonedGalaCase()
        ).map { initialCase ->
            // Reconcile with saved preferences
            val savedStatusStr = prefs.getString("case_status_${initialCase.id}", null)
            val status = if (savedStatusStr != null) {
                try { CaseStatus.valueOf(savedStatusStr) } catch (_: Exception) { initialCase.status }
            } else initialCase.status

            val savedScore = prefs.getInt("case_score_${initialCase.id}", initialCase.score ?: 0)
            val savedAccuracy = prefs.getInt("case_accuracy_${initialCase.id}", initialCase.accuracy ?: 0)

            val examinedJsonStr = prefs.getString("examined_evidence_${initialCase.id}", null)
            val examinedIds = mutableSetOf<String>()
            if (examinedJsonStr != null) {
                try {
                    val arr = JSONArray(examinedJsonStr)
                    for (i in 0 until arr.length()) examinedIds.add(arr.getString(i))
                } catch (_: Exception) {}
            }

            val updatedEvidence = initialCase.evidence.map { ev ->
                val isExamined = examinedIds.contains(ev.id) || ev.isExamined
                val note = prefs.getString("notes_${initialCase.id}_${ev.id}", ev.playerNotes) ?: ev.playerNotes
                ev.copy(isExamined = isExamined, playerNotes = note)
            }

            val updatedSuspects = initialCase.suspects.map { s ->
                val isBroken = prefs.getBoolean("suspect_broken_${initialCase.id}_${s.id}", s.isBroken)
                s.copy(isBroken = isBroken)
            }

            // Restore board connections
            val connJsonStr = prefs.getString("connections_${initialCase.id}", null)
            val connections = if (connJsonStr != null) {
                try {
                    val arr = JSONArray(connJsonStr)
                    val list = mutableListOf<BoardConnection>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list.add(
                            BoardConnection(
                                id = obj.getString("id"),
                                fromNodeId = obj.getString("from"),
                                toNodeId = obj.getString("to"),
                                relationType = RelationType.valueOf(obj.getString("type")),
                                isUserCreated = obj.optBoolean("userCreated", true),
                                isVerified = obj.optBoolean("verified", false)
                            )
                        )
                    }
                    list
                } catch (_: Exception) {
                    initialCase.boardConnections
                }
            } else {
                initialCase.boardConnections
            }

            initialCase.copy(
                status = status,
                score = if (status == CaseStatus.SOLVED) savedScore else initialCase.score,
                accuracy = if (status == CaseStatus.SOLVED) savedAccuracy else initialCase.accuracy,
                evidence = updatedEvidence,
                suspects = updatedSuspects,
                boardConnections = connections
            )
        }
    }

    private fun createSilentApartmentCase(): CaseModel {
        val victim = VictimProfile(
            name = "Elena Marsh",
            age = 28,
            occupation = "Graphic Designer",
            relationship = "Target of crypto debt dispute",
            summary = "Found dead on living room sofa with door bolted from inside. Quiet designer who threatened legal action over unpaid debts.",
            portraitInitials = "EM"
        )

        val timeline = listOf(
            TimelineItem(
                id = "tl_01",
                time = "10:15 PM",
                title = "Victim seen alive",
                source = "Neighbor's statement / Delivery Driver",
                status = TimelineStatus.CONFIRMED,
                details = "Courier handed hot noodle order directly to Elena at her apartment doorway."
            ),
            TimelineItem(
                id = "tl_02",
                time = "10:32 PM",
                title = "Possible argument",
                source = "Cell tower & Wi-Fi router log",
                status = TimelineStatus.REPORTED,
                details = "Encrypted VOIP call initiated through apartment building router. Audio shouting overheard by hallway sensor."
            ),
            TimelineItem(
                id = "tl_03",
                time = "10:45 PM",
                title = "Last known sighting",
                source = "Tea shop CCTV",
                status = TimelineStatus.CONFIRMED,
                details = "CCTV camera across the lane records an individual in a yellow reflective hooded jacket slipping into the building's rear alleyway."
            ),
            TimelineItem(
                id = "tl_04",
                time = "11:00 PM",
                title = "Estimated time of death",
                source = "Coroner's preliminary inspection",
                status = TimelineStatus.CONFIRMED,
                details = "Coroner estimates asphyxiation occurred between 10:45 PM and 11:05 PM. Minimal signs of physical struggle."
            ),
            TimelineItem(
                id = "tl_05",
                time = "11:20 PM",
                title = "Body discovered",
                source = "Neighbor called police",
                status = TimelineStatus.CONFIRMED,
                details = "Police breach unit arrived following report of continuous alarm. Deadbolt had to be cut through."
            )
        )

        val suspects = listOf(
            Suspect(
                id = "suspect_daniel",
                name = "Daniel Harper",
                roleAndAge = "Neighbor • 32",
                occupation = "Software Engineer",
                relationshipToVictim = "Neighbor (4 years)",
                potentialMotive = "Financial issues / $15,000 escrow dispute",
                alibi = "Claims he was at home playing video games the entire evening without leaving.",
                statement = "I was at home the whole night. I didn't leave my apartment.",
                contradictingEvidenceId = "ev_cctv_alley",
                confessionOrClue = "All right! You saw the jacket on CCTV. I went to her apartment at 10:40 PM to settle the crypto debt, but she was mocking me. I lost my temper... I used her scarf, then locked the deadbolt from the outside using the nylon thread and magnet through the door gap!",
                isBroken = false,
                initials = "DH",
                accentColor = 0xFF35526C
            ),
            Suspect(
                id = "suspect_sophia",
                name = "Sophia Chen",
                roleAndAge = "Friend • 27",
                occupation = "Illustrator & Co-worker",
                relationshipToVictim = "Best friend & studio partner",
                potentialMotive = "Copyright dispute over shared design client",
                alibi = "Had tea with Elena at 8:00 PM, then attended a 9:45 PM movie screening downtown.",
                statement = "I left Elena's place around 9:15 PM and was at the Cinema downtown until midnight. I have my ticket stub.",
                contradictingEvidenceId = "ev_cinema_stub",
                confessionOrClue = "Sophia's ticket stub and CCTV from Cinema 4 downtown confirm she was seated inside the theatre from 9:45 PM to 11:45 PM. Her alibi is completely solid.",
                isBroken = false,
                initials = "SC",
                accentColor = 0xFF2F4638
            ),
            Suspect(
                id = "suspect_marcus",
                name = "Marcus Vance",
                roleAndAge = "Ex-Boyfriend • 30",
                occupation = "Fitness Instructor",
                relationshipToVictim = "Ex-boyfriend (broke up 2 months ago)",
                potentialMotive = "Bitter breakup and demanding return of spare keys",
                alibi = "Claims he was out drinking with gym buddies across town.",
                statement = "I was out with my gym friends across town until 2 AM. You can ask anyone at the pub.",
                contradictingEvidenceId = "ev_police_blotter",
                confessionOrClue = "Marcus was actually stopped and cited at a DUI police checkpoint 12 miles away at 10:20 PM. Though aggressive in text messages, he was physically miles away when Elena was killed.",
                isBroken = false,
                initials = "MV",
                accentColor = 0xFF583A3A
            ),
            Suspect(
                id = "suspect_ryan",
                name = "Ryan Torres",
                roleAndAge = "Colleague • 35",
                occupation = "Design Agency Director",
                relationshipToVictim = "Elena's primary freelance client manager",
                potentialMotive = "Suspected embezzlement of agency funds",
                alibi = "Working late in the downtown design studio office.",
                statement = "I never left the studio building on 5th Avenue until nearly midnight.",
                contradictingEvidenceId = "ev_keycard_log",
                confessionOrClue = "Electronic keycard logs and front desk security logs confirm Ryan badged into the server room at 10:15 PM and exited the building lobby at 11:55 PM.",
                isBroken = false,
                initials = "RT",
                accentColor = 0xFF433F5E
            )
        )

        val evidence = listOf(
            EvidenceItem(
                id = "ev_crime_scene",
                caseId = "case_01_silent_apartment",
                title = "Crime Scene Photo",
                type = EvidenceType.PHOTOS,
                shortDescription = "Victim on sofa with locked window latch",
                fullDescription = "Elena Marsh was discovered slumped on her living room sofa. The living room window latch was closed from inside. No sign of forced entry on the main reinforced door.",
                source = "First Responders / Forensics",
                timestamp = "11:25 PM",
                isExamined = true,
                isDecisive = false
            ),
            EvidenceItem(
                id = "ev_cashmere_scarf",
                caseId = "case_01_silent_apartment",
                title = "Cashmere Scarf (Murder Weapon)",
                type = EvidenceType.PHYSICAL,
                shortDescription = "Blue woven scarf matching neck ligature marks",
                fullDescription = "Recovered from behind the living room sofa radiator. Fibers strictly match the ligature contusions on the victim's neck. Traces of male sweat detected on the fringed ends.",
                source = "Forensics / CSI Unit",
                timestamp = "11:40 PM",
                isExamined = true,
                isDecisive = true
            ),
            EvidenceItem(
                id = "ev_phone_records",
                caseId = "case_01_silent_apartment",
                title = "Phone & Router Records",
                type = EvidenceType.DOCUMENTS,
                shortDescription = "10:32 PM call from local router MAC address",
                fullDescription = "Data logs indicate an incoming encrypted call received by Elena at 10:32 PM lasting 4 minutes. The call originated from a device authenticated on Daniel Harper's home Wi-Fi network next door.",
                source = "Cyber Crime Division",
                timestamp = "10:32 PM",
                isExamined = true,
                isDecisive = true
            ),
            EvidenceItem(
                id = "ev_fingerprint_report",
                caseId = "case_01_silent_apartment",
                title = "Fingerprint Report",
                type = EvidenceType.FORENSICS,
                shortDescription = "Partial thumbprint on hallway door frame",
                fullDescription = "Fingerprint lift #4B reveals Daniel Harper's right thumb print freshly imprinted on the exterior rubber seal of Elena's doorway latch.",
                source = "Forensic Lab Registry",
                timestamp = "01:15 AM",
                isExamined = true,
                isDecisive = true
            ),
            EvidenceItem(
                id = "ev_cctv_alley",
                caseId = "case_01_silent_apartment",
                title = "Tea Shop CCTV Footage",
                type = EvidenceType.PHOTOS,
                shortDescription = "Reflective jacket entering rear alley at 10:45 PM",
                fullDescription = "High-definition street surveillance shows a person wearing a neon-striped running jacket matching Daniel's gear slipping through the building back fire exit at 10:45 PM, directly contradicting his claim that he was home.",
                source = "Riverside Tea House Camera",
                timestamp = "10:45 PM",
                isExamined = true,
                isDecisive = true
            ),
            EvidenceItem(
                id = "ev_handwritten_note",
                caseId = "case_01_silent_apartment",
                title = "Handwritten Note",
                type = EvidenceType.DOCUMENTS,
                shortDescription = "Note on coffee table: 'Meet me at 10 PM - E.'",
                fullDescription = "Note reads: 'Meet me at 10 PM to settle the escrow debt. If you stall again, the police report goes in tomorrow morning. - E.'",
                source = "Living Room Coffee Table",
                timestamp = "11:30 PM",
                isExamined = true,
                isDecisive = false
            ),
            EvidenceItem(
                id = "ev_nylon_thread",
                caseId = "case_01_silent_apartment",
                title = "Nylon Thread & Magnet",
                type = EvidenceType.PHYSICAL,
                shortDescription = "Fishing line and magnet recovered from 4th floor trash chute",
                fullDescription = "Crime scene techs found high-tensile 0.2mm nylon fishing monofilament tied around a strong neodymium magnet in Daniel's hallway trash chute. When looped over the internal deadbolt lever, this allows the lock to be turned from the outside hallway before pulling the thread free through the weather stripping.",
                source = "Hallway Waste Disposal Inspection",
                timestamp = "02:00 AM",
                isExamined = false,
                isDecisive = true
            ),
            EvidenceItem(
                id = "ev_police_blotter",
                caseId = "case_01_silent_apartment",
                title = "Traffic Checkpoint Blotter",
                type = EvidenceType.DOCUMENTS,
                shortDescription = "Marcus Vance cited 12 miles away at 10:20 PM",
                fullDescription = "Official highway patrol record logs Marcus Vance at an active breathalyzer sobriety checkpoint on Interstate 94 at 10:20 PM, establishing his presence miles away.",
                source = "Highway Patrol Station 7",
                timestamp = "10:20 PM",
                isExamined = false,
                isDecisive = false
            )
        )

        val deductions = listOf(
            DeductionRule(
                id = "ded_01",
                fromId = "suspect_daniel",
                toId = "ev_cctv_alley",
                relationType = RelationType.CONTRADICTS,
                description = "CCTV footage proves Daniel was outside Elena's building alley at 10:45 PM, shattering his home alibi."
            ),
            DeductionRule(
                id = "ded_02",
                fromId = "suspect_daniel",
                toId = "ev_handwritten_note",
                relationType = RelationType.MOTIVE,
                description = "The note proves Elena gave Daniel a midnight deadline to repay $15,000 before reporting him for fraud."
            ),
            DeductionRule(
                id = "ded_03",
                fromId = "ev_nylon_thread",
                toId = "victim_elena",
                relationType = RelationType.WEAPON,
                description = "The nylon thread and magnet explain the locked-room mystery—how Daniel turned the deadbolt from the hallway."
            ),
            DeductionRule(
                id = "ded_04",
                fromId = "suspect_daniel",
                toId = "ev_fingerprint_report",
                relationType = RelationType.SUPPORTS,
                description = "Daniel's fresh thumbprint on the outer weather latch links him physically to Elena's doorway."
            )
        )

        val boardNodes = listOf(
            BoardNode("victim_elena", "Elena Marsh", "Victim", NodeType.VICTIM, 0.50f, 0.48f),
            BoardNode("suspect_daniel", "Daniel Harper", "Neighbor • 32", NodeType.SUSPECT, 0.22f, 0.25f),
            BoardNode("suspect_sophia", "Sophia Chen", "Friend • 27", NodeType.SUSPECT, 0.78f, 0.25f),
            BoardNode("suspect_marcus", "Marcus Vance", "Ex-boyfriend • 30", NodeType.SUSPECT, 0.22f, 0.72f),
            BoardNode("suspect_ryan", "Ryan Torres", "Colleague • 35", NodeType.SUSPECT, 0.78f, 0.72f),
            BoardNode("ev_cctv_alley", "Tea Shop CCTV", "Alley Footwear", NodeType.EVIDENCE, 0.50f, 0.16f),
            BoardNode("ev_cashmere_scarf", "Cashmere Scarf", "Murder Weapon", NodeType.EVIDENCE, 0.50f, 0.82f)
        )

        val boardConnections = listOf(
            BoardConnection("conn_01", "suspect_daniel", "victim_elena", RelationType.MOTIVE, isUserCreated = false, isVerified = true),
            BoardConnection("conn_02", "suspect_sophia", "victim_elena", RelationType.SUPPORTS, isUserCreated = false, isVerified = true),
            BoardConnection("conn_03", "suspect_marcus", "victim_elena", RelationType.TIMELINE, isUserCreated = false, isVerified = false),
            BoardConnection("conn_04", "suspect_ryan", "victim_elena", RelationType.TIMELINE, isUserCreated = false, isVerified = false)
        )

        return CaseModel(
            id = "case_01_silent_apartment",
            title = "The Silent Apartment",
            subtitle = "A locked-room tragedy in the heart of Riverside",
            synopsis = "A neighbor found a dead body in the apartment next door. The door was locked from the inside. But how did the killer get in?",
            category = CaseCategory.DAILY,
            difficulty = 3,
            estimatedMinutes = 15,
            location = "Riverside Apartments, Unit 4B",
            incidentTime = "Last night (10:30 PM)",
            victim = victim,
            status = CaseStatus.IN_PROGRESS,
            canonicalCulpritId = "suspect_daniel",
            canonicalCulpritName = "Daniel Harper",
            canonicalMotive = "Financial gain / debt evasion ($15,000)",
            canonicalMethod = "Strangulation (using scarf) + thread & magnet deadbolt trick",
            decisiveEvidenceIds = listOf("ev_cctv_alley", "ev_cashmere_scarf", "ev_phone_records", "ev_nylon_thread"),
            howYouKnewSummary = "Phone records + inconsistent alibi on CCTV + nylon thread deadbolt trick",
            fullSolutionExplanation = "Daniel Harper owed Elena $15,000 in escrow funds. Elena issued an ultimatum to repay by midnight or face criminal fraud charges. At 10:45 PM, Daniel sneaked through the rear alley (caught on CCTV) wearing his running jacket. During an altercation on the sofa, he strangled her using her own cashmere scarf. To stage a locked-room suicide or impossible crime, he looped monofilament nylon thread around the inside deadbolt knob with a magnet, exited through the main door, and turned the latch from the hallway before pulling the thread free. The thread was found in the trash chute alongside his Wi-Fi phone logs.",
            timeline = timeline,
            suspects = suspects,
            evidence = evidence,
            deductions = deductions,
            boardNodes = boardNodes,
            boardConnections = boardConnections
        )
    }

    private fun createRiversideMurderCase(): CaseModel {
        val victim = VictimProfile(
            name = "Julian Sterling",
            age = 54,
            occupation = "Antique Art Dealer",
            relationship = "Owner of Sterling Fine Arts",
            summary = "Found dead on the dark wooden planks of Pier 14. Blunt force trauma to the temple with tidal marks on clothing.",
            portraitInitials = "JS"
        )
        return CaseModel(
            id = "case_02_riverside_murder",
            title = "The Riverside Murder",
            subtitle = "A high-stakes art smuggling operation ends in homicide",
            synopsis = "Julian Sterling's body was discovered under Pier 14 at low tide. With valuable maritime cargo missing, three individuals had access to the private docks.",
            category = CaseCategory.LONG_TERM,
            difficulty = 4,
            estimatedMinutes = 25,
            stagesText = "3/3 stages",
            location = "Riverside Pier 14, Docks Warehouse",
            incidentTime = "Two nights ago (11:45 PM)",
            victim = victim,
            status = CaseStatus.AVAILABLE,
            canonicalCulpritId = "suspect_bruce",
            canonicalCulpritName = "Captain Bruce Foley",
            canonicalMotive = "Smuggling dispute over $200,000 antique maritime coins",
            canonicalMethod = "Blunt force with brass ship winch lever",
            decisiveEvidenceIds = listOf("ev_pier_winch", "ev_tide_watch"),
            howYouKnewSummary = "Winch lever brass residue + tide water analysis + harbor radio log",
            fullSolutionExplanation = "Captain Bruce Foley transported smuggled 17th-century doubloons for Julian. When Julian refused to pay the inflated transport bounty, Foley struck him with the brass winch bar and pushed him beneath the tidal pilings.",
            timeline = emptyList(),
            suspects = emptyList(),
            evidence = emptyList(),
            deductions = emptyList()
        )
    }

    private fun createMuseumHeistCase(): CaseModel {
        val victim = VictimProfile(
            name = "Museum Night Security",
            age = 45,
            occupation = "Senior Guard",
            relationship = "In charge of North Wing Gallery",
            summary = "Knocked unconscious and the rare Somerset Star Emerald stolen from the armored display case.",
            portraitInitials = "SG"
        )
        return CaseModel(
            id = "case_03_museum_heist",
            title = "The Museum Heist",
            subtitle = "A precision theft of the city's most priceless gemstone",
            synopsis = "A blackout occurred at 02:00 AM. The lasers were bypassed using internal override codes known only to museum insiders.",
            category = CaseCategory.COMPLETED,
            difficulty = 3,
            estimatedMinutes = 18,
            location = "Grand Metropolitan Museum, Vault 3",
            incidentTime = "Solved on Oct 18, 2025",
            victim = victim,
            status = CaseStatus.SOLVED,
            score = 92,
            accuracy = 92,
            solvedDate = "Oct 18, 2025",
            canonicalCulpritId = "suspect_curator",
            canonicalCulpritName = "Dr. Raymond Vance",
            canonicalMotive = "Replacing authentic emerald with synthetic paste duplicate",
            canonicalMethod = "Laser circuit loop shunt + sedative aerosol",
            decisiveEvidenceIds = listOf("ev_laser_shunt"),
            howYouKnewSummary = "Access log badge cloned from curator's office safe",
            fullSolutionExplanation = "Dr. Vance used a pre-installed resistor shunt to disable the infrared beam without tripping the central alarm panel.",
            timeline = emptyList(),
            suspects = emptyList(),
            evidence = emptyList(),
            deductions = emptyList()
        )
    }

    private fun createPoisonedGalaCase(): CaseModel {
        val victim = VictimProfile(
            name = "Lord Arthur Blackwood",
            age = 62,
            occupation = "Philanthropist & Industrialist",
            relationship = "Host of Annual Charity Gala",
            summary = "Collapsed during the midnight vintage champagne toast in front of eighty socialite guests.",
            portraitInitials = "AB"
        )
        return CaseModel(
            id = "case_04_poisoned_gala",
            title = "The Poisoned Gala",
            subtitle = "Cyanide in the crystal flute at the high-society ball",
            synopsis = "Only Arthur's glass contained lethal sodium cyanide. The sommelier poured from an unopened magnum bottle in full view.",
            category = CaseCategory.COMPLETED,
            difficulty = 4,
            estimatedMinutes = 20,
            location = "Blackwood Manor, Grand Ballroom",
            incidentTime = "Solved on Oct 16, 2025",
            victim = victim,
            status = CaseStatus.SOLVED,
            score = 88,
            accuracy = 88,
            solvedDate = "Oct 16, 2025",
            canonicalCulpritId = "suspect_sommelier",
            canonicalCulpritName = "Beatrice Vance",
            canonicalMotive = "Revenge for brother's ruined mining company",
            canonicalMethod = "Cyanide-coated crystal rim dried before the banquet",
            decisiveEvidenceIds = listOf("ev_glass_rim"),
            howYouKnewSummary = "Residue on glass rim rather than inside the wine liquid",
            fullSolutionExplanation = "The poison was applied directly to the crystal rim of Lord Blackwood's monogrammed glass hours before the toast.",
            timeline = emptyList(),
            suspects = emptyList(),
            evidence = emptyList(),
            deductions = emptyList()
        )
    }

    private fun createDefaultDailyChallenge(): DailyChallenge {
        return DailyChallenge(
            id = "challenge_missing_necklace",
            title = "The Missing Necklace",
            premise = "A valuable diamond necklace disappeared from a locked hotel suite. Three staff members had master keys during the 15-minute window.",
            difficulty = 2,
            clueText = "The thief wore velvet gloves and didn't touch the dressing table dust, yet the safe keypad was wiped with lemon-scented solvent.",
            options = listOf(
                "Housekeeper (carrying lemon cleaning spray)",
                "Bellhop (carrying luggage cart)",
                "Concierge (at front desk)"
            ),
            correctIndex = 0,
            explanation = "The lemon cleaning spray bottle carried on the housekeeper's cart matches the specific solvent residues on the safe buttons!"
        )
    }
}
