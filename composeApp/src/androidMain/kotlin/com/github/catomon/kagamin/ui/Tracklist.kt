package com.github.catomon.kagamin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.components.ThumbnailTrackItem
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.TracklistManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun Tracklist(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?,
    tracks: List<AudioTrack>,
    onPlay: (AudioTrack) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }

    Column(
        modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Companion.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(
                    color = KagaminTheme.backgroundTransparent,
                    size = size,
                    blendMode = BlendMode.Companion.SrcOut
                )
                drawContent()
            }, horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
//        TrackThumbnail(
//            currentTrack, Modifier.Companion.size(300.dp)
//        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(4.dp),
            modifier = Modifier.Companion
        ) {
            items(tracks.size) {
                val track = tracks[it]
                ThumbnailTrackItem(
                    it,
                    track,
                    tracklistManager,
                    viewModel,
                    currentTrack == track,
                    { onPlay(track) })
            }
        }
    }
}