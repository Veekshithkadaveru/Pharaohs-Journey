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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.R
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import kotlinx.coroutines.delay

private val DeepAmber  = Color(0xFFB85C00)
private val BrightGold = Color(0xFFFFD700)
private val GlowOrange = Color(0xFFFF9800)

@Composable
fun SplashScreen(navController: NavController) {
    var portraitVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        portraitVisible = true
        delay(500)
        titleVisible = true
        delay(2300)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    val portraitAlpha by animateFloatAsState(
        targetValue = if (portraitVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "portraitAlpha"
    )
    val portraitScale by animateFloatAsState(
        targetValue = if (portraitVisible) 1f else 0.82f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "portraitScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "titleAlpha"
    )
    val titleOffset by animateFloatAsState(
        targetValue = if (titleVisible) 0f else 30f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "titleOffset"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF3D1A00), Color(0xFF1A0A00), Color(0xFF3D1A00))
                )
            )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
        ) {

            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(304.dp)
                        .graphicsLayer { alpha = glowAlpha * portraitAlpha }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    BrightGold.copy(alpha = 0.55f),
                                    GlowOrange.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Image(
                    painter = painterResource(R.drawable.ic_launcher_pharaoh),
                    contentDescription = "Pharaoh",
                    modifier = Modifier
                        .size(268.dp)
                        .graphicsLayer {
                            alpha = portraitAlpha
                            scaleX = portraitScale
                            scaleY = portraitScale
                        }
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(20.dp),
                            ambientColor = BrightGold,
                            spotColor = GlowOrange
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(BrightGold, DeepAmber, BrightGold)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha
                    translationY = titleOffset
                }
            ) {
                Text(
                    text = "PHARAOH'S",
                    color = SandParchment,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    letterSpacing = 9.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "JOURNEY",
                    color = BrightGold,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 46.sp,
                    letterSpacing = 5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ancient Wisdom Awaits",
                    color = SandParchment.copy(alpha = 0.85f),
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
