# CASE ZERO — Complete Product Requirements Document
**Version:** 2.0 · **Platform:** Native Android · **Build workflow:** Google AI Studio · **Budget:** ৳0 for development  
**Product type:** Offline-first detective investigation game  
**Master design reference:** The previously generated CASE ZERO multi-screen UI storyboard. Attach that image to Google AI Studio with this document.

---

## 0. EXECUTION DIRECTIVE

Build CASE ZERO as a real, polished, playable Android game—not a prototype, landing page, WebView, or static UI demo.

The attached CASE ZERO storyboard is the mandatory master visual reference. Preserve its screen composition, cinematic imagery, noir palette, card hierarchy, navigation style, and atmosphere. The reference is illustrative rather than a source of exact content: correct any garbled mockup text and implement clear, functional UI.

**Do not attempt to implement every phase in one uncontrolled code dump.** First inspect the existing project and tool capabilities. Then implement the phases in order, building and testing after each. Preserve existing working code and user data. If a feature cannot be completed in the current phase, label it as not implemented instead of displaying a fake control.

### Non-negotiable success conditions
1. Native Android UI; prefer Kotlin + Jetpack Compose if the project environment supports it.
2. No paid AI APIs or paid backend services. The app must not need an AI model to run.
3. Core cases and gameplay work offline, without login.
4. A small number of fully authored, logically valid cases is better than many shallow cases.
5. Every visible control has a real action.
6. Progress survives app closure and reopening.
7. No layout overflow, clipped text, broken alignment, awkward spacing, or inaccessible controls.
8. The mysterious cinematic atmosphere must be consistent across every screen.
9. No fake statistics, fake online players, fake leaderboards, or claims of live data.
10. No feature is considered complete until it has been exercised and verified.

# 1. PRODUCT DEFINITION

## 1.1 Product premise
CASE ZERO is a single-player detective game in which the player solves original fictional criminal cases through observation, deduction, and evidence-based reasoning.

The player is not simply asked “Who did it?” They must investigate:
- What happened?
- When did it happen?
- Who had motive, access, and opportunity?
- Which statements conflict with established facts?
- Which clues are decisive and which are distractions?
- Which explanation is supported by the total evidence?

A case ends with an accusation and a reasoned explanation of the solution.

## 1.2 Target audience
Players who enjoy detective fiction, murder mysteries, logic puzzles, crime investigation stories, and thoughtful mobile games. The experience should be approachable for casual players but reward careful reasoning.

## 1.3 Product pillars
**Atmosphere:** A rainy, cinematic, late-night detective world.  
**Deduction:** Clues and contradictions matter; guessing alone is not mastery.  
**Clarity:** The player always understands what can be examined and what to do next.  
**Continuity:** The investigation can be paused and resumed without losing work.  
**Fairness:** The solution is logically supported by clues available before the accusation.  
**Craft:** Every screen looks intentional and every interaction behaves reliably.

## 1.4 MVP boundaries
The first release is an offline single-player game. Do not build multiplayer, cloud accounts, live chat, user-generated cases, generative AI suspect conversations, or real-time global rankings in the MVP. These features introduce cost, moderation, security, and backend complexity without being necessary to validate the core experience.

# 2. MASTER MOCKUP AND VISUAL SPECIFICATION

## 2.1 Reference screen map
The supplied storyboard defines the intended design for:
- CASE ZERO splash.
- Three onboarding panels.
- Home dashboard.
- Case library.
- Case overview.
- Case timeline and victim profile.
- Evidence gallery.
- Investigation board.
- Suspect interrogation.
- Final accusation and solved state.
- Results and score.
- Detective rank and achievements.
- Daily challenge.
- Settings.
- Optional light-mode home.

Use the storyboard as the design baseline for proportions and hierarchy. If a reference detail is ambiguous, choose the simplest implementation that preserves the overall style and usability.

## 2.2 Art direction: Noir Obsidian
The world should feel mysterious, intelligent, restrained, and cinematic—not horror-themed, cartoonish, or cyberpunk neon.

