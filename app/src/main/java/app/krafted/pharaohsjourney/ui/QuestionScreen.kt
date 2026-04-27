package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.data.model.Chamber
import app.krafted.pharaohsjourney.data.model.JourneyQuestion
import app.krafted.pharaohsjourney.data.model.QuestionType
import app.krafted.pharaohsjourney.ui.components.ScarabLives
import app.krafted.pharaohsjourney.viewmodel.AnswerPhase
import app.krafted.pharaohsjourney.viewmodel.JourneyUiState
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun QuestionScreen(viewModel: JourneyViewModel, navController: NavController) {
    val backStackEntry = navController.currentBackStackEntry
    val chamberId = backStackEntry?.arguments?.getInt("chamberId") ?: 1

    var hasStarted by rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(chamberId) {
        if (!hasStarted) {
            viewModel.startChamber(chamberId)
            hasStarted = true
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val answerPhase = uiState.answerPhase

    LaunchedEffect(answerPhase) {
        when (answerPhase) {
            AnswerPhase.CORRECT -> {
                delay(850L)
                viewModel.continueAfterAnswer()
            }
            AnswerPhase.WRONG -> {
                delay(1300L)
                navController.navigate(Screen.Trap.createRoute(chamberId))
            }
            AnswerPhase.GAME_OVER -> {
                delay(800L)
                navController.navigate(Screen.GameOver.createRoute(uiState.currentChamberId))
            }
            AnswerPhase.CHAMBER_COMPLETE -> {
                delay(850L)
                navController.navigate(Screen.ChamberComplete.createRoute(uiState.currentChamberId))
            }
            AnswerPhase.VICTORY -> {
                delay(850L)
                navController.navigate(Screen.Victory.route)
            }
            else -> Unit
        }
    }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(answerPhase) {
        if (answerPhase == AnswerPhase.WRONG || answerPhase == AnswerPhase.GAME_OVER) {
            for (v in listOf(-14f, 14f, -11f, 11f, -7f, 7f, -4f, 4f, 0f)) {
                shakeOffset.animateTo(v, tween(50))
            }
        }
    }

    val context = LocalContext.current
    val chamber = uiState.currentChamber
    val question = uiState.currentQuestion
    val qIdx = uiState.currentQuestionIndex

    val accentColor = remember(chamber?.accentColor) {
        runCatching { Color(android.graphics.Color.parseColor(chamber?.accentColor ?: "")) }
            .getOrElse { Color(0xFFFFB300) }
    }

    val isCorrectPhase = answerPhase == AnswerPhase.CORRECT || answerPhase == AnswerPhase.CHAMBER_COMPLETE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = shakeOffset.value.dp)
    ) {
        if (chamber != null) {
            val bgResName = "back_${(qIdx % 5) + 1}"
            val bgResId = context.resources.getIdentifier(
                bgResName, "drawable", context.packageName
            )
            if (bgResId != 0) {
                Image(
                    painter = painterResource(id = bgResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0703)))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0703)))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
        )

        if (answerPhase == AnswerPhase.WRONG || answerPhase == AnswerPhase.GAME_OVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFC80000).copy(alpha = 0.28f))
            )
        }
        if (isCorrectPhase && uiState.feedbackText != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFB300).copy(alpha = 0.14f))
            )
        }

        if (chamber != null && question != null) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                QuestionHeader(
                    chamber = chamber,
                    qIdx = qIdx,
                    uiState = uiState,
                    accentColor = accentColor
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    val guardianResId = context.resources.getIdentifier(
                        chamber.guardianDrawable, "drawable", context.packageName
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            DifficultyBadge(difficulty = question.difficulty)
                            if (question.type == QuestionType.RIDDLE || question.type == QuestionType.TRUE_FALSE) {
                                TypeBadge(type = question.type)
                            }
                        }
                        if (guardianResId != 0) {
                            Image(
                                painter = painterResource(id = guardianResId),
                                contentDescription = null,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F0B07).copy(alpha = 0.94f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        Text(
                            text = question.text,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f),
                                lineHeight = 26.sp,
                                textAlign = TextAlign.Center,
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(0f, 2f),
                                    blurRadius = 4f
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    val labels = listOf("A", "B", "C", "D")
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        question.options.chunked(2).forEach { rowOpts ->
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                rowOpts.forEach { opt ->
                                    val optIdx = question.options.indexOf(opt)
                                    AnswerButton(
                                        label = labels.getOrElse(optIdx) { "" },
                                        text = opt,
                                        question = question,
                                        selectedAnswer = uiState.selectedAnswer,
                                        answerPhase = answerPhase,
                                        accentColor = accentColor,
                                        onClick = {
                                            if (answerPhase == AnswerPhase.IDLE) {
                                                viewModel.submitAnswer(opt)
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowOpts.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = answerPhase != AnswerPhase.IDLE,
                        enter = slideInVertically { it }
                    ) {
                        val reactionBg = if (isCorrectPhase) {
                            Color(0xFF4CAF50).copy(alpha = 0.13f)
                        } else {
                            Color(0xFFEF5350).copy(alpha = 0.13f)
                        }
                        val reactionBorder = if (isCorrectPhase) {
                            Color(0xFF4CAF50).copy(alpha = 0.35f)
                        } else {
                            Color(0xFFEF5350).copy(alpha = 0.35f)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, reactionBorder, RoundedCornerShape(10.dp))
                                .background(reactionBg, RoundedCornerShape(10.dp))
                                .padding(vertical = 10.dp, horizontal = 13.dp)
                        ) {
                            Text(
                                text = uiState.feedbackText.orEmpty(),
                                fontFamily = FontFamily.Default,
                                fontSize = 11.5.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.White.copy(alpha = 0.82f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun QuestionHeader(
    chamber: Chamber,
    qIdx: Int,
    uiState: JourneyUiState,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 44.dp, start = 15.dp, end = 15.dp, bottom = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CHAMBER ${chamber.id}",
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor.copy(alpha = 0.95f),
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
                Text(
                    text = chamber.name,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f),
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }
            Text(
                text = "Q${qIdx + 1} / 5",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFB300),
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until 5) {
                val barColor = when {
                    i < qIdx -> accentColor
                    i == qIdx -> Color.White
                    else -> Color.White.copy(alpha = 0.18f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(barColor, RoundedCornerShape(2.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScarabLives(lives = uiState.lives)
            Text(
                text = "⭐ ${"%,d".format(uiState.score)}",
                fontFamily = FontFamily.Serif,
                fontSize = 11.5.sp,
                color = Color(0xFFFFB300)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val diffColor = when (difficulty.uppercase()) {
        "EASY" -> Color(0xFF66BB6A)
        "MEDIUM" -> Color(0xFFFFA726)
        "HARD" -> Color(0xFFEF5350)
        else -> Color(0xFFFFA726)
    }
    Box(
        modifier = Modifier
            .border(1.dp, diffColor.copy(alpha = 0.33f), RoundedCornerShape(20.dp))
            .background(diffColor.copy(alpha = 0.125f), RoundedCornerShape(20.dp))
            .padding(vertical = 3.dp, horizontal = 9.dp)
    ) {
        Text(
            text = difficulty.uppercase(),
            fontFamily = FontFamily.Serif,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = diffColor
        )
    }
}

@Composable
private fun TypeBadge(type: QuestionType) {
    val label: String
    val textColor: Color
    val bgColor: Color
    val borderColor: Color

    when (type) {
        QuestionType.RIDDLE -> {
            label = "RIDDLE"
            textColor = Color(0xFFCE93D8)
            bgColor = Color(0xFF9C27B0).copy(alpha = 0.18f)
            borderColor = Color(0xFFCE93D8).copy(alpha = 0.4f)
        }
        QuestionType.TRUE_FALSE -> {
            label = "TRUE/FALSE"
            textColor = Color(0xFF90CAF9)
            bgColor = Color(0xFF2196F3).copy(alpha = 0.18f)
            borderColor = Color(0xFF90CAF9).copy(alpha = 0.4f)
        }
        else -> return
    }

    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(vertical = 3.dp, horizontal = 9.dp)
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = textColor
        )
    }
}

@Composable
private fun AnswerButton(
    label: String,
    text: String,
    question: JourneyQuestion,
    selectedAnswer: String?,
    answerPhase: AnswerPhase,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnswered = selectedAnswer != null && answerPhase != AnswerPhase.IDLE
    val isCorrectAnswer = text == question.correctAnswer
    val isSelected = text == selectedAnswer

    val (bgColor, borderColor, textColor) = when {
        isAnswered && isCorrectAnswer && isSelected -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.65f),
            Color(0xFF81C784),
            Color(0xFFE8F5E9)
        )
        isAnswered && isSelected && !isCorrectAnswer -> Triple(
            Color(0xFFE53935).copy(alpha = 0.75f),
            Color(0xFFEF5350),
            Color(0xFFFFEBEE)
        )
        else -> Triple(
            Color(0xFF140E0A).copy(alpha = 0.88f),
            Color.White.copy(alpha = 0.18f),
            Color.White.copy(alpha = 0.95f)
        )
    }

    Box(
        modifier = modifier
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .background(bgColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(1.dp, accentColor.copy(alpha = 0.31f), RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.13f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
