package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun VictoryScreen(viewModel: JourneyViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val chambers = uiState.chambers
    val context = LocalContext.current

    var litCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(chambers.size) {
        if (chambers.isNotEmpty()) {
            chambers.forEachIndexed { i, _ ->
                delay(210L)
                litCount = i + 1
            }
        }
    }

    val bgResId = context.resources.getIdentifier("egpt_back_2", "drawable", context.packageName)
    val guardianResId = context.resources.getIdentifier("egpt_sym_7", "drawable", context.packageName)

    val infiniteTransition = rememberInfiniteTransition(label = "victory")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(1250), RepeatMode.Reverse),
        label = "float"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(750), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = Modifier.fillMaxSize().background(TombBlack)) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.28f }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.6f), Color(0xFF040301).copy(alpha = 0.88f))
                    )
                )
        )

        HieroglyphBg()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "YOU ESCAPED THE TOMB",
                fontFamily = FontFamily.Serif,
                fontSize = 8.5.sp,
                letterSpacing = 6.sp,
                color = Color(0xFFFFB300).copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "VICTORY!",
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp,
                lineHeight = 34.sp
            )

            if (guardianResId != 0) {
                Image(
                    painter = painterResource(guardianResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(115.dp)
                        .offset(y = floatY.dp)
                        .graphicsLayer { alpha = glowAlpha }
                        .padding(vertical = 10.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(115.dp))
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    chambers.forEachIndexed { index, chamber ->
                        val isLit = litCount > index
                        val chamberAccent = remember(chamber.accentColor) {
                            runCatching { Color(android.graphics.Color.parseColor(chamber.accentColor)) }
                                .getOrElse { Color(0xFFFFB300) }
                        }
                        val symResId = context.resources.getIdentifier(
                            chamber.symbolDrawable, "drawable", context.packageName
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isLit) chamberAccent.copy(alpha = 0.125f)
                                    else Color.White.copy(alpha = 0.04f)
                                )
                                .border(
                                    1.5.dp,
                                    if (isLit) chamberAccent.copy(alpha = 0.44f) else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .graphicsLayer { alpha = if (isLit) 1f else 0.22f },
                            contentAlignment = Alignment.Center
                        ) {
                            if (symResId != 0) {
                                Image(
                                    painter = painterResource(symResId),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            EgyptDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "FINAL SCORE",
                fontFamily = FontFamily.Serif,
                fontSize = 9.5.sp,
                color = Color.White.copy(alpha = 0.45f),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "%,d".format(uiState.score),
                fontFamily = FontFamily.Serif,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB300),
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\"The pharaohs rest peacefully. You have proven worthy of the journey.\"",
                fontFamily = FontFamily.Default,
                fontStyle = FontStyle.Italic,
                fontSize = 12.5.sp,
                color = Color.White.copy(alpha = 0.5f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, AncientGold.copy(alpha = 0.5f), RoundedCornerShape(13.dp))
                    .clip(RoundedCornerShape(13.dp))
                    .background(AncientGold.copy(alpha = 0.08f))
                    .clickable {
                        viewModel.resetJourney()
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HOME ⌂",
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = AncientGold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
