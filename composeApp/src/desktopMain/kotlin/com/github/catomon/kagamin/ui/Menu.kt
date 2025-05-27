package com.github.catomon.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.kagaminWindowDecoration
import com.github.catomon.kagamin.ui.screens.Background
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun Menu(
    currentTrack: AudioTrack?,
    viewModel: KagaminViewModel,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.kagaminWindowDecoration().background(color = KagaminTheme.background)) {
        Background(currentTrack, Modifier.matchParentSize())

        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier.verticalScroll(
                rememberScrollState()
            ).background(color = KagaminTheme.backgroundTransparent).width(150.dp)
        ) {
            TextButton(
                {
                    viewModel.currentTab = Tabs.ADD_TRACKS
                    viewModel.createPlaylistWindow = !viewModel.createPlaylistWindow
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Add tracks",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            TextButton(
                {
                    viewModel.currentTab = Tabs.CREATE_PLAYLIST
                    viewModel.createPlaylistWindow = !viewModel.createPlaylistWindow
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "New playlist",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            TextButton(
                {

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "About",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            TextButton(
                {
                    navigateToSettings()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Settings",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}