package app.krafted.pharaohsjourney.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.pharaohsjourney.data.ChamberRepository
import app.krafted.pharaohsjourney.data.db.AppDatabase
import app.krafted.pharaohsjourney.data.db.JOURNEY_PROGRESS_ID
import app.krafted.pharaohsjourney.data.db.JourneyProgress
import app.krafted.pharaohsjourney.data.db.ScoreRecord
import app.krafted.pharaohsjourney.data.model.Chamber
import app.krafted.pharaohsjourney.data.model.JourneyQuestion
import app.krafted.pharaohsjourney.data.model.QuestionType
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MAX_LIVES = 3
private const val FINAL_CHAMBER_ID = 7

enum class AnswerPhase {
    IDLE,
    CORRECT,
    WRONG,
    CHAMBER_COMPLETE,
    GAME_OVER,
    VICTORY
}

data class JourneyUiState(
    val isLoading: Boolean = true,
    val chambers: List<Chamber> = emptyList(),
    val currentChamber: Chamber? = null,
    val currentQuestion: JourneyQuestion? = null,
    val currentQuestionIndex: Int = 0,
    val lives: Int = MAX_LIVES,
    val selectedAnswer: String? = null,
    val answerPhase: AnswerPhase = AnswerPhase.IDLE,
    val feedbackText: String? = null,
    val completedChamberIds: Set<Int> = emptySet(),
    val currentChamberId: Int = 1,
    val unlockedChamberId: Int = 1,
    val score: Int = 0,
    val bestScore: Int = 0,
    val isChamberComplete: Boolean = false,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val errorMessage: String? = null
)

