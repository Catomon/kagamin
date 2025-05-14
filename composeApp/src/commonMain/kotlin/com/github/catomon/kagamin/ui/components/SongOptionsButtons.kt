package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.toTrackData
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.like_song
import kagamin.composeapp.generated.resources.rate_song
import kagamin.composeapp.generated.resources.song_info
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun SongOptionsButtons(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    val curTrack = viewModel.currentTrack

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

        LikeSongButton(viewModel, curTrack, buttonsSize)
    }
}

@Composable
fun LikeSongButton(
    viewModel: KagaminViewModel,
    curTrack: AudioTrack?,
    buttonsSize: Dp,
    modifier: Modifier = Modifier
) {
    var updatingLike by remember { mutableStateOf(false) }
    var loved by remember(
        updatingLike,
        curTrack
    ) {
        mutableStateOf(curTrack?.let {
            viewModel.lovedSongs.containsKey(
                it.uri
            )
        } ?: false)
    }

    IconButton(
        onClick = {
            viewModel.viewModelScope.launch {
                if (updatingLike) return@launch
                updatingLike = true
                if (!loved) {
                    curTrack?.toTrackData()?.let { curTrackData ->
                        viewModel.lovedSongs[curTrackData.uri] = curTrackData
                        withContext(Dispatchers.IO) {
                            savePlaylist(
                                "loved",
                                viewModel.lovedSongs.values.toList() + curTrackData
                            )
                        }
                    }
                } else {
                    curTrack?.toTrackData()?.let { curTrackData ->
                        viewModel.lovedSongs.remove(curTrackData.uri)
                        withContext(Dispatchers.IO) {
                            savePlaylist(
                                "loved",
                                viewModel.lovedSongs.values.toList()
                            )
                        }
                    }
                }
                loved = curTrack?.let { viewModel.lovedSongs.containsKey(it.uri) } ?: false
                updatingLike = false
            }
        },
        modifier = modifier.size(buttonsSize)
    ) {
        Icon(
            painter = painterResource(Res.drawable.like_song),
            contentDescription = "like song",
            tint = if (loved) KagaminTheme.colors.disabled else KagaminTheme.colors.buttonIconTransparent
        )
    }
}