**Palette tokens (starting values; refine after visual inspection):**
- `background`: #071019
- `surface`: #0B1722
- `surfaceElevated`: #102231
- `surfaceInteractive`: #142A3A
- `accentAmber`: #E7B15E
- `accentAmberMuted`: #B98743
- `accentBlue`: #6C9DBA
- `textPrimary`: #F3F1EA
- `textSecondary`: #A8B4BE
- `textMuted`: #748391
- `success`: muted green with accessible contrast
- `danger`: restrained crimson with accessible contrast
- `divider`: low-contrast blue-gray

Use centralized theme tokens rather than hardcoding colors separately on every screen. Test actual contrast; the palette is a starting point, not an excuse for low readability.

## 2.3 Imagery and assets
Use original, legally usable assets bundled with the app. Prefer a small, consistent collection of cinematic scenes, suspect portraits, case thumbnails, and evidence images. Optimize images for mobile memory and storage.

Do not depend on downloading images at runtime. Do not use copyrighted movie/TV stills, real crime-scene photographs of victims, or unlicensed assets. If suitable assets cannot be sourced at zero cost, create simple original illustrations or carefully designed placeholders and clearly keep the art direction consistent.

The app must still look intentional when an optional image is unavailable. Never show broken-image icons.

## 2.4 Motion and sound
Use subtle transitions: fade, short slide, restrained card motion, evidence selection feedback, and small progress animations. Avoid long intro sequences, excessive parallax, constant particle effects, or animations that slow older phones.

Sound and haptics are optional enhancements, not MVP blockers. If included, provide settings and respect silent mode and accessibility preferences.

# 3. STRICT UI/UX AND LAYOUT RULES

This is a critical acceptance area. A beautiful color palette cannot compensate for poor spacing or broken layout.

## 3.1 Spacing system
Use a consistent 4dp-based scale, with 8dp as the standard rhythm:
- 4dp: icon/text micro-gap.
- 8dp: compact gap.
- 12dp: dense card padding or related elements.
- 16dp: default screen horizontal inset and standard card padding.
- 20dp: spacious card or section padding.
- 24dp: separation between major sections.
- 32dp: major visual separation.

These are reusable tokens, not fixed values for every situation. Establish a hierarchy and use it consistently.

## 3.2 Layout requirements
- Align screen titles, section headings, cards, and primary actions to consistent guides.
- Use intentional parent padding and child spacing. Do not stack random spacer values until the screen looks acceptable.
- Prefer responsive Compose layouts over absolute coordinates.
- Do not set fixed card heights if dynamic text may be clipped.
- Keep buttons inside their parent containers and inside the safe display area.
- Use scrollable content when necessary; keep important actions visible or predictably reachable.
- Do not create horizontal scrolling unless it is intentional and visually communicated.
- Avoid cards nested inside cards without a clear information hierarchy.
- Keep icon sizes, card radii, elevations, and button heights consistent.
- Maintain readable line lengths and sensible text wrapping.
- Ensure long case titles, suspect names, translated strings, and large font settings do not break the layout.
- Respect display cutouts, status bars, navigation bars, edge-to-edge behavior, and gesture insets.
- Bottom navigation must not cover the last item in scrollable content.
- Avoid tiny metadata, low-contrast labels, excessive empty space, or dense walls of text.

## 3.3 Touch, accessibility, and feedback
- Aim for at least 48dp touch targets.
- Give every interactive element a clear pressed, selected, disabled, and where relevant loading state.
- Provide meaningful accessibility labels for icons and interactive images.
- Never communicate a state only through color.
- Support system font scaling without clipping essential content.
- Provide a usable alternative to drag-only evidence-board interactions.
- Respect reduced-motion preferences where practical.

## 3.4 Mandatory layout review
For each screen:
1. Inspect at a compact phone width and a typical phone width.
2. Check top/bottom insets and scroll behavior.
3. Check long text and large font scale.
4. Check that no element overlaps, clips, or escapes its parent.
5. Check spacing consistency against related screens.
6. Fix issues before proceeding.

# 4. TECHNICAL ARCHITECTURE

## 4.1 Recommended stack
Use Kotlin, Jetpack Compose, Material 3 foundations, Navigation Compose, ViewModel, coroutines/Flow, Room, and DataStore where supported by the actual project. Use stable compatible versions and avoid unnecessary dependencies.

If the existing generated project already uses a viable native stack, inspect it before changing architecture. Do not replace working code without a clear reason.

