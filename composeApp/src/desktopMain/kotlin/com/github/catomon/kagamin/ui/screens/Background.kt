package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings

@Composable
fun Background(currentTrack: AudioTrack?, modifier: Modifier) {
//    Box(Modifier.fillMaxSize().background(color = KagaminTheme.colors.background))

    val settings = LocalAppSettings.current

    if (settings.useTrackImageAsBackground)
        TrackThumbnail(
            currentTrack,
            modifier = modifier.clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            blur = true,
        )
}