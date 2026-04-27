package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import app.krafted.pharaohsjourney.ui.components.ScarabLives
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun TrapScreen(viewModel: JourneyViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val lives = uiState.lives
    val context = LocalContext.current

    val trapMessages = remember {
        listOf(
            "The floor crumbles beneath you!",
            "A poisoned dart flies from the wall!",
            "Sand begins to fill the chamber!",
            "The torches extinguish around you!"
        )
    }
    val message = remember { trapMessages.random() }

    LaunchedEffect(Unit) {
        delay(2600L)
        viewModel.continueAfterAnswer()
        navController.popBackStack()
    }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        for (v in listOf(-14f, 14f, -11f, 11f, -7f, 7f, -4f, 4f, 0f)) {
            shakeOffset.animateTo(v, tween(50))
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "float"
    )

    val symResId = context.resources.getIdentifier("egpt_sym_4", "drawable", context.packageName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(listOf(Color(0xFF180202), Color(0xFF0A0101)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(28.dp)
        ) {
            if (symResId != 0) {
                Image(
                    painter = painterResource(symResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(150.dp)
                        .offset(x = shakeOffset.value.dp, y = floatY.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "TRAP!",
                fontFamily = FontFamily.Serif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF5350),
                letterSpacing = 3.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                fontFamily = FontFamily.Default,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
                modifier = Modifier.padding(bottom = 22.dp)
            )
            ScarabLives(lives = lives)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (lives > 0) "${lives} ${if (lives == 1) "LIFE" else "LIVES"} REMAINING" else "ALL LIVES LOST",
                fontFamily = FontFamily.Serif,
                fontSize = 10.5.sp,
                letterSpacing = 2.5.sp,
                color = if (lives > 0) Color(0xFFFFB300).copy(alpha = 0.65f) else Color(0xFFEF5350)
            )
        }
    }
}
