package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.*
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

    Box(modifier = Modifier.fillMaxSize()) {
        val bgResId = context.resources.getIdentifier(chamber.backgroundDrawable, "drawable", context.packageName)
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
                .background(Color.Black.copy(alpha = 0.55f))
        )

        var doorAnimStarted by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { delay(400L); doorAnimStarted = true }
        val doorHeight by animateFloatAsState(
            targetValue = if (doorAnimStarted) 0f else 1f,
            animationSpec = tween(durationMillis = 1200)
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(doorHeight)
                    .background(DarkSand)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Chamber Cleared!", style = MaterialTheme.typography.headlineMedium, color = AncientGold)
            Spacer(modifier = Modifier.height(16.dp))

            val infiniteTransition = rememberInfiniteTransition()
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.45f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
            )
            val symResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
            if (symResId != 0) {
                Image(
                    painter = painterResource(symResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .graphicsLayer { alpha = glowAlpha }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = chamber.completeText,
                color = SandParchment,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .clickable {
                        viewModel.advanceAfterChamberComplete()
                        navController.navigate(Screen.ChamberIntro.createRoute(chamberId + 1)) {
                            popUpTo(Screen.Question.createRoute(chamberId)) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val btnResId = context.resources.getIdentifier("egpt_decor_button", "drawable", context.packageName)
                if (btnResId != 0) {
                    Image(
                        painter = painterResource(btnResId),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                }
                Text("Advance →", style = MaterialTheme.typography.titleLarge, color = SandParchment)
            }
        }
    }
}
