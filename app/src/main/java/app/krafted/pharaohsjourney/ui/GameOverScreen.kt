package app.krafted.pharaohsjourney.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import app.krafted.pharaohsjourney.ui.theme.ChamberCrimson
import app.krafted.pharaohsjourney.ui.theme.SandParchment
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
                .background(Color.Black.copy(alpha = 0.85f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "YOU HAVE FALLEN",
                style = MaterialTheme.typography.displayMedium,
                color = ChamberCrimson,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            val symResId = context.resources.getIdentifier(chamber.symbolDrawable, "drawable", context.packageName)
            if (symResId != 0) {
                Image(
                    painter = painterResource(symResId),
                    contentDescription = null,
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "The trap has claimed your soul. The guardian ${chamber.guardian} shows no mercy.",
                color = SandParchment,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .clickable {
                        viewModel.restartCurrentChamber()
                        navController.navigate(Screen.ChamberIntro.createRoute(chamberId)) {
                            popUpTo(Screen.GameOver.createRoute(chamberId)) { inclusive = true }
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
                Text("Restart Chamber", style = MaterialTheme.typography.titleLarge, color = SandParchment)
            }
        }
    }
}
