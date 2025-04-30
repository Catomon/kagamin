package com.github.catomon.kagamin.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.components.ImageWithShadow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.music_note
import kagamin.composeapp.generated.resources.playlists
import kagamin.composeapp.generated.resources.tiny_star_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TracklistTabButton(
    onClick: () -> Unit,
    color: Color = KagaminTheme.theme.buttonIconSmall,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.music_note),
            "Tracklist tab",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun PlaylistsTabButton(
    onClick: () -> Unit,
    color: Color = KagaminTheme.theme.buttonIconSmall,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.playlists),
            "Tracklist tab",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun PlaybackTabButton(
    onClick: () -> Unit,
    color: Color = KagaminTheme.theme.buttonIconSmall,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.tiny_star_icon),
            "Tracklist tab",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}