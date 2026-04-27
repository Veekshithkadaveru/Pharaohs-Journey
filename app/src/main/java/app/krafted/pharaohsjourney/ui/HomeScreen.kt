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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.data.model.Chamber
import app.krafted.pharaohsjourney.viewmodel.JourneyUiState
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel

private val HomeTextShadow = Shadow(
    color = Color.Black.copy(alpha = 0.85f),
    offset = Offset(1.5f, 1.5f),
    blurRadius = 4f
)

@Composable
fun HomeScreen(viewModel: JourneyViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) contentVisible = true
    }
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "homeContentAlpha"
    )
    val contentOffsetY by animateFloatAsState(
        targetValue = if (contentVisible) 0f else 18f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "homeContentOffsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0905), Color(0xFF0A0703))
                )
            )
    ) {
        HieroglyphBg()

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(2f),
                color = Color(0xFFFFB300)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = contentAlpha
                        translationY = contentOffsetY
                    }
                    .zIndex(2f)
            ) {
                HomeHeader(uiState = uiState)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 4.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    itemsIndexed(uiState.chambers) { idx, chamber ->
                        ChamberCard(
                            chamber = chamber,
                            idx = idx,
                            uiState = uiState,
                            onClick = {
                                navController.navigate(Screen.ChamberIntro.createRoute(chamber.id))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                BottomCta(uiState = uiState, navController = navController)
            }
        }
    }
}

@Composable
private fun HomeHeader(uiState: JourneyUiState) {
    val context = LocalContext.current
    val sym7ResId = context.resources.getIdentifier("egpt_sym_7", "drawable", context.packageName)
    val sym5ResId = context.resources.getIdentifier("egpt_sym_5", "drawable", context.packageName)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFB300).copy(alpha = 0.12f),
                        Color.Transparent
                    )
                )
            )
            .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
    ) {
        if (sym7ResId != 0) {
            Image(
                painter = painterResource(id = sym7ResId),
                contentDescription = null,
                modifier = Modifier
                    .size(58.dp)
                    .align(Alignment.TopEnd)
                    .graphicsLayer { alpha = 0.55f }
            )
        }
        if (sym5ResId != 0) {
            Image(
                painter = painterResource(id = sym5ResId),
                contentDescription = null,
                modifier = Modifier
                    .width(40.dp)
                    .height(24.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer { alpha = 0.45f }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "THE TOMB AWAITS",
                fontFamily = FontFamily.Serif,
                fontSize = 9.5.sp,
                letterSpacing = 5.5.sp,
                color = Color(0xFFFFC533).copy(alpha = 0.9f),
                style = TextStyle(shadow = HomeTextShadow)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "PHARAOH'S JOURNEY",
                fontFamily = FontFamily.Serif,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.95f),
                        offset = Offset(2f, 2f),
                        blurRadius = 5f
                    )
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            EgyptDivider(accentColor = Color(0xFFFFB300))
            if (uiState.bestScore > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Best Score: ${uiState.bestScore}",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.5.sp,
                    color = Color(0xFFFFC533).copy(alpha = 0.9f),
                    style = TextStyle(shadow = HomeTextShadow)
                )
            }
        }
    }
}

@Composable
private fun ChamberCard(
    chamber: Chamber,
    idx: Int,
    uiState: JourneyUiState,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isDone = chamber.id in uiState.completedChamberIds
    val isCurrent = !isDone && chamber.id == uiState.unlockedChamberId
    val isLocked = !isDone && !isCurrent

    val accentColor = remember(chamber.accentColor) {
        runCatching { Color(android.graphics.Color.parseColor(chamber.accentColor)) }
            .getOrElse { Color(0xFFFFB300) }
    }

    val symbolResId = context.resources.getIdentifier(
        chamber.symbolDrawable, "drawable", context.packageName
    )

    val borderColor = when {
        isDone -> accentColor.copy(alpha = 0.31f)
        isCurrent -> accentColor.copy(alpha = 0.56f)
        else -> Color.White.copy(alpha = 0.07f)
    }
    val bgColor = when {
        isDone -> accentColor.copy(alpha = 0.05f)
        isCurrent -> accentColor.copy(alpha = 0.094f)
        else -> Color.White.copy(alpha = 0.012f)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "chamberPulse_${chamber.id}")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.48f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha_${chamber.id}"
    )
    val symbolFloat by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "symbolFloat_${chamber.id}"
    )
    val bottomGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bottomGlowAlpha_${chamber.id}"
    )
    var cardVisible by remember(chamber.id) { mutableStateOf(false) }
    LaunchedEffect(chamber.id) {
        cardVisible = true
    }
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 520,
            delayMillis = (idx * 60).coerceAtMost(360),
            easing = FastOutSlowInEasing
        ),
        label = "cardAlpha_${chamber.id}"
    )
    val cardOffsetY by animateFloatAsState(
        targetValue = if (cardVisible) 0f else 10f,
        animationSpec = tween(
            durationMillis = 520,
            delayMillis = (idx * 60).coerceAtMost(360),
            easing = FastOutSlowInEasing
        ),
        label = "cardOffsetY_${chamber.id}"
    )
    val animatedBorderColor = if (isCurrent) accentColor.copy(alpha = pulseAlpha) else borderColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = cardAlpha * if (isLocked) 0.5f else 1f
                translationY = cardOffsetY
            }
            .border(1.dp, animatedBorderColor, RoundedCornerShape(14.dp))
            .background(bgColor, RoundedCornerShape(14.dp))
            .then(
                if (!isLocked) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(1.dp, accentColor.copy(alpha = 0.27f), RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (symbolResId != 0) {
                    Image(
                        painter = painterResource(id = symbolResId),
                        contentDescription = chamber.name,
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                alpha = if (isLocked) 0.55f else 1f
                                translationY = if (isCurrent) symbolFloat else 0f
                            }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CHAMBER ${chamber.id}",
                    fontFamily = FontFamily.Serif,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 1.sp,
                    style = TextStyle(shadow = HomeTextShadow)
                )
                Text(
                    text = chamber.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(shadow = HomeTextShadow)
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${chamber.guardian} · ${chamber.subtitle}",
                    fontSize = 11.5.sp,
                    color = Color.White.copy(alpha = if (isLocked) 0.55f else 0.68f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(shadow = HomeTextShadow)
                )
            }

            when {
                isDone -> Text(
                    text = "✓",
                    fontSize = 22.sp,
                    color = Color(0xFF7BE27F),
                    style = TextStyle(shadow = HomeTextShadow)
                )
                isCurrent -> Text(
                    text = "ENTER ▶",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    style = TextStyle(shadow = HomeTextShadow)
                )
                isLocked -> Text(
                    text = "🔒",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.55f),
                    style = TextStyle(shadow = HomeTextShadow)
                )
            }
        }

        if (isCurrent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, Color.Transparent)
                        )
                    )
                    .graphicsLayer { alpha = bottomGlowAlpha }
            )
        }
    }
}

@Composable
private fun BottomCta(uiState: JourneyUiState, navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "bottomCta")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScale"
    )
    val buttonLabel = when {
        uiState.completedChamberIds.isEmpty() -> "BEGIN THE JOURNEY"
        uiState.completedChamberIds.size >= 7 -> "JOURNEY AGAIN ↺"
        else -> "CONTINUE JOURNEY ▶"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
    ) {
        Button(
            onClick = {
                navController.navigate(Screen.ChamberIntro.createRoute(uiState.unlockedChamberId))
            },
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFB300), Color(0xFFE65C00))
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(17.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonLabel,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.72f),
                            offset = Offset(1.5f, 1.5f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }
    }
}
