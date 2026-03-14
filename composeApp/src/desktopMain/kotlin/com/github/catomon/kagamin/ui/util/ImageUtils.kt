package com.github.catomon.kagamin.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.IRect

fun ImageBitmap.removeBlackBars(): ImageBitmap {
    val w = width
    val h = height
    val pixels = IntArray(w * h)
    readPixels(pixels)

    fun averageColor(y: Int, xStart: Int, count: Int): Color {
        var r = 0f; var g = 0f; var b = 0f
        repeat(count.coerceAtMost(w)) { i ->
            val px = Color(pixels[y * w + (xStart + i).coerceAtMost(w - 1)])
            r += px.red; g += px.green; b += px.blue
        }
        return Color(r / count, g / count, b / count, 1f)
    }

    val sampleCount = 5
    val topColor = averageColor(0, w / 2 - sampleCount / 2, sampleCount)
    val bottomColor = averageColor(h - 1, w / 2 - sampleCount / 2, sampleCount)
    val leftColor = averageColor(h / 2 - sampleCount / 2, 0, sampleCount)
    val rightColor = averageColor(h / 2 - sampleCount / 2, w - sampleCount, sampleCount)

    fun isBarColor(c: Color, ref: Color): Boolean {
        val dr = (c.red - ref.red) * 255f
        val dg = (c.green - ref.green) * 255f
        val db = (c.blue - ref.blue) * 255f
        return dr * dr + dg * dg + db * db < 100f * 100f
    }

    var topCrop = 0
    while (topCrop < h && (0 until w).all { x -> isBarColor(Color(pixels[topCrop * w + x]), topColor) }) {
        topCrop++
    }

    var bottomCrop = h - 1
    while (bottomCrop > topCrop && (0 until w).all { x -> isBarColor(Color(pixels[bottomCrop * w + x]), bottomColor) }) {
        bottomCrop--
    }

    var leftCrop = 0
    while (leftCrop < w && (topCrop..bottomCrop).all { y -> isBarColor(Color(pixels[y * w + leftCrop]), leftColor) }) {
        leftCrop++
    }

    var rightCrop = w - 1
    while (rightCrop > leftCrop && (topCrop..bottomCrop).all { y -> isBarColor(Color(pixels[y * w + rightCrop]), rightColor) }) {
        rightCrop--
    }

    if (leftCrop >= w || topCrop >= h || rightCrop < 0 || bottomCrop < 0) {
        return this
    }

    val offset = IntOffset(leftCrop, topCrop)
    val size = IntSize(rightCrop - leftCrop + 1, bottomCrop - topCrop + 1)
    return cropped(offset, size)
}


fun ImageBitmap.cropped(offset: IntOffset, size: IntSize): ImageBitmap {
    val srcBitmap = this.asSkiaBitmap()

    val dstBitmap = Bitmap()
    dstBitmap.allocN32Pixels(size.width, size.height)

    val subset = IRect.makeXYWH(offset.x, offset.y, size.width, size.height)

    val success = srcBitmap.extractSubset(dstBitmap, subset)

    require(success) { "Failed to extract subset from bitmap" }

    return dstBitmap.asComposeImageBitmap()
}