package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
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
    color: Color = KagaminTheme.theme.buttonIconSmall
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(32.dp)
    ) {
        ImageWithShadow(
            painterResource,
            "Add button",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}