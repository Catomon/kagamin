package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.lucky_background_stars
import org.jetbrains.compose.resources.painterResource

@Composable
fun Background(currentTrack: AudioTrack?, modifier: Modifier) {
//    Box(Modifier.fillMaxSize().background(color = KagaminTheme.colors.background))

    val settings = LocalAppSettings.current

    if (settings.useTrackImageAsBackground && currentTrack != null)
        TrackThumbnail(
            currentTrack,
            modifier = modifier.clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            blur = true,
        )
    else
        Image(
            painter = painterResource(Res.drawable.lucky_background_stars),
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(14.dp)),
            contentDescription = null,
            colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
        )
}