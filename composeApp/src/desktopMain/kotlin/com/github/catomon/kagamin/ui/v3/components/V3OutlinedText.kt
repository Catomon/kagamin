package com.github.catomon.kagamin.ui.v3.components

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Tsukaremi's signature text: a stroked outline layer with the fill on top.
 * Used for every label so text stays readable over gradients and thumbnails.
 */
@Composable
fun V3OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fillColor: Color = Color.White,
    outlineColor: Color = Color.Black,
    fontSize: TextUnit = TextUnit.Unspecified,
    outlineSize: Float = 4f,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    lineHeight: TextUnit = TextUnit.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
) {
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Text(
            text = text,
            style = LocalTextStyle.current.merge(
                TextStyle(
                    color = outlineColor,
                    drawStyle = Stroke(width = outlineSize, join = StrokeJoin.Round),
                )
            ),
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow,
            lineHeight = lineHeight,
        )
        Text(
            text = text,
            color = fillColor,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow,
            lineHeight = lineHeight,
        )
    }
}
