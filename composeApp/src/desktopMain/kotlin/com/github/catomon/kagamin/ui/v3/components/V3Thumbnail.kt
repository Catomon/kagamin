package com.github.catomon.kagamin.ui.v3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.ui.v3.theme.V3Theme

/**
 * Big square track image with a gradient frame, a soft glow and a slim
 * click-to-seek progress line along the bottom edge.
 */
@Composable
fun V3Thumbnail(
    track: AudioTrack?,
    position: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    side: Dp = 300.dp,
    showProgress: Boolean = true,
) {
    val colors = V3Theme.colors
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val progress = when {
        track == null -> 0f
        track.duration <= 0 || track.duration == Long.MAX_VALUE -> 0f
        else -> (position.toFloat() / track.duration).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .size(side)
            .v3Glow(color = Color.White, alpha = 0.35f, cornerRadius = 12.dp, shadowRadius = 6.dp)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.panelStart, colors.panelEnd)))
            .border(
                width = 3.dp,
                color = colors.controlContent.copy(alpha = 0.22f),
                shape = shape,
            )
            .hoverable(interactionSource)
            .pointerInput(track) {
                if (track == null) return@pointerInput
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek((track.duration * fraction).toLong())
                }
            },
    ) {
        TrackThumbnail(
            currentTrack = track,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            size = ThumbnailCacheManager.SIZE.H512,
        )

        if (showProgress && track != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(colors.headerOutline.copy(alpha = 0.55f)),
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(colors.accentSoft, colors.accent)
                                ),
                                CircleShape,
                            ),
                    )
                }
            }
        }

        if (isHovered && track != null) {
            V3OutlinedText(
                text = "${formatMillisToMinutesSeconds(position)} / ${formatMillisToMinutesSeconds(track.duration)}",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                fillColor = colors.headerContent,
                outlineColor = colors.headerOutline,
                fontSize = 11.sp,
                outlineSize = 3f,
                maxLines = 1,
            )
        }
    }
}
