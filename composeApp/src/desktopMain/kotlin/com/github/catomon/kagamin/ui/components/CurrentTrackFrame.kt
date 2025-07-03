package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurrentTrackFrame(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?, modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isThumbnailHovered by interactionSource.collectIsHoveredAsState()

    val position by viewModel.position.collectAsState()
    val progress by remember(currentTrack) {
        derivedStateOf {
            when (currentTrack) {
                null -> 0f
                else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) position.toFloat() / currentTrack.duration else -1f
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

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnailWithProgressOverlay(
                currentTrack,
                onSetProgress = {
                    if (currentTrack != null) {
                        viewModel.seek((currentTrack.duration * it).toLong())
                    }
                },
                progress = progressAnimated,
                progressColor = KagaminTheme.colors.thumbnailProgressIndicator,
                modifier = Modifier.padding(8.dp).size(145.dp).hoverable(interactionSource).onPointerEvent(
                    PointerEventType.Move
                ) {
                    progressOnHover = it.changes.first().position.x / size.width
                },
                size = ThumbnailCacheManager.SIZE.H150,
                controlProgress = true
            )

            PlaybackButtons(
                viewModel, Modifier.width(133.dp)
            )

            PlaybackOptionsButtons(viewModel, Modifier.width(133.dp))

            VolumeOptions(
                volume = viewModel.volume.value,
                onVolumeChange = { newVolume ->
                    viewModel.setVolume(newVolume)
                },
                modifier = Modifier.width(133.dp)
            )

//            TrackProgressIndicator(
//                currentTrack,
//                player,
//                updateProgress,
//                progress,
//                modifier = Modifier.padding(horizontal = 20.dp)
//            )

            //todo
//            SongOptionsButtons(
//                modifier = Modifier.width(133.dp),
//                viewModel = viewModel,
//            )
        }
    }
}

@Composable
fun CompactCurrentTrackFrame(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?,
    modifier: Modifier = Modifier
) {
    val position by viewModel.position.collectAsState()

    val progress by remember(currentTrack) {
        derivedStateOf {
            when (currentTrack) {
                null -> 0f
                else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) position.toFloat() / currentTrack.duration else 0f
            }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(modifier.hoverable(interactionSource), contentAlignment = Alignment.Center) {

        val progressTargetValue = remember(
            currentTrack, isHovered, progress
        ) { if (isHovered) 1f else progress }
        val progressAnimated by animateFloatAsState(progressTargetValue)

        TrackThumbnailWithProgressOverlay(
            currentTrack,
            onSetProgress = {
                if (currentTrack != null) {
                    viewModel.seek((currentTrack.duration * it).toLong())
                }
            },
            progress = progressAnimated,
            progressColor = KagaminTheme.colors.thumbnailProgressIndicator,
            modifier = Modifier.padding(8.dp).size(145.dp),
            size = ThumbnailCacheManager.SIZE.H150
        )

        AnimatedVisibility(
            isHovered,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.height(144.dp).width(153.dp).clip(
                RoundedCornerShape(12.dp)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                //modifier = Modifier.let { if (currentTrack != null) it.background(Colors.barsTransparent) else it },
            ) {
                Spacer(Modifier.height(16.dp))

                PlaybackOptionsButtons(viewModel, Modifier.width(133.dp))

                PlaybackButtons(
                    viewModel, Modifier.width(133.dp)
                )

                VolumeOptions(
                    volume = viewModel.volume.value,
                    onVolumeChange = { newVolume ->
                        viewModel.setVolume(newVolume)
                    },
                    modifier = Modifier.width(133.dp)
                )

//                TrackProgressIndicator(
//                    currentTrack,
//                    player,
//                    updateProgress,
//                    progress,
//                    modifier = Modifier.padding(horizontal = 20.dp)
//                )
            }
        }
    }
}
