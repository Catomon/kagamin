package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurrentTrackFrameHorizontal(track: AudioTrack?, viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    echoTrace { "CurrentTrackFrameHorizontal" }

    val interactionSource = remember { MutableInteractionSource() }
    val isThumbnailHovered by interactionSource.collectIsHoveredAsState()

    val position by viewModel.position.collectAsState()
    val progress by remember(track) {
        derivedStateOf {
            when (track) {
                null -> 0f
                else -> if (track.duration > 0 && track.duration < Long.MAX_VALUE) position.toFloat() / track.duration else -1f
            }
        }
    }
    var progressOnHover by remember { mutableStateOf(0f) }
    val progressTargetValue by remember(progress) {
        derivedStateOf {
            if (isThumbnailHovered) progressOnHover else progress
        }
    }
    val progressAnimated by animateFloatAsState(progressTargetValue)

    Row(modifier.height(100.dp)) {
        TrackThumbnailProgressOverlay(
            track,
            onSetProgress = {
                if (track != null) {
                    viewModel.seek((track.duration * it).toLong())
                }
            },
            progress = progressAnimated,
            progressColor = KagaminTheme.colors.thumbnailProgressIndicator,
            modifier = Modifier.size(100.dp).hoverable(interactionSource).onPointerEvent(
                PointerEventType.Move
            ) {
                progressOnHover = it.changes.first().position.x / size.width
            },
            shape = RoundedCornerShape(8.dp),
            height = ThumbnailCacheManager.SIZE.H150,
            shadow = false,
            controlProgress = true
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
                    modifier = Modifier.let { if (isThumbnailHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
                )

                if (track.artist.isNotBlank())
                    Text(
                        track.artist,
                        fontSize = 10.sp,
                        color = KagaminTheme.textSecondary,
                        maxLines = 1,
                        modifier = Modifier.let { if (isThumbnailHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                        lineHeight = 18.sp
                    )

                Spacer(Modifier.weight(1f))

                if (track.duration >= 0)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        echoTrace { "TrackDurationText" }

                        val trackDurationText by remember(track) {
                            derivedStateOf {
                                "${
                                    if (isThumbnailHovered) formatMillisToMinutesSeconds((track.duration * progressAnimated).toLong())
                                    else formatMillisToMinutesSeconds(track.let { position })
                                }/${formatMillisToMinutesSeconds(track.duration)}"
                            }
                        }

                        Text(
                            trackDurationText,
                            fontSize = 10.sp,
                            color = KagaminTheme.colors.buttonIcon
                        )
                    }
            }
    }
}