## 4.2 Layers
- **Presentation:** screens, reusable components, theme, UI state.
- **Navigation:** routes, arguments, back behavior, deep links only if needed.
- **Domain:** investigation rules, deduction evaluation, progression calculations.
- **Content:** structured bundled case definitions and media references.
- **Data:** Room entities/DAO, repositories, DataStore preferences.
- **Platform services:** notification scheduling and optional ads.

Keep game rules out of composables. UI renders observable state and sends user actions to ViewModels/domain logic. Avoid duplicating the same case logic in multiple screens.

## 4.3 Stable identity
Give cases, suspects, evidence, statements, events, deductions, achievements, and rewards stable IDs. Never use list positions or display names as database identity. Display-name capitalization changes must not create duplicate cases.

## 4.4 State and navigation
Navigation must preserve the current case context. Back navigation must return to the previous meaningful screen without resetting the investigation. On process recreation, restore the active case and saved progress from storage.

# 5. DATA MODEL AND PERSISTENCE

The following describes required conceptual data; adapt it to the selected database implementation.

## 5.1 Case definition
Each case includes:
- `caseId`, title, subtitle, synopsis, cover asset, category, difficulty, estimated duration.
- Victim profile and relevant location data.
- Canonical incident timeline.
- Suspect definitions and relationships.
- Evidence definitions and discovery conditions.
- Statements and their truth status.
- Contradiction and deduction relationships.
- Canonical culprit, motive, method, timeline, and solution explanation.
- Required deductions, optional discoveries, hint stages, and scoring rubric.
- Unlock requirements and completion rewards.

Separate immutable authored case content from mutable player progress.

## 5.2 Player progress
Persist:
- Onboarding completion.
- Theme and settings.
- Current active case.
- Case status: locked, available, in progress, completed.
- Discovered and examined evidence.
- Notes and confirmed deductions.
- Suspect interrogation/statement state.
- Timeline discoveries.
- Investigation-board positions and links.
- Accusation attempts and final result.
- XP, rank, achievements, challenge history, and streak state.
- Notification preferences.

## 5.3 Persistence behavior
Save important actions promptly. App process termination, screen rotation, or navigation must not silently discard work. Test by force-closing and reopening.

Provide an intentional reset-progress action with confirmation. Do not reset the database just to update content. Use safe, versioned migrations. Handle missing or corrupted records gracefully and show a useful recovery state rather than crashing.

# 6. CASE CONTENT FORMAT AND MYSTERY INTEGRITY

Each case must be authored from its solution outward. Establish the truth before writing dialogue or clue descriptions.

For every case, document:
1. The true sequence of events.
2. The culprit.
3. Motive.
4. Method.
5. Opportunity and access.
6. Each suspect's real whereabouts and knowledge.
7. What each suspect says and why.
8. Every evidence item's source and meaning.
9. Which clues are decisive, supporting, misleading, or irrelevant.
10. The deductions a careful player can make.
11. The minimum evidence needed for a fair accusation.
12. Acceptable alternative interpretations, if any.
13. The full post-case explanation.

### Fair-play rules
- All decisive information must be discoverable before the final accusation.
- Dates, times, travel, access, statements, and physical evidence must agree.
- Do not make the solution depend on a hidden fact never shown to the player.
- A red herring may be suspicious, but its resolution must be fair.
- Not every suspect who lies is the murderer.
- Distinguish lies about the crime from lies about unrelated personal secrets.
- The culprit must be supported by a combination of evidence, not merely a confession that appears from nowhere.
- Hints may direct attention to evidence but must not silently reveal the answer too early.
- Use original fictional cases; do not claim invented events are real crimes.

### MVP content target
Ship with **at least 3 complete, tested cases** if the content budget permits. Each should have a distinct setting, motive pattern, suspect cast, and decisive evidence. It is better to ship 1–3 genuinely complete cases than to fill the app with repetitive or logically broken filler. Every advertised case must be playable end-to-end.

# 7. SCREEN-BY-SCREEN REQUIREMENTS

## 7.1 Splash
- CASE ZERO wordmark and short tagline.
- Rainy-night/cinematic image matching the reference.
- Dark system-bar treatment.
- Subtle short animation, if performant.
- Fast startup; no artificial delay or network dependency.

