package com.github.catomon.kagamin.ui2.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * ui2's text style: a stroked outline layer with the filled text stacked on top.
 * Used everywhere instead of plain Text.
 */
@Composable
fun Ui2OutlinedText(
    text: String,
    color: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    outlineSize: Float = 3f,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    color = outlineColor,
                    drawStyle = Stroke(width = outlineSize, join = StrokeJoin.Round),
                )
            ),
            fontSize = fontSize,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow,
        )
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow,
        )
    }
}
