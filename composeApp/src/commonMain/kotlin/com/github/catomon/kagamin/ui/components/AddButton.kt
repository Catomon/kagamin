package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddButton(
    painterResource: Painter = painterResource(Res.drawable.add),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = KagaminTheme.colors.buttonIconSmall,
    size: Dp = 20.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clip(RoundedCornerShape(8.dp)).clickable { onClick() }
    ) {
        ImageWithShadow(
            painterResource,
            "Add button",
            modifier = Modifier.size(size),
            colorFilter = ColorFilter.tint(color)
        )
    }
}