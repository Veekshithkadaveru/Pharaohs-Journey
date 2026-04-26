package app.krafted.pharaohsjourney.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.data.model.QuestionType
import app.krafted.pharaohsjourney.ui.components.ScarabLives
import app.krafted.pharaohsjourney.ui.theme.FadedHieroglyphic
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.AnswerPhase
import app.krafted.pharaohsjourney.ui.TrapOverlay
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel

@Composable
fun QuestionScreen(viewModel: JourneyViewModel, navController: NavController) {
    val backStackEntry = navController.currentBackStackEntry
    val chamberId = backStackEntry?.arguments?.getInt("chamberId") ?: 1

    LaunchedEffect(chamberId) {
        viewModel.startChamber(chamberId)
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.answerPhase) {
        when (uiState.answerPhase) {
            AnswerPhase.GAME_OVER -> navController.navigate(Screen.GameOver.createRoute(uiState.currentChamberId))
            AnswerPhase.CHAMBER_COMPLETE -> navController.navigate(Screen.ChamberComplete.createRoute(uiState.currentChamberId))
            AnswerPhase.VICTORY -> navController.navigate(Screen.Victory.route)
            else -> Unit
        }
    }

    val context = LocalContext.current
    val chamber = uiState.currentChamber
    val question = uiState.currentQuestion
    val answerPhase = uiState.answerPhase
    val interactionEnabled = answerPhase == AnswerPhase.IDLE

    Box(modifier = Modifier.fillMaxSize()) {
        if (chamber != null) {
            val resId = context.resources.getIdentifier(
                chamber.backgroundDrawable, "drawable", context.packageName
            )
            if (resId != 0) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(TombBlack))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().background(TombBlack))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScarabLives(lives = uiState.lives)
                Text(
                    text = "Score: ${uiState.score}",
                    color = SandParchment,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Q${uiState.currentQuestionIndex + 1} / 5",
                color = FadedHieroglyphic,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = question?.text.orEmpty(),
                color = SandParchment,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (question != null && chamber != null) {
                val accentColor = runCatching {
                    Color(android.graphics.Color.parseColor(chamber.accentColor))
                }.getOrElse { AncientGoldFallback }

                when (question.type) {
                    QuestionType.TRUE_FALSE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("True", "False").forEach { option ->
                                OutlinedButton(
                                    onClick = { viewModel.submitAnswer(option) },
                                    enabled = interactionEnabled,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SandParchment,
                                        disabledContentColor = SandParchment.copy(alpha = 0.4f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (interactionEnabled) accentColor else accentColor.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = option)
                                }
                            }
                        }
                    }
                    QuestionType.MULTIPLE_CHOICE, QuestionType.RIDDLE -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            question.options.forEach { option ->
                                OutlinedButton(
                                    onClick = { viewModel.submitAnswer(option) },
                                    enabled = interactionEnabled,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = SandParchment,
                                        disabledContentColor = SandParchment.copy(alpha = 0.4f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (interactionEnabled) accentColor else accentColor.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = option)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (answerPhase == AnswerPhase.CORRECT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2E7D32).copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    uiState.feedbackText?.let { feedback ->
                        Text(
                            text = feedback,
                            color = SandParchment,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.continueAfterAnswer() }) {
                        Text(text = "Continue →")
                    }
                }
            }
        }
        if (answerPhase == AnswerPhase.WRONG) {
            TrapOverlay(
                trapText = uiState.trapText,
                wrongReaction = uiState.feedbackText,
                onComplete = { viewModel.continueAfterAnswer() }
            )
        }
    }
}

private val AncientGoldFallback = Color(0xFFFFB300)
