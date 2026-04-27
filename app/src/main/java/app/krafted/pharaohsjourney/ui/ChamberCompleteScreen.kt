package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun ChamberCompleteScreen(viewModel: JourneyViewModel, navController: NavController) {
    val chamberId = navController.currentBackStackEntry?.arguments?.getInt("chamberId") ?: 1
    val uiState by viewModel.uiState.collectAsState()
    val chamber = uiState.chambers.firstOrNull { it.id == chamberId }
    val context = LocalContext.current

    if (chamber == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AncientGold)
        }
        return
    }

    val accentColor = remember(chamber.accentColor) {
        runCatching { Color(android.graphics.Color.parseColor(chamber.accentColor)) }.getOrElse { Color(0xFFFFB300) }
    }
    val darkColor = remember(chamber.darkColor) {
        runCatching { Color(android.graphics.Color.parseColor(chamber.darkColor)) }.getOrElse { Color(0xFF7A5800) }
    }

    val bgResId = context.resources.getIdentifier(chamber.backgroundDrawable, "drawable", context.packageName)
    val symbolResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
    val doorSymResId = context.resources.getIdentifier("egpt_sym_5", "drawable", context.packageName)

    var doorOpen by remember { mutableStateOf(false) }
    var showBtn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500L)
        doorOpen = true
        delay(1300L)
        showBtn = true
    }

    val doorScale by animateFloatAsState(
        targetValue = if (doorOpen) 0f else 1f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "door"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "complete")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "glow"
    )
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(1750), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.4f }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(TombBlack))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (symbolResId != 0) {
                Image(
                    painter = painterResource(symbolResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .offset(y = floatY.dp)
                        .graphicsLayer { alpha = glowAlpha }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CHAMBER COMPLETE",
                fontFamily = FontFamily.Serif,
                fontSize = 10.sp,
                letterSpacing = 5.sp,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = chamber.name,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            EgyptDivider(accentColor = accentColor)
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(190.dp)
                    .height(88.dp)
                    .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleY = doorScale
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        }
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF4A3018), Color(0xFF2E1C09)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (doorSymResId != 0) {
                        Image(
                            painter = painterResource(doorSymResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(38.dp)
                                .graphicsLayer { alpha = 0.55f },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                if (doorOpen) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PASSAGE OPEN",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.5.sp,
                            color = accentColor,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"${chamber.completeText}\"",
                fontFamily = FontFamily.Default,
                fontStyle = FontStyle.Italic,
                fontSize = 12.5.sp,
                color = Color.White.copy(alpha = 0.65f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (showBtn) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(Brush.linearGradient(listOf(accentColor, darkColor)))
                        .clickable {
                            viewModel.advanceAfterChamberComplete()
                            if (chamberId == 7) {
                                navController.navigate(Screen.Victory.route) {
                                    popUpTo(Screen.Question.createRoute(chamberId)) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.ChamberIntro.createRoute(chamberId + 1)) {
                                    popUpTo(Screen.Question.createRoute(chamberId)) { inclusive = true }
                                }
                            }
                        }
                        .padding(horizontal = 36.dp, vertical = 15.dp)
                ) {
                    Text(
                        text = if (chamberId == 7) "CLAIM VICTORY ▶" else "NEXT CHAMBER ▶",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp,
                        color = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(49.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
