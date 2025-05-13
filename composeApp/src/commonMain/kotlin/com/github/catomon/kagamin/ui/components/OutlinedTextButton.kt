package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme

@Composable
fun OutlinedTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.border(2.dp, KagaminTheme.colors.buttonIcon, RoundedCornerShape(6.dp))
            .height(32.dp),
        content = {
            Text(
                text,
                color = KagaminTheme.colors.buttonIcon,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        },
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues.Absolute(),
        colors = ButtonDefaults.outlinedButtonColors()
    )
}