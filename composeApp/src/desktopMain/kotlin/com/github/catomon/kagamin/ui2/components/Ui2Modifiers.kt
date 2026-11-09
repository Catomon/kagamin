package com.github.catomon.kagamin.ui2.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui2.theme.LocalUi2Colors
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect

/**
 * Tsukaremi-style undecorated window frame: room for the glow, a 4dp
 * gradient border (palette start -> end), a soft glow and an 8dp rounded clip.
 */
@Composable
fun Modifier.ui2WindowDecoration(): Modifier {
    val colors = LocalUi2Colors.current
    val shape = RoundedCornerShape(8.dp)
    return this
        .padding(8.dp)
        .border(
            width = 4.dp,
            brush = Brush.verticalGradient(
                0.0f to colors.windowBorderStart,
                0.15f to colors.windowBorderStart,
                0.5f to colors.windowBorderEnd,
                1.0f to colors.windowBorderEnd,
            ),
            shape = shape,
        )
        .ui2Glow(color = colors.glow)
        .clip(shape)
}

/** Skia-blurred rounded-rect glow, drawn behind the node. */
fun Modifier.ui2Glow(
    color: Color = Color.White,
    alpha: Float = 0.75f,
    cornerRadius: Dp = 8.dp,
    shadowRadius: Dp = 4.dp,
) = drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = shadowColor
            maskFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, shadowRadius.toPx())
        }
        canvas.nativeCanvas.drawRRect(
            RRect.makeLTRB(0f, 0f, size.width, size.height, cornerRadius.toPx()),
            paint,
        )
    }
}
