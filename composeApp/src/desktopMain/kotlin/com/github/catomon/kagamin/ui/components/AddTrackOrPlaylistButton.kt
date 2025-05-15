package com.github.catomon.kagamin.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun AddTrackOrPlaylistButton(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    AddButton(
        onClick = {
            viewModel.currentTab = Tabs.CREATE_PLAYLIST
            viewModel.createPlaylistWindow = !viewModel.createPlaylistWindow
        },
        modifier = modifier,
        color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall,
        size = 24.dp
    )
}