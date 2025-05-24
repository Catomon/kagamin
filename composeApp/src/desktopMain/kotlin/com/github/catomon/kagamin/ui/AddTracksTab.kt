package com.github.catomon.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.loadTrackFilesToCurrentPlaylist
import com.github.catomon.kagamin.ui.components.OutlinedTextButton
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddTracksTab(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val snackbar = LocalSnackbarHostState.current
    val extensions = listOf("mp3", "wav")

    val filePicker = rememberFilePickerLauncher(
        mode = FileKitMode.Multiple(),
        type = FileKitType.File(extensions)
    ) { files ->
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (files != null) {
                    loadTrackFilesToCurrentPlaylist(files.map { it.file }, viewModel, snackbar)
                }
            }

            snackbar.showSnackbar("${files?.size ?: 0} tracks were added.")
        }
    }

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
            if (currentPlaylist.isOnline)
                OnlinePlaylistOptions(currentPlaylist, onAddTracks = { link ->
                    viewModel.viewModelScope.launch {
                        val loadedTracks = viewModel.loadTracks(link)
                        val loadedUris = loadedTracks.map { it.uri }
                        viewModel.updatePlaylist(currentPlaylist.copy(tracks = currentPlaylist.tracks.filter { it.uri !in loadedUris } + loadedTracks))
                    }
                })
            else
                OfflinePlaylistOptions(currentPlaylist, filePicker)
        }
    }
}

@Composable
private fun ColumnScope.OnlinePlaylistOptions(
    currentPlaylist: Playlist,
    onAddTracks: (url: String) -> Unit,
) {
    var link by remember { mutableStateOf("") }

    Text(
        "Playlist: ${currentPlaylist.name}\n" +
                "Enter playlist or track url:",
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 8.dp),
        color = KagaminTheme.textSecondary
    )

    OutlinedTextField(
        link, onValueChange = {
            link = it
        },
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        maxLines = 1,
        label = { Text("URL") }
    )

    OutlinedTextButton(text = "Add", onClick = {
        onAddTracks(link)
        link = ""
    })
}

@Composable
private fun ColumnScope.OfflinePlaylistOptions(
    currentPlaylist: Playlist,
    filePicker: PickerResultLauncher
) {
    Text(
        "Playlist: ${currentPlaylist.name}\nDrop files or folders here,\nor select from folder:",
        textAlign = TextAlign.Center,
        //color = Colors.noteText
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 8.dp),
        color = KagaminTheme.textSecondary
    )

    IconButton(onClick = {
        filePicker.launch()
    }) {
        Icon(
            painterResource(Res.drawable.folder),
            "Select files from folder",
            tint = KagaminTheme.colors.buttonIcon
        )
    }
}