## 7.2 Onboarding
Three concise panels:
1. Investigate real-feeling fictional mysteries.
2. Examine evidence and question suspects.
3. Connect clues and make a defensible accusation.
Include back/next, skip where appropriate, and get-started. Persist completion. Returning users should not repeat onboarding.

## 7.3 Home
- Contextual greeting based on local time.
- Detective rank and real progress.
- Featured daily case.
- Continue Investigation card when a case is in progress.
- Shortcuts to cases and daily challenge.
- Recent/available cases.
- No fabricated statistics. For a new user, use a designed welcome state instead of fake numbers.
- Main CTA opens the correct case and restores saved state.

## 7.4 Case library
- Categories: Daily, Main Cases, In Progress, Completed.
- Search and filters must actually work.
- Case cards show title, premise, difficulty, estimated duration, status, and progress when relevant.
- Empty states explain what to do next.
- Avoid duplicate cases from casing differences or repeated seed data.

## 7.5 Case overview
Show case premise, victim, location, incident time, difficulty, known facts, and Start/Continue action. Do not reveal the canonical solution. Distinguish confirmed facts from unverified claims.

## 7.6 Case file and timeline
Display chronological events with source attribution and clear distinctions between confirmed, reported, and disputed information. Allow the player to revisit events. Timeline content comes from structured case data.

## 7.7 Suspect dossiers
Each suspect has a stable identity, portrait, background, relationship to victim, potential motive, known opportunity, alibi, statements, and discovered contradictions. Do not label someone guilty before the solution reveal.

## 7.8 Evidence gallery
- Filter by evidence type and discovery state.
- Evidence cards have clear title, type, thumbnail, and examination status.
- Selecting a card opens its full detail view.
- Mark as examined and add/edit notes.
- Evidence status updates consistently across gallery, board, and case file.
- No dead filters or placeholder evidence.

## 7.9 Evidence detail
Show the item, description, source/collection context, relevant timestamp, known relationships, and player notes. Do not reveal all possible deductions simply because an item is opened. Marking as examined must persist.

## 7.10 Investigation board
Implement a functional, touch-friendly board:
- Add discovered evidence, suspects, and relevant events.
- Move and arrange nodes.
- Connect nodes with defined relationship types.
- Inspect, edit, and remove a connection.
- Pan/zoom if feasible and performant.
- Save node positions and connections.
- Reset layout only after confirmation; resetting layout must not erase evidence.
- Provide a list-based accessible alternative.
- Connection validation uses case-defined relationships; arbitrary lines are not automatically correct deductions.
- Avoid drag-only interactions; offer tap-to-select then tap-to-connect where feasible.
- Ensure the board remains usable on compact displays.

## 7.11 Interrogation
- Show suspect portrait and profile.
- Present authored dialogue/statement choices.
- Allow the player to challenge a statement using relevant discovered evidence.
- Validate the selected evidence against the case logic.
- Give contextual feedback without exposing the entire solution.
- Persist interrogation state.
- Include unrelated lies and truthful-but-suspicious statements for believable nuance.
- No paid generative AI or runtime network request.

## 7.12 Final accusation
Require the player to select a culprit and, where the case supports it, identify motive, method, and supporting evidence. Explain submission and confirmation clearly. Do not let an accidental tap end the case. Evaluation should assess the culprit and the strength of the theory, not just the name.

## 7.13 Results
Display outcome, deduction score, evidence quality, correct deductions, missed optional clues, mistakes, time if accurately measured, XP, achievements, and next case. Reveal the canonical solution and explain how the decisive clues establish it. Save the result permanently.

## 7.14 Rank and achievements
Suggested ranks: Rookie Detective, Junior Investigator, Field Detective, Senior Detective, Chief Investigator. Define explicit XP thresholds and persist rank changes. Achievements must have real unlock criteria and must not be awarded repeatedly for the same event.

## 7.15 Daily challenge
Provide a short authored challenge selected from bundled content using a stable local-date rule. Avoid changing the challenge every time the app opens. Persist completion and result. Handle timezone/date changes sensibly. Do not claim that local challenges are live global competitions.

## 7.16 Profile and settings
Include actual progress, theme selection, notification controls, sound/haptics if implemented, language only if fully translated, About, Privacy Policy, Terms and Conditions, and Reset Progress. Legal pages must load without network access and contain appropriate app-specific text. Do not include a fake support email or fabricated company address.

