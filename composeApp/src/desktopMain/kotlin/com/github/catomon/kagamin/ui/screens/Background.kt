package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.theme.KagaminTheme

@Composable
fun Background(currentTrack: AudioTrack?) {
//    Box(Modifier.fillMaxSize().background(color = KagaminTheme.colors.background))

    val settings = LocalAppSettings.current

    if (settings.useTrackImageAsBackground)
        TrackThumbnail(
            currentTrack,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            blur = true,
        )
}