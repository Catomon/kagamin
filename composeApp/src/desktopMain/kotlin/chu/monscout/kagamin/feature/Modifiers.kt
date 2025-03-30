package chu.monscout.kagamin.feature

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

fun Modifier.customShadow(
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