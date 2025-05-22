package com.github.catomon.kagamin.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.util.echoTrace

@Composable
fun CurrentTrackFrameHorizontal(track: AudioTrack?, modifier: Modifier = Modifier) {
    echoTrace { "CurrentTrackFrameHorizontal" }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Row(modifier) {
        TrackThumbnail(
            track,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(8.dp),
            height = ThumbnailCacheManager.SIZE.H64
        )

        if (track != null)
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    .padding(start = 4.dp)
            ) {
                Text(
                    track.title,
                    fontSize = 12.sp,
                    color = KagaminTheme.text,
                    maxLines = 1,
                    modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
                )

                if (track.artist.isNotBlank())
                    Text(
                        track.artist,
                        fontSize = 10.sp,
                        color = KagaminTheme.textSecondary,
                        maxLines = 1,
                        modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                        lineHeight = 18.sp
                    )

                if (track.duration >= 0)
                    Text(
                        remember { formatMillisToMinutesSeconds(track.duration) },
                        fontSize = 10.sp,
                        color = KagaminTheme.textSecondary,
                        maxLines = 1,
                        modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                        lineHeight = 18.sp
                    )
            }
    }
}

@Preview
@Composable
private fun Preview() {
    KagaminTheme {
        Surface() {
            CurrentTrackFrameHorizontal(
                AudioTrack("0", "dipshit", "Nai Nai Nai", "Kagami Hiiragi"),
                Modifier.height(200.dp).fillMaxWidth()
            )
        }
    }
}