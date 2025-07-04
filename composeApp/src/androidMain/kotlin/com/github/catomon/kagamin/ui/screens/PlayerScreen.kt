package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.fetchAudioTracks
import com.github.catomon.kagamin.result
import com.github.catomon.kagamin.ui.BottomBar
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun PlayerScreen(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    var tracks by remember { mutableStateOf<Map<String, List<AudioTrack>>>(emptyMap()) }
    var isScanning by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var currentFolder by remember { mutableStateOf("") }

    val currentTrack by viewModel.currentTrack.collectAsState()

    val playState by viewModel.playState.collectAsState()
    val playMode by viewModel.playMode.collectAsState()

    val currentPlaylist by viewModel.currentPlaylist.collectAsState()

    LaunchedEffect(isScanning) {
        if (!isScanning) {
            currentFolder = "Music"
            viewModel.changeCurrentPlaylist(
                Playlist(
                    currentFolder,
                    currentFolder,
                    tracks[currentFolder] ?: emptyList(),
                )
            )
        }
    }

    LaunchedEffect(result) {
        tracks = fetchAudioTracks(context)

        isScanning = false
    }

    Box(
        modifier
            .fillMaxSize()
            .background(KagaminTheme.colors.background), contentAlignment = Alignment.Center
    ) {
        Background(currentTrack, Modifier.matchParentSize())

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (currentFolder.isEmpty())
                AnimatedContent(isScanning, modifier = Modifier.weight(1f)) {
                    if (it) {
                        Text("Scanning...")
                    } else {
                        if (tracks.isEmpty()) {
                            Text("Empty.")
                        } else {
                            LazyColumn(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                items(tracks.keys.size) {
                                    Text(tracks.keys.elementAt(it), modifier = Modifier.clickable {
                                        currentFolder = tracks.keys.elementAt(it)
                                        viewModel.changeCurrentPlaylist(
                                            Playlist(
                                                currentFolder,
                                                currentFolder,
                                                tracks[currentFolder] ?: emptyList(),
                                            )
                                        )
                                    })
                                }
                            }
                        }
                    }
                }
            else
                Tracklist(
                    viewModel,
                    currentTrack,
                    tracks = currentPlaylist.tracks,
                    onPlay = { viewModel.play(it) },
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

            BottomBar(
                trackName = currentTrack?.title ?: "",
                isPlaying = playState == AudioPlayerService.PlayState.PLAYING,
                isRepeat = playMode == PlaylistsManager.PlayMode.REPEAT_TRACK,
                isShuffle = playMode == PlaylistsManager.PlayMode.RANDOM,
                onPlayPauseClick = viewModel::onPlayPause,
                onPrevClick = viewModel::prevTrack,
                onNextClick = viewModel::nextTrack,
                onRepeatClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.REPEAT_TRACK) },
                onShuffleClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.RANDOM) },
                modifier = Modifier.fillMaxWidth()
            )
        }

//        IconButton(
//            onClick = viewModel::onPlayPause,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .size(64.dp)
//                .background(
//                    KagaminTheme.colors.backgroundTransparent,
//                    CircleShape
//                ),
//        ) {
//            AnimatedContent(playState == AudioPlayerService.PlayState.PLAYING) { isPlaying ->
//                if (isPlaying) {
//                    Icon(
//                        painterResource(Res.drawable.pause_star),
//                        "Pause",
//                        modifier = Modifier.size(64.dp),
//                        tint = KagaminTheme.colors.buttonIcon
//                    )
//
//                } else {
//                    Icon(
//                        painterResource(Res.drawable.play_star),
//                        "Play",
//                        modifier = Modifier.size(64.dp),
//                        tint = KagaminTheme.colors.buttonIcon
//                    )
//                }
//            }
//        }
    }
}