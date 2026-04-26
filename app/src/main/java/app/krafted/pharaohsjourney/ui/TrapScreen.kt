package app.krafted.pharaohsjourney.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.krafted.pharaohsjourney.ui.theme.SandParchment
import kotlinx.coroutines.delay

@Composable
fun TrapOverlay(trapText: String?, wrongReaction: String?, onComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500L)
        onComplete()
    }

    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        shakeAnim.animateTo(18f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
        shakeAnim.animateTo(-14f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        shakeAnim.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = shakeAnim.value.dp)
            .background(Color(0xFFC62828).copy(alpha = 0.78f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            if (trapText != null) {
                Text(
                    text = trapText,
                    color = SandParchment,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (wrongReaction != null) {
                Text(
                    text = wrongReaction,
                    color = SandParchment,
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
