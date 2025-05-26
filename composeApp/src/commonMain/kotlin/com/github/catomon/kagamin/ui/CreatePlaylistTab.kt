package com.github.catomon.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.isValidFileName
import com.github.catomon.kagamin.ui.components.OutlinedTextButton
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.yt_ic
import org.jetbrains.compose.resources.painterResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun CreatePlaylistTab(viewModel: KagaminViewModel, modifier: Modifier) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isOnline by remember { mutableStateOf(false) }
    var link by remember(isOnline) { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        AddTrackCreatePlaylistTabButtons(viewModel, modifier = Modifier.fillMaxWidth())

        Column(
            Modifier.fillMaxSize().background(KagaminTheme.colors.backgroundTransparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                name,
                onValueChange = {
                    name = it.take(64)
                },
                isError = isError,
                singleLine = true,
                label = { Text("New playlist") },
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1
            )

            if (isOnline) {
                OutlinedTextField(
                    link,
                    onValueChange = {
                        link = it
                    },
                    isError = isError,
                    singleLine = true,
                    label = { Text("URL (optional)") },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(Res.drawable.yt_ic), null, modifier = Modifier.padding(4.dp))
                Text("Online")
                Checkbox(isOnline, onCheckedChange = {
                    isOnline = !isOnline
                })

                OutlinedTextButton(
                    text = "Create",
                    onClick = {
                        if (isValidFileName(name)) {
                            val newPlaylist = Playlist(
                                id = Uuid.random().toString(),
                                name = name,
                                tracks = emptyList(),
                                isOnline = isOnline,
                                url = link
                            )
                            viewModel.createPlaylist(newPlaylist)
                            viewModel.updateCurrentPlaylist(newPlaylist)

                            viewModel.currentTab = Tabs.ADD_TRACKS
                        } else {
                            isError = true
                        }
                    }
                )
            }
        }
    }
}