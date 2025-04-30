package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddItemButton(onClick: () -> Unit, modifier: Modifier) {
    IconButton(onClick = onClick, modifier.background(KagaminTheme.background, CircleShape).size(32.dp)) {
        Icon(painterResource(Res.drawable.add), "Add button", modifier = Modifier.size(16.dp))
    }
}