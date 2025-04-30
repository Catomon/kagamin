package com.github.catomon.kagamin.ui

import androidx.compose.runtime.Composable
import com.github.catomon.kagamin.data.PlaylistData
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
expect fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    viewModel: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit
)