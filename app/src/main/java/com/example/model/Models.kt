package com.example.model

enum class CaseCategory {
    ALL,
    DAILY,
    LONG_TERM,
    COMPLETED
}

enum class CaseStatus {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    SOLVED
}

enum class EvidenceType {
    ALL,
    PHOTOS,
    DOCUMENTS,
    FORENSICS,
    PHYSICAL
}

enum class TimelineStatus {
    CONFIRMED,
    REPORTED,
    DISPUTED
}

enum class NodeType {
    VICTIM,
    SUSPECT,
    EVIDENCE,
    EVENT
}

enum class RelationType(val label: String, val colorHex: Long) {
    CONTRADICTS("Contradicts", 0xFFD65D5D),
    MOTIVE("Motive", 0xFFE7B15E),
    TIMELINE("Timeline Link", 0xFF6C9DBA),
    WEAPON("Weapon Link", 0xFFE04545),
    ALIBI_BREAK("Breaks Alibi", 0xFFFF8A65),
    SUPPORTS("Supports Fact", 0xFF4EBA88)
}

enum class DetectiveRank(val title: String, val minXp: Int, val maxXp: Int) {
    ROOKIE("Rookie Detective", 0, 300),
    JUNIOR("Junior Investigator", 300, 750),
    FIELD("Field Detective", 750, 1500),
    SENIOR("Senior Detective", 1500, 3000),
    CHIEF("Chief Investigator", 3000, 5000)
}

data class VictimProfile(
    val name: String,
    val age: Int,
    val occupation: String,
    val relationship: String,
    val summary: String,
    val portraitInitials: String = "EM"
)

data class TimelineItem(
    val id: String,
    val time: String,
    val title: String,
    val source: String,
    val status: TimelineStatus,
    val details: String
)

data class Suspect(
    val id: String,
    val name: String,
    val roleAndAge: String,
    val occupation: String,
    val relationshipToVictim: String,
    val potentialMotive: String,
    val alibi: String,
    val statement: String,
    val contradictingEvidenceId: String,
    val confessionOrClue: String,
    val isBroken: Boolean = false,
    val initials: String,
    val accentColor: Long = 0xFF2A4259
)

data class EvidenceItem(
    val id: String,
    val caseId: String,
    val title: String,
    val type: EvidenceType,
    val shortDescription: String,
    val fullDescription: String,
    val source: String,
    val timestamp: String,
    val isExamined: Boolean = false,
    val playerNotes: String = "",
    val isDecisive: Boolean = false
)

data class DeductionRule(
    val id: String,
    val fromId: String,
    val toId: String,
    val relationType: RelationType,
    val description: String
)

data class BoardNode(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: NodeType,
    val x: Float, // Relative 0..1 coordinates
    val y: Float
)

data class BoardConnection(
    val id: String,
    val fromNodeId: String,
    val toNodeId: String,
    val relationType: RelationType,
    val isUserCreated: Boolean = true,
    val isVerified: Boolean = false
)

data class CaseModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val synopsis: String,
    val category: CaseCategory,
    val difficulty: Int, // 1..5
    val estimatedMinutes: Int,
    val location: String,
    val incidentTime: String,
    val victim: VictimProfile,
    val status: CaseStatus,
    val solvedDate: String? = null,
    val score: Int? = null,
    val accuracy: Int? = null,
    val stagesText: String? = null,
    // Canonical solution
    val canonicalCulpritId: String,
    val canonicalCulpritName: String,
    val canonicalMotive: String,
    val canonicalMethod: String,
    val decisiveEvidenceIds: List<String>,
    val howYouKnewSummary: String,
    val fullSolutionExplanation: String,
    // Case data
    val timeline: List<TimelineItem>,
    val suspects: List<Suspect>,
    val evidence: List<EvidenceItem>,
    val deductions: List<DeductionRule>,
    val boardNodes: List<BoardNode> = emptyList(),
    val boardConnections: List<BoardConnection> = emptyList()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val premise: String,
    val difficulty: Int,
    val clueText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val isCompleted: Boolean = false,
    val selectedOption: Int? = null
)

data class DetectiveProfile(
    val rank: DetectiveRank = DetectiveRank.ROOKIE,
    val currentXp: Int = 120,
    val casesSolved: Int = 1,
    val winRate: Int = 85,
    val currentStreak: Int = 4,
    val activeCaseId: String = "case_01_silent_apartment"
)
