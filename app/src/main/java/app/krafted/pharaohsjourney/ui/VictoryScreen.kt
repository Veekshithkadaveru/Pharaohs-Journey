package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun VictoryScreen(viewModel: JourneyViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val chambers = uiState.chambers

    var currentLitIndex by remember { mutableIntStateOf(-1) }
    var showFinalText by remember { mutableStateOf(false) }

    LaunchedEffect(chambers.size) {
        if (chambers.isNotEmpty()) {
            for (i in chambers.indices) {
                delay(400L)
                currentLitIndex = i
            }
            delay(600L)
            showFinalText = true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(TombBlack)) {
        val bgResId = context.resources.getIdentifier("egpt_back_2", "drawable", context.packageName)
        if (bgResId != 0) {
            Image(
                painter = painterResource(bgResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.5f }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "TOMB ESCAPED",
                style = MaterialTheme.typography.displayMedium,
                color = AncientGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Layout symbols in a grid or row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                chambers.take(4).forEachIndexed { index, chamber ->
                    val isLit = currentLitIndex >= index
                    val symResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
                    if (symResId != 0) {
                        Image(
                            painter = painterResource(symResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer { alpha = if (isLit) 1f else 0.2f }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.75f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                chambers.drop(4).forEachIndexed { index, chamber ->
                    val actualIndex = index + 4
                    val isLit = currentLitIndex >= actualIndex
                    val symResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
                    if (symResId != 0) {
                        Image(
                            painter = painterResource(symResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer { alpha = if (isLit) 1f else 0.2f }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            androidx.compose.animation.AnimatedVisibility(visible = showFinalText) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You have defied the gods and escaped the tomb with your soul intact.",
                        color = SandParchment,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Final Score: ${uiState.score}",
                        color = AncientGold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .clickable {
                                viewModel.resetJourney()
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(0) { inclusive = true }
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
                        Text("Return to Map", style = MaterialTheme.typography.titleLarge, color = SandParchment)
                    }
                }
            }
        }
    }
}
