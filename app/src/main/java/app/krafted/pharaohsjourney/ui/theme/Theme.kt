package app.krafted.pharaohsjourney.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EgyptianDarkScheme = darkColorScheme(
    primary = AncientGold,
    onPrimary = TombBlack,
    background = TombBlack,
    onBackground = SandParchment,
    surface = DarkSand,
    onSurface = SandParchment,
    secondary = FadedHieroglyphic,
    onSecondary = SandParchment,
    error = ChamberCrimson
)

@Composable
fun PharaohsJourneyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EgyptianDarkScheme,
        typography = PharaohsJourneyTypography
    ) {
        androidx.compose.material3.ProvideTextStyle(
            value = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.85f),
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            content = content
        )
    }
}
