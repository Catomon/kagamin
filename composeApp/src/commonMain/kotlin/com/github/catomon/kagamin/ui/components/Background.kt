package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.theme.KagaminColors
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.group_background
import kagamin.composeapp.generated.resources.group_background2
import kagamin.composeapp.generated.resources.lucky_background_stars
import kagamin.composeapp.generated.resources.miyuki_bg2
import kagamin.composeapp.generated.resources.nata_background
import kagamin.composeapp.generated.resources.tsukagami
import org.jetbrains.compose.resources.painterResource

@Composable
fun Background(currentTrack: AudioTrack?, modifier: Modifier) {
//    Box(Modifier.fillMaxSize().background(color = KagaminTheme.colors.background))

    val settings = LocalAppSettings.current

    val backgroundRes = when (KagaminTheme.colors) {
        KagaminColors.Blue -> Res.drawable.nata_background
        KagaminColors.KagaminDark -> Res.drawable.group_background2
        KagaminColors.Pink -> Res.drawable.miyuki_bg2
        KagaminColors.Violet -> Res.drawable.tsukagami
        KagaminColors.VioletKasa -> Res.drawable.tsukagami
        else -> {
            Res.drawable.lucky_background_stars
        }
    }

    if (settings.useTrackImageAsBackground && currentTrack != null)
        TrackThumbnail(
            currentTrack,
            modifier = modifier.clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            blur = true,
        )
    else
        Image(
            painter = painterResource(backgroundRes),
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(14.dp)),
            contentDescription = null,
//            colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
        )
}