# 8. DEDUCTION ENGINE AND SCORING

Use deterministic, unit-testable rules. No AI model is needed to solve or score cases.

## 8.1 Deduction types
- Timeline contradiction.
- Alibi contradiction.
- Motive supported by evidence.
- Opportunity/access established.
- Physical evidence link.
- Statement contradicted by a verified fact.
- Method supported by evidence.
- Alternative suspect eliminated.

## 8.2 Evidence relationship model
Each case defines known relationships between evidence, suspects, events, statements, and deductions. A discovered clue can support multiple deductions. A connection may be:
- Supports.
- Contradicts.
- Places at location.
- Establishes timeline.
- Establishes motive.
- Establishes access/opportunity.
- Links evidence to a suspect.

Only case-defined or otherwise justified relationships count as correct. The game should explain why a relationship is correct or unsupported.

## 8.3 Score model
Use a transparent per-case rubric rather than arbitrary random scores. Suggested starting weights:
- Correct culprit: 35%.
- Decisive evidence selection: 25%.
- Correct motive: 10%.
- Correct method: 10%.
- Timeline/contradiction reasoning: 15%.
- Unsupported assumptions penalty: up to 5%.

Normalize scoring for each case and test that scores remain within 0–100. Adjust weights per case if necessary, but document them. Optional clues should improve the score or unlock achievements without making a fair solution impossible.

Record both the score and a human-readable explanation of its components. Never show “100% evidence accuracy” unless the scoring calculation supports it.

# 9. RETENTION DESIGN

Retention should come from meaningful mystery progression, not manipulative friction.

- Daily mini-case or deduction puzzle.
- Continue Investigation entry point.
- Multi-session cases with automatic saving.
- Rank and achievements tied to reasoning quality.
- Case archive and replay.
- Optional daily streak.
- A clear “next case” transition after results.
- New challenge availability based on local content.
- Optional notifications, disabled unless the user opts in.

Avoid punishing streak resets, artificial energy timers, forced social sharing, fake scarcity, and forced ads before the player can continue.

# 10. MONETIZATION AND POLICY

Ads are optional for the initial build and must not block core functionality.

Possible later placements:
- An occasional interstitial after the results screen, never during a critical deduction or accusation.
- Optional rewarded ad for a hint or bonus item, if the ad is available and the user explicitly chooses it.

Requirements:
- Use an official, policy-compliant SDK only when intentionally implementing ads.
- During development, use test ad configuration. Do not ship test ads as production ads.
- Core gameplay must work if no ad is loaded or the device is offline.
- Do not reward accidental clicks or place ads beside controls likely to be tapped unintentionally.
- Do not promise revenue or guaranteed ad fill.
- Keep monetization behind a clear module/feature boundary so it can be added after gameplay QA.

# 11. NOTIFICATIONS

Optional reminders:
- Daily challenge reminder.
- Resume an unfinished case, if the player enables it.

Requirements:
- Explain the value of reminders before asking permission.
- Request runtime permission where required by the Android version.
- Respect denial and never repeatedly prompt.
- Allow the player to turn reminders off.
- Use appropriate Android scheduling APIs; do not promise exact delivery if the platform does not guarantee it.
- Avoid duplicate, test-only, or excessive notifications.
- Do not claim that notifications are scheduled until scheduling succeeds.

# 12. OFFLINE AND ERROR BEHAVIOR

The app must launch, browse cases, investigate, save progress, calculate results, and display legal pages without internet access.

- Bundle essential case definitions and media.
- Do not show fake network loading spinners for local operations.
- If an optional asset fails, use a designed fallback.
- If a record is missing, show a recoverable state instead of crashing.
- If a database operation fails, report the problem clearly and avoid falsely displaying “Saved.”
- If ads or optional services are unavailable, gameplay continues normally.
- Do not require account creation for the MVP.

# 13. SECURITY, PRIVACY, AND CONTENT

- Collect no personal data unless a feature truly requires it.
- Do not request unnecessary permissions.
- Keep local progress on-device.
- If analytics or ads are later added, update privacy disclosures and follow applicable platform rules.
- Do not fabricate a developer identity, support address, company, or legal claims.
- Clearly identify the cases as fictional.
- Use assets with compatible licenses and retain any required attribution.
- Avoid graphic depictions of violence; aim for suspense through atmosphere, clues, and narrative.

# 14. TEST PLAN AND ACCEPTANCE CRITERIA

A release candidate is not complete until all applicable checks pass.

## Functional tests
- Fresh install → splash → onboarding → home.
- Skip and complete onboarding; relaunch and confirm onboarding does not repeat.
- Open each case and start/resume it.
- Examine evidence and confirm the state updates across screens.
- Add and edit notes.
- Add, move, connect, and remove board items; leave the screen and confirm state persists.
- Challenge a suspect statement with relevant and irrelevant evidence.
- Complete a case accusation and review the score/solution.
- Confirm XP and achievements are awarded once, correctly.
- Force-stop and reopen during an investigation; confirm progress restores.
- Change theme and confirm it persists.
- Test empty, locked, completed, and error states.
- Test notification permission granted and denied, if notifications are implemented.
- Test offline launch and a complete offline case.

## Visual tests
- Check every screen on a compact and typical phone width.
- Check system insets and bottom navigation.
- Check long titles, long statements, and large font scale.
- Confirm no clipped text, overflow, overlapping cards, buttons outside containers, or inconsistent alignment.
- Confirm colors and typography remain consistent.
- Confirm the master reference's visual identity remains recognizable.
- Check touch targets and contrast.
- Check scrolling and back navigation.

## Content tests
For every shipped case:
- Verify the canonical solution.
- Verify timeline consistency.
- Verify every decisive clue is discoverable.
- Verify each suspect's statements against the truth.
- Verify red herrings are fair.
- Verify the player can reach a correct solution without guessing hidden information.
- Verify score calculations and explanation.
- Verify the solution is not revealed before accusation except through an explicitly requested solution view.

## Technical tests
- Build succeeds from a clean state.
- No startup crash or navigation crash.
- No dead buttons or placeholder destinations.
- No user data loss during normal updates.
- No duplicated case entries.
- No claims of online functionality where none exists.
- App remains responsive on modest Android hardware.

# 15. IMPLEMENTATION PHASES

**Phase 1 — Foundation**
- Inspect project and choose compatible native architecture.
- Establish theme tokens, typography, spacing, reusable components, system insets, and navigation.
- Implement splash, onboarding, home, case list, settings shell.
- Set up local persistence and structured case content.
- Validate visual quality before expanding.

**Phase 2 — One Complete Vertical Slice**
- Build one complete case from overview through timeline, suspects, evidence, notes, interrogation, accusation, scoring, solution, and saved result.
- Test restart/resume behavior.
- Do not proceed until the case works end-to-end.

**Phase 3 — Investigation Board**
- Implement functional nodes, links, validation, touch interactions, persistence, and accessible alternative controls.
- Verify board behavior on compact phones.

**Phase 4 — Content and Progression**
- Add additional fully tested cases.
- Add case filters, daily challenge, ranks, achievements, archive, and replay.
- Ensure no duplicate or fake data.

**Phase 5 — Finishing**
- Settings, optional notifications, legal pages, accessibility, asset optimization, empty/error states, and optional ad integration.
- Full functional and visual QA.
- Fix regressions before adding any new feature.

At the end of each phase, provide a concise report: what is implemented, what was tested, known limitations, and the next phase. Never claim untested work is complete.

# 16. REQUIRED DELIVERY

Deliver:
1. A buildable native Android project.
2. Reusable UI components and centralized design tokens.
3. Structured case content and deterministic investigation rules.
4. Persistent local storage.
5. At least one fully playable, tested end-to-end case before scaling content.
6. A screen-by-screen implementation matching the supplied master mockup.
7. Build/run instructions.
8. A short list of implemented features, tests performed, and remaining limitations.

Do not deliver only a PRD summary, a screenshot, a static prototype, or a collection of disconnected screens.

## FINAL MASTER INSTRUCTION

The previously generated CASE ZERO storyboard is the mandatory master UI reference. Build the app around its cinematic noir identity and its screen hierarchy. The atmosphere must remain mysterious and premium, while the interface stays exceptionally clean and usable.

**Spacing, padding, alignment, responsive layouts, persistence, and functional interactions are non-negotiable.** No element may overflow its container. No fake functionality may be presented as complete. Work phase by phase, verify the actual app, and preserve the approved visual direction throughout.
