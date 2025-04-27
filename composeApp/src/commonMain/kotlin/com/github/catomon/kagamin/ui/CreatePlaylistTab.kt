package com.github.catomon.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.isValidFileName
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.util.Tabs
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.yt_ic
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreatePlaylistTab(viewModel: KagaminViewModel, modifier: Modifier) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLink by remember { mutableStateOf(false) }
    var link by remember(isLink) { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(Colors.theme.listItemB),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(name, onValueChange = {
            name = it
        }, isError = isError, singleLine = true, label = { Text("New playlist") }, modifier = Modifier.padding(horizontal = 8.dp))

        if (isLink) {
            TextField(
                link,
                onValueChange = {
                    link = it
                },
                isError = isError,
                singleLine = true,
                label = { Text("Link") },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(Res.drawable.yt_ic), "Youtube icon")
            Checkbox(isLink, onCheckedChange = {
                isLink = !isLink
            })

            Button(onClick = {
                if (isValidFileName(name)) {
                    viewModel.currentPlaylistName = name
                    if (isLink) {
                        viewModel.audioPlayer.load(listOf(link))
                    }
                    savePlaylist(viewModel.currentPlaylistName, viewModel.audioPlayer.playlist.value.toTypedArray())
                    viewModel.currentTab = Tabs.TRACKLIST
                } else
                    isError = true
            }) {
                Text(if (isLink) "Add" else "Create")
            }
        }
    }
}