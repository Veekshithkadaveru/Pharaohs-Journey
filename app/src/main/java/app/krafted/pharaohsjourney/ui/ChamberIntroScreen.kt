package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.FadedHieroglyphic
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel
import kotlinx.coroutines.delay

@Composable
fun ChamberIntroScreen(viewModel: JourneyViewModel, navController: NavController) {
    val chamberId = navController.currentBackStackEntry?.arguments?.getInt("chamberId") ?: 1
    val uiState by viewModel.uiState.collectAsState()
    val chamber = uiState.chambers.firstOrNull { it.id == chamberId }
    val context = LocalContext.current

    if (chamber == null) {
        Box(modifier = Modifier.fillMaxSize().background(TombBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AncientGold)
        }
        return
    }

    val bgResId = context.resources.getIdentifier(chamber.backgroundDrawable, "drawable", context.packageName)

    var displayedText by remember(chamber.introText) { mutableStateOf("") }
    var typewriterDone by remember(chamber.introText) { mutableStateOf(false) }

    LaunchedEffect(chamber.introText) {
        displayedText = ""
        typewriterDone = false
        for (char in chamber.introText) {
            displayedText += char
            delay(25L)
        }
        typewriterDone = true
    }

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

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chamber.name,
                style = MaterialTheme.typography.headlineMedium,
                color = AncientGold
            )
            Text(
                text = chamber.guardian,
                style = MaterialTheme.typography.bodyLarge,
                color = FadedHieroglyphic,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = displayedText,
                style = MaterialTheme.typography.bodyLarge,
                color = SandParchment,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(visible = typewriterDone) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .clickable { navController.navigate(Screen.Question.createRoute(chamberId)) },
                    contentAlignment = Alignment.Center
                ) {
                    val btnResId = context.resources.getIdentifier("egpt_decor_button", "drawable", context.packageName)
                    if (btnResId != 0) {
                        Image(
                            painter = painterResource(btnResId),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )
                    }
                    Text("Enter Chamber →", style = MaterialTheme.typography.titleLarge, color = SandParchment)
                }
            }
        }
    }
}
