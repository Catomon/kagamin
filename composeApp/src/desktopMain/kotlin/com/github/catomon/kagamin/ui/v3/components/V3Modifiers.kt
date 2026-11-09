package com.github.catomon.kagamin.ui.v3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.WindowConfig
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect

/**
 * Tsukaremi-style undecorated window frame: a soft glow, a 4dp gradient
 * border and a rounded clip around the whole app.
 */
@Composable
fun Modifier.v3WindowDecoration(): Modifier {
    val colors = V3Theme.colors
    val shape = RoundedCornerShape(8.dp)

    return if (WindowConfig.isTransparent)
        this.padding(8.dp)
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
            .v3Glow(color = Color.White, alpha = 0.75f, cornerRadius = 12.dp, shadowRadius = 4.dp)
            .clip(shape)
    else
        this.border(2.dp, colors.windowBorderStart, RectangleShape)
}

/** Skia-blurred rounded-rect glow, drawn behind the node. */
fun Modifier.v3Glow(
    color: Color = Color.White,
    alpha: Float = 0.7f,
    cornerRadius: Dp = 12.dp,
    shadowRadius: Dp = 6.dp,
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

/** Opacity applied to the playlist/tracklist panels and their entries. */
val LocalV3PanelAlpha = staticCompositionLocalOf { 1f }
val LocalV3ItemAlpha = staticCompositionLocalOf { 1f }

/** Opaque gradient panel used for the playlist and tracklist columns. */
@Composable
fun Modifier.v3Panel(shape: Shape = RoundedCornerShape(8.dp)): Modifier {
    val colors = V3Theme.colors
    val alpha = LocalV3PanelAlpha.current
    return this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(
                    colors.controlStart.copy(alpha = alpha),
                    colors.controlEnd.copy(alpha = alpha),
                )
            ),
            shape,
        )
        .border(
            width = 2.dp,
            color = colors.controlContent.copy(alpha = 0.22f),
            shape = shape,
        )
}

/** Border shown while files are dragged over a list entry. */
@Composable
fun V3ItemDropBorder(isTarget: Boolean): Modifier =
    if (isTarget)
        Modifier.border(
            width = 2.dp,
            color = V3Theme.colors.accentSoft,
            shape = RoundedCornerShape(8.dp),
        )
    else Modifier

/** Gradient list entry. v3 always paints entries; the old itemBackgroundColor setting is ignored. */
@Composable
fun Modifier.v3ItemBackground(
    selected: Boolean,
    shape: Shape = RoundedCornerShape(8.dp),
): Modifier {
    val colors = V3Theme.colors
    val alpha = LocalV3ItemAlpha.current
    val brush = if (selected)
        Brush.horizontalGradient(
            listOf(
                colors.itemSelectedStart.copy(alpha = alpha),
                colors.itemSelectedEnd.copy(alpha = alpha),
            )
        )
    else
        Brush.horizontalGradient(
            listOf(
                colors.itemStart.copy(alpha = alpha),
                colors.itemEnd.copy(alpha = alpha),
            )
        )

    return this
        .clip(shape)
        .background(brush, shape)
        .border(
            width = 1.dp,
            color = if (selected) colors.panelBorderStart else colors.itemStart,
            shape = shape,
        )
}
