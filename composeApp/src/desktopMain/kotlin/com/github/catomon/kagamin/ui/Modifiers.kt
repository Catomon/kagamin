package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter

@Composable
internal fun InitialFadeIn(durationMs: Int = 500, content: @Composable() AnimatedVisibilityScope.() -> Unit) {
    var visibility by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit, block = { visibility = true })
    AnimatedVisibility(visible = visibility, enter = fadeIn(tween(durationMs)), content = content)
}

internal fun Modifier.customShadow(
    color: Color = Color.Black,
    alpha: Float = 0.75f,
    cornerRadius: Dp = 12.dp,
    shadowRadius: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp
) = drawBehind {
    val shadowColor = color.copy(alpha = alpha).toArgb()

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                this.color = shadowColor
                maskFilter = MaskFilter.makeBlur(
                    FilterBlurMode.NORMAL,
                    shadowRadius.toPx()
                )
            }
        }

        canvas.drawRoundRect(
            left = offsetX.toPx(),
            top = offsetY.toPx(),
            right = size.width + offsetX.toPx(),
            bottom = size.height + offsetY.toPx(),
            cornerRadius.toPx(),
            cornerRadius.toPx(),
            paint
        )
    }
}