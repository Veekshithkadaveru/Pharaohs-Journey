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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel

@Composable
fun GameOverScreen(viewModel: JourneyViewModel, navController: NavController) {
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

    val bgResId = context.resources.getIdentifier(chamber.backgroundDrawable, "drawable", context.packageName)
    val anubisResId = context.resources.getIdentifier("egpt_sym_2", "drawable", context.packageName)

    val infiniteTransition = rememberInfiniteTransition(label = "gameover")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(tween(1250), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050101))) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.16f }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.68f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (anubisResId != 0) {
                Image(
                    painter = painterResource(anubisResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(145.dp)
                        .offset(y = floatY.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FALLEN",
                fontFamily = FontFamily.Serif,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF5350),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = chamber.name,
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.55f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"The sands are not merciful.\"",
                fontFamily = FontFamily.Default,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.45f),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            EgyptDivider(accentColor = Color(0xFFEF5350))

            Text(
                text = "THE CHAMBER RESETS FROM THE BEGINNING",
                fontFamily = FontFamily.Serif,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.32f),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Box(
                modifier = Modifier
                    .border(1.5.dp, Color(0xFFEF5350).copy(alpha = 0.5f), RoundedCornerShape(11.dp))
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFFEF5350).copy(alpha = 0.12f))
                    .clickable {
                        viewModel.restartCurrentChamber()
                        navController.navigate(Screen.ChamberIntro.createRoute(chamberId)) {
                            popUpTo(Screen.GameOver.createRoute(chamberId)) { inclusive = true }
                        }
                    }
                    .padding(horizontal = 36.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TRY AGAIN ↺",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFFEF9A9A)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .border(1.5.dp, AncientGold.copy(alpha = 0.5f), RoundedCornerShape(11.dp))
                    .clip(RoundedCornerShape(11.dp))
                    .background(AncientGold.copy(alpha = 0.10f))
                    .clickable {
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                    .padding(horizontal = 36.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HOME ⌂",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = AncientGold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
