package com.github.catomon.kagamin.ui.v3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

/** Playlists first, then the selected playlist's tracklist with a back button. */
@Composable
fun V3LibraryPane(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
) {
    var browsingPlaylists by remember { mutableStateOf(true) }

    if (browsingPlaylists) {
        V3PlaylistsPane(
            viewModel = viewModel,
            modifier = modifier,
            onPlaylistOpened = { browsingPlaylists = false },
        )
    } else {
        V3TracklistPane(
            viewModel = viewModel,
            modifier = modifier,
            onBack = { browsingPlaylists = true },
        )
    }
}
