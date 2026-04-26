package app.krafted.pharaohsjourney.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.krafted.pharaohsjourney.data.model.Chamber
import app.krafted.pharaohsjourney.ui.theme.AncientGold
import app.krafted.pharaohsjourney.ui.theme.FadedHieroglyphic
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import app.krafted.pharaohsjourney.ui.theme.TombBlack
import app.krafted.pharaohsjourney.viewmodel.JourneyUiState
import app.krafted.pharaohsjourney.viewmodel.JourneyViewModel

@Composable
fun HomeScreen(viewModel: JourneyViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TombBlack)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AncientGold
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Pharaoh's Journey",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AncientGold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp)
                )
                Text(
                    text = "Best: ${uiState.bestScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FadedHieroglyphic,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.chambers) { chamber ->
                        ChamberRow(
                            chamber = chamber,
                            uiState = uiState,
                            onChamberClick = {
                                navController.navigate(Screen.ChamberIntro.createRoute(chamber.id))
                            }
                        )
                    }
                }
                LeaderboardButton(
                    onClick = { navController.navigate(Screen.Leaderboard.route) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ChamberRow(
    chamber: Chamber,
    uiState: JourneyUiState,
    onChamberClick: () -> Unit
) {
    val context = LocalContext.current
    val isLocked = chamber.id > uiState.unlockedChamberId
    val isComplete = chamber.id in uiState.completedChamberIds
    val isCurrent = chamber.id == uiState.currentChamberId && !isLocked

    val symbolResId = context.resources.getIdentifier(
        chamber.symbolDrawable, "drawable", context.packageName
    )
    val rowBgResId = context.resources.getIdentifier(
        "egpt_decor_row", "drawable", context.packageName
    )

    val rowModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .then(
            if (isCurrent && !isComplete) {
                Modifier.border(1.dp, AncientGold)
            } else Modifier
        )
        .then(
            if (!isLocked) Modifier.clickable { onChamberClick() } else Modifier
        )
        .graphicsLayer { alpha = if (isLocked) 0.4f else 1f }

    Box(modifier = rowModifier) {
        if (rowBgResId != 0) {
            Image(
                painter = painterResource(id = rowBgResId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (symbolResId != 0) {
                Image(
                    painter = painterResource(id = symbolResId),
                    contentDescription = chamber.name,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Spacer(modifier = Modifier.size(32.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = chamber.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = SandParchment
                )
                Text(
                    text = chamber.guardian,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FadedHieroglyphic
                )
            }
            when {
                isLocked -> Text(
                    text = "🔒",
                    style = MaterialTheme.typography.bodyLarge,
                    color = FadedHieroglyphic
                )
                isComplete -> Text(
                    text = "✓",
                    style = MaterialTheme.typography.titleLarge,
                    color = AncientGold
                )
                else -> Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun LeaderboardButton(onClick: () -> Unit) {
    val context = LocalContext.current
    val buttonResId = context.resources.getIdentifier(
        "egpt_decor_button", "drawable", context.packageName
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 8.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (buttonResId != 0) {
            Image(
                painter = painterResource(id = buttonResId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
        }
        Text(
            text = "Leaderboard",
            style = MaterialTheme.typography.titleLarge,
            color = SandParchment
        )
    }
}
