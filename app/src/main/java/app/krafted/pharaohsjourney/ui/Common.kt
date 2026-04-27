package app.krafted.pharaohsjourney.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

private val hieroglyphChars = listOf(
    "𓂀", "𓃀", "𓄿", "𓅓", "𓆙", "𓇋", "𓈖", "𓉐", "𓊪", "𓋴",
    "𓌀", "𓍢", "𓎛", "𓏏", "𓐍", "𓂋", "𓊽", "𓃭", "𓅱", "𓆑"
)

private data class GlyphPos(val x: Int, val y: Int, val size: Float, val rotation: Float)

private val glyphPositions = listOf(
    GlyphPos(8, 12, 18f, -8f),
    GlyphPos(72, 5, 14f, 12f),
    GlyphPos(140, 18, 20f, -5f),
    GlyphPos(210, 8, 16f, 7f),
    GlyphPos(280, 14, 22f, -10f),
    GlyphPos(330, 3, 15f, 4f),
    GlyphPos(20, 120, 16f, 15f),
    GlyphPos(90, 95, 14f, -12f),
    GlyphPos(175, 110, 20f, 6f),
    GlyphPos(260, 100, 18f, -7f),
    GlyphPos(315, 130, 14f, 9f),
    GlyphPos(45, 250, 22f, -4f),
    GlyphPos(130, 240, 16f, 11f),
    GlyphPos(220, 265, 14f, -9f),
    GlyphPos(300, 255, 20f, 5f),
    GlyphPos(10, 370, 18f, 13f),
    GlyphPos(100, 380, 14f, -6f),
    GlyphPos(195, 360, 16f, 8f),
    GlyphPos(270, 390, 22f, -14f),
    GlyphPos(340, 375, 15f, 3f)
)

@Composable
fun HieroglyphBg() {
    val goldColor = Color(0xFFFFB300).copy(alpha = 0.05f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .pointerInput(Unit) {}
    ) {
        hieroglyphChars.forEachIndexed { idx, char ->
            val pos = glyphPositions[idx]
            Text(
                text = char,
                color = goldColor,
                fontSize = pos.size.sp,
                modifier = Modifier
                    .offset(x = pos.x.dp, y = pos.y.dp)
                    .graphicsLayer { rotationZ = pos.rotation }
            )
        }
    }
}

@Composable
fun EgyptDivider(
    accentColor: Color = Color(0xFFFFB300),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val symResId = context.resources.getIdentifier("egpt_sym_5", "drawable", context.packageName)

    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, accentColor.copy(alpha = 0.5f))
                    )
                )
        )
        if (symResId != 0) {
            Image(
                painter = painterResource(id = symResId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(18.dp)
                    .height(11.dp)
                    .graphicsLayer { alpha = 0.7f }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )
    }
}
