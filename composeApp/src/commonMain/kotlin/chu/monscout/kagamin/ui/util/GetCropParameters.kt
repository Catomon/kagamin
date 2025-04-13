package chu.monscout.kagamin.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun getCropParameters(original: ImageBitmap): Pair<IntOffset, IntSize> {
    val width = original.width
    val height = original.height

    var left = width
    var right = 0
    var top = height
    var bottom = 0

    val pixels = IntArray(width * height)
    original.readPixels(pixels)

//    val topColor = Color(pixels[width / 2 ])
    val leftColor = Color(pixels[(height / 2) * width])

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = pixels[y * width + x]
            val color = Color(pixel)
            val isHorizontalBarsColor = false //abs((color.red * 255 + color.green * 255 + color.blue * 255) - (leftColor.red * 255 + leftColor.green * 255 + leftColor.blue * 255)) < 25
            val isVerticalBarsColor = color.red * 255 + color.green * 255 + color.blue * 255 > 120

            if (isHorizontalBarsColor || isVerticalBarsColor) { //0.47f
                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }
    }

    val offset = IntOffset(left, top)
    val size = IntSize(right - left + 1, bottom - top + 1)

    return Pair(offset, size)
}