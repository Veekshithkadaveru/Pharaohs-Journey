package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun ChamberIntroScreen(viewModel: JourneyViewModel, navController: NavController) {
    val chamberId = navController.currentBackStackEntry?.arguments?.getInt("chamberId") ?: 1
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val chamber = uiState.chambers.firstOrNull { it.id == chamberId }

    if (chamber == null) {
        Box(modifier = Modifier.fillMaxSize().background(TombBlack), contentAlignment = Alignment.Center) {
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
    val speakerLabelColor = remember(accentColor) { accentTowardWhite(accentColor, 0.32f) }
    val titleShadow = remember {
        Shadow(
            color = Color.Black.copy(alpha = 0.55f),
            offset = Offset(0f, 2f),
            blurRadius = 12f
        )
    }

    val bgResName = "back_${((chamber.id - 1) % 5) + 1}"
    val bgResId = context.resources.getIdentifier(bgResName, "drawable", context.packageName)
    val symbolResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
    val guardianResId = context.resources.getIdentifier(chamber.guardianDrawable, "drawable", context.packageName)

    var displayedText by remember(chamber.introText) { mutableStateOf("") }
    var typewriterDone by remember(chamber.introText) { mutableStateOf(false) }
    var showButton by remember(chamber.introText) { mutableStateOf(false) }

    LaunchedEffect(chamber.introText) {
        displayedText = ""
        typewriterDone = false
        showButton = false
        for (char in chamber.introText) {
            displayedText += char
            delay(28L)
        }
        typewriterDone = true
        delay(500L)
        showButton = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "intro")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(1750), RepeatMode.Reverse),
        label = "float"
    )
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "cursor"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(TombBlack))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.18f),
                            Color.Black.copy(alpha = 0.48f),
                            Color.Black.copy(alpha = 0.82f),
                            Color.Black.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = chamber.name,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = 32.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFFBF5),
                            shadow = titleShadow
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .zIndex(2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (guardianResId != 0) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(y = floatOffset.dp)) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(accentColor.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )
                        )
                        Image(
                            painter = painterResource(guardianResId),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxHeight(0.64f)
                        )
                    }
                }
            }

            val cardShape = RoundedCornerShape(24.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                accentColor.copy(alpha = 0.55f),
                                accentColor.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        shape = cardShape
                    )
                    .clip(cardShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1A1510).copy(alpha = 0.97f),
                                Color(0xFF0C0906).copy(alpha = 0.99f)
                            )
                        )
                    )
            ) {
                val progressChipShape = RoundedCornerShape(999.dp)
                val progressPillColor = Color(0xFF120E0A).copy(alpha = 0.92f)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(progressChipShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            progressPillColor,
                                            Color(0xFF0A0806).copy(alpha = 0.96f)
                                        )
                                    )
                                )
                                .border(1.5.dp, accentColor.copy(alpha = 0.5f), progressChipShape)
                                .padding(horizontal = 20.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(1.dp)
                                    .width(22.dp)
                                    .background(Brush.horizontalGradient(listOf(Color.Transparent, SandParchment.copy(alpha = 0.55f))))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "CHAMBER ${chamber.id} OF 7",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    letterSpacing = 2.4.sp,
                                    color = Color(0xFFFFFBF0),
                                    fontWeight = FontWeight.SemiBold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.75f),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 0f
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .height(1.dp)
                                    .width(22.dp)
                                    .background(Brush.horizontalGradient(listOf(SandParchment.copy(alpha = 0.55f), Color.Transparent)))
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        if (symbolResId != 0) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                accentColor.copy(alpha = 0.22f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(symbolResId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "${chamber.guardian} speaks",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 17.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = speakerLabelColor,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.45f),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 4f
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = chamber.subtitle,
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = SandParchment.copy(alpha = 0.88f),
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }

                    EgyptDivider(
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    val bodyColor = Color(0xFFF2EBE0)
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (typewriterDone) chamber.introText else displayedText,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 17.sp,
                                lineHeight = 27.sp,
                                fontStyle = FontStyle.Italic,
                                color = bodyColor,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (!typewriterDone) {
                            Text(
                                text = "▍",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Thin,
                                    color = bodyColor.copy(alpha = cursorAlpha)
                                ),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showButton,
                        enter = slideInVertically(initialOffsetY = { it }),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonShape = RoundedCornerShape(16.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 22.dp)
                                .clip(buttonShape)
                                .background(Brush.verticalGradient(listOf(accentColor, darkColor)))
                                .border(1.dp, Color(0xFFFFE082).copy(alpha = 0.45f), buttonShape)
                                .clickable { navController.navigate(Screen.Question.createRoute(chamberId)) }
                                .padding(vertical = 18.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "FACE THE CHAMBER",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.2.sp,
                                        color = Color.White,
                                        shadow = Shadow(
                                            color = Color.Black.copy(alpha = 0.35f),
                                            offset = Offset(0f, 1f),
                                            blurRadius = 2f
                                        )
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "▶",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.95f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun accentTowardWhite(base: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = base.red * (1f - f) + f,
        green = base.green * (1f - f) + f,
        blue = base.blue * (1f - f) + f,
        alpha = base.alpha
    )
}
