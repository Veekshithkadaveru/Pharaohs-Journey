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
        typography = PharaohsJourneyTypography,
        content = content
    )
}
