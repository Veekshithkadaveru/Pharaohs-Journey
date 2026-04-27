package app.krafted.pharaohsjourney.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.krafted.pharaohsjourney.R

@Composable
fun ScarabLives(lives: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (index in 0..2) {
            if (index > 0) {
                Spacer(modifier = Modifier.width(4.dp))
            }
            Crossfade(targetState = index < lives) { isAlive ->
                if (isAlive) {
                    Image(
                        painter = painterResource(R.drawable.egpt_life_scarab),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.egpt_life_scarab_cracked),
                        contentDescription = null,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.colorMatrix(
                            androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
                        ),
                        alpha = 0.35f,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
