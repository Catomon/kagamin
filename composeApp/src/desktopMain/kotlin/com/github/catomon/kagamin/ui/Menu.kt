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
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.kagaminWindowDecoration
import com.github.catomon.kagamin.openInBrowser
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlin.system.exitProcess

@Composable
fun Menu(
    currentTrack: AudioTrack?,
    viewModel: KagaminViewModel,
    navigateToSettings: () -> Unit,
    onClose: () -> Unit,
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
                    onClose()
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
                    onClose()
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
                    openInBrowser("https://github.com/Catomon")
                    onClose()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "GitHub",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            TextButton(
                {
                    navigateToSettings()
                    onClose()
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

            TextButton(
                {
                    viewModel.saveSettings()
                    onClose()
                    exitProcess(0)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Exit App",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            val window = LocalWindow.current
            TextButton(
                {
                    window.isMinimized = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Minimize",
                    color = KagaminTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}