class JourneyViewModel @JvmOverloads constructor(
    application: Application,
    private val chamberRepository: ChamberRepository = ChamberRepository(application),
    private val appDatabase: AppDatabase = AppDatabase.getInstance(application)
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(JourneyUiState())
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    init {
        loadJourney()
    }

    fun startChamber(chamberId: Int) {
        val state = _uiState.value
        val chamber = state.chambers.firstOrNull { it.id == chamberId } ?: return
        val allowedChamberId = chamberId.coerceAtMost(state.unlockedChamberId)
        val allowedChamber = state.chambers.firstOrNull { it.id == allowedChamberId } ?: chamber
        _uiState.update {
            it.copy(
                currentChamber = allowedChamber,
                currentQuestion = allowedChamber.questions.firstOrNull(),
                currentQuestionIndex = 0,
                lives = MAX_LIVES,
                selectedAnswer = null,
                answerPhase = AnswerPhase.IDLE,
                feedbackText = null,
                currentChamberId = allowedChamber.id,
                isChamberComplete = false,
                isGameOver = false,
                isVictory = false,
                errorMessage = null
            )
        }
        persistProgress(allowedChamber.id, state.completedChamberIds)
    }

    fun submitAnswer(answer: String) {
        val state = _uiState.value
        if (state.answerPhase != AnswerPhase.IDLE) return

        val question = state.currentQuestion ?: return
        val chamber = state.currentChamber ?: return
        val isCorrect = answer == question.correctAnswer

        if (isCorrect) {
            handleCorrectAnswer(chamber, question, answer, state)
        } else {
            handleWrongAnswer(question, answer, state)
        }
    }

    fun continueAfterAnswer() {
        val state = _uiState.value
        when (state.answerPhase) {
            AnswerPhase.CORRECT -> moveToNextQuestion(state)
            AnswerPhase.WRONG -> _uiState.update {
                it.copy(
                    selectedAnswer = null,
                    answerPhase = AnswerPhase.IDLE,
                    feedbackText = null
                )
            }

            else -> Unit
        }
    }

    fun restartCurrentChamber() {
        startChamber(_uiState.value.currentChamberId)
    }

    fun advanceAfterChamberComplete() {
        val state = _uiState.value
        if (state.answerPhase != AnswerPhase.CHAMBER_COMPLETE) return
        val nextChamberId = (state.currentChamberId + 1).coerceAtMost(FINAL_CHAMBER_ID)
        startChamber(nextChamberId)
    }

    fun resetJourney() {
        viewModelScope.launch {
            appDatabase.journeyDao().clearProgress()
            val chambers = _uiState.value.chambers
            val firstChamber = chambers.firstOrNull()
            _uiState.update {
                JourneyUiState(
                    isLoading = false,
                    chambers = chambers,
                    currentChamber = firstChamber,
                    currentQuestion = firstChamber?.questions?.firstOrNull(),
                    bestScore = it.bestScore
                )
            }
        }
    }

    private fun calcPoints(qIdx: Int, lives: Int, question: JourneyQuestion): Int {
        val base = when (question.type) {
            QuestionType.RIDDLE -> 200
            QuestionType.TRUE_FALSE -> 50
            else -> 100
        }
        return (base * (1.0 + qIdx * 0.2) + lives * 25).roundToInt()
    }

    private fun loadJourney() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val chambers = chamberRepository.getChambers()
                    val progress = appDatabase.journeyDao().getProgress(JOURNEY_PROGRESS_ID)
                    val bestScore = appDatabase.scoreDao().getBestScore() ?: 0
                    Triple(chambers, progress, bestScore)
                }
            }.onSuccess { (chambers, progress, bestScore) ->
                val completedIds = progress?.completedChamberIds.orEmpty().toSet()
                val unlockedChamberId = calculateUnlockedChamberId(completedIds)
                val currentChamberId = progress?.currentChamberId
                    ?.coerceIn(1, FINAL_CHAMBER_ID)
                    ?.coerceAtMost(unlockedChamberId)
                    ?: unlockedChamberId
                val chamber = chambers.firstOrNull { it.id == currentChamberId } ?: chambers.first()
                _uiState.value = JourneyUiState(
                    isLoading = false,
                    chambers = chambers,
                    currentChamber = chamber,
                    currentQuestion = chamber.questions.firstOrNull(),
                    completedChamberIds = completedIds,
                    currentChamberId = chamber.id,
                    unlockedChamberId = unlockedChamberId,
                    bestScore = bestScore
                )
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load journey"
                    )
                }
            }
        }
    }

    private fun handleCorrectAnswer(
        chamber: Chamber,
        question: JourneyQuestion,
        answer: String,
        state: JourneyUiState
    ) {
        val newScore = state.score + calcPoints(state.currentQuestionIndex, state.lives, question)
        val isLastQuestion = state.currentQuestionIndex == chamber.questions.lastIndex

        if (isLastQuestion) {
            completeChamber(chamber, question, answer, state, newScore)
        } else {
            _uiState.update {
                it.copy(
                    selectedAnswer = answer,
                    answerPhase = AnswerPhase.CORRECT,
                    feedbackText = question.correctReaction,
                    score = newScore
                )
            }
        }
    }

    private fun handleWrongAnswer(
        question: JourneyQuestion,
        answer: String,
        state: JourneyUiState
    ) {
        val remainingLives = (state.lives - 1).coerceAtLeast(0)
        val phase = if (remainingLives == 0) AnswerPhase.GAME_OVER else AnswerPhase.WRONG
        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                answerPhase = phase,
                lives = remainingLives,
                feedbackText = question.wrongReaction,
                isGameOver = remainingLives == 0
            )
        }
    }

    private fun moveToNextQuestion(state: JourneyUiState) {
        val chamber = state.currentChamber ?: return
        val nextIndex = state.currentQuestionIndex + 1
        val nextQuestion = chamber.questions.getOrNull(nextIndex) ?: return
        _uiState.update {
            it.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = nextQuestion,
                selectedAnswer = null,
                answerPhase = AnswerPhase.IDLE,
                feedbackText = null
            )
        }
    }

    private fun completeChamber(
        chamber: Chamber,
        question: JourneyQuestion,
        answer: String,
        state: JourneyUiState,
        newScore: Int
    ) {
        val completedIds = (state.completedChamberIds + chamber.id).toSortedSet()
        val isVictory = chamber.id == FINAL_CHAMBER_ID
        val persistedCurrentChamberId = if (isVictory) FINAL_CHAMBER_ID else chamber.id + 1
        val phase = if (isVictory) AnswerPhase.VICTORY else AnswerPhase.CHAMBER_COMPLETE

        _uiState.update {
            it.copy(
                selectedAnswer = answer,
                answerPhase = phase,
                feedbackText = question.correctReaction,
                completedChamberIds = completedIds,
                currentChamberId = chamber.id,
                unlockedChamberId = calculateUnlockedChamberId(completedIds),
                score = newScore,
                bestScore = if (isVictory) maxOf(it.bestScore, newScore) else it.bestScore,
                isChamberComplete = !isVictory,
                isVictory = isVictory
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            saveProgress(persistedCurrentChamberId, completedIds)
            if (isVictory) {
                appDatabase.scoreDao().insertScore(ScoreRecord(score = newScore))
            }
        }
    }

    private fun calculateUnlockedChamberId(completedChamberIds: Set<Int>): Int {
        val nextChamberId = (completedChamberIds.maxOrNull() ?: 0) + 1
        return nextChamberId.coerceIn(1, FINAL_CHAMBER_ID)
    }

    private fun persistProgress(currentChamberId: Int, completedChamberIds: Set<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            saveProgress(currentChamberId, completedChamberIds)
        }
    }

    private suspend fun saveProgress(currentChamberId: Int, completedChamberIds: Set<Int>) {
        appDatabase.journeyDao().upsertProgress(
            JourneyProgress(
                currentChamberId = currentChamberId,
                completedChamberIds = completedChamberIds.toList()
            )
        )
    }
}
