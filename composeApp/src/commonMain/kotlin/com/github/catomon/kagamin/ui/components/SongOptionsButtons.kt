package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.PlaylistsLoader
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.like_song
import kagamin.composeapp.generated.resources.rate_song
import kagamin.composeapp.generated.resources.song_info
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SongOptionsButtons(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    var updatingLike by remember { mutableStateOf(false) }
    var isLoved by remember(
        updatingLike,
        currentTrack
    ) {
        mutableStateOf(currentTrack?.let { currentTrack ->
            viewModel.playlists.value
                .firstOrNull { playlist -> playlist.id == "loved" }?.tracks?.any { it.id == currentTrack.id }
        } ?: false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.height(buttonsSize)
    ) {
        IconButton({
            //todo
        }, modifier = Modifier.size(buttonsSize)) {
            ImageWithShadow(
                painterResource(Res.drawable.song_info),
                "info",
                colorFilter = if (false) ColorFilter.tint( //
                    KagaminTheme.colors.buttonIcon
                )
                else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
            )
        }

        IconButton({
            //todo
        }, modifier = Modifier.size(buttonsSize)) {
            ImageWithShadow(
                painterResource(Res.drawable.rate_song),
                "rate song",
                colorFilter = if (false) ColorFilter.tint( //if rated
                    KagaminTheme.colors.buttonIcon
                )
                else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
            )
        }

        LikeSongButton(isLoved, {
            viewModel.viewModelScope.launch {
                if (updatingLike) return@launch
                updatingLike = true
                if (!isLoved) {
                    currentTrack?.let addToLoved@{ track ->
                        //get loved playlist or create new and then add the track to it and finally save playlist
                        viewModel.playlists.value
                            .firstOrNull { playlist -> playlist.id == "loved" }
                            ?.let { playlist ->
                                if (playlist.tracks.any { it.id == track.id }) return@addToLoved
                                viewModel.updatePlaylist(playlist.copy(tracks = playlist.tracks + track)); playlist
                            } ?: Playlist("loved", "loved", listOf(track))
                            .also { playlist ->
                                viewModel.createPlaylist(
                                    playlist
                                )
                            }.also {
                                PlaylistsLoader.savePlaylist(it)
                            }

                    }
                } else {
                    //remove the track from the loved playlist and then save playlist
                    currentTrack?.let { track ->
                        viewModel.playlists.value
                            .firstOrNull { playlist -> playlist.id == "loved" }
                            ?.let { playlist ->
                                viewModel.updatePlaylist(playlist.copy(tracks = playlist.tracks - track))
                                PlaylistsLoader.savePlaylist(playlist)
                            }
                    }
                }
                isLoved = currentTrack?.let { viewModel.lovedSongs.containsKey(it.uri) } ?: false
                updatingLike = false
            }
        }, buttonsSize)
    }
}

@Composable
fun LikeSongButton(
    isLoved: Boolean,
    onClick: () -> Unit,
    buttonsSize: Dp,
    modifier: Modifier = Modifier
) {

    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonsSize)
    ) {
        Icon(
            painter = painterResource(Res.drawable.like_song),
            contentDescription = "like song",
            tint = if (isLoved) KagaminTheme.colors.disabled else KagaminTheme.colors.buttonIconTransparent
        )
    }
}