package com.github.catomon.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddTracksTab(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
//    val showFilePicker = remember { mutableStateOf(false) }
//    MultiFilePicker(showFilePicker, viewModel.audioPlayer, viewModel.currentPlaylistName)

    val audioPlayer = viewModel.audioPlayer
    val currentPlaylistName = viewModel.currentPlaylistName

    val snackbar = LocalSnackbarHostState.current
    val extensions = listOf("mp3", "wav")

    val filePicker = rememberFilePickerLauncher(
        mode = FileKitMode.Multiple(),
        type = FileKitType.File(extensions)
    ) { files ->
        viewModel.viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (files != null) {
                    audioPlayer.load(files.map { it.path })

                    savePlaylist(currentPlaylistName, audioPlayer.playlist.value.toTypedArray())
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

        Column(Modifier.fillMaxSize().background(KagaminTheme.theme.listItem), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(
                "Playlist: ${viewModel.currentPlaylistName}\nDrop files or folders here,\nor select from folder:",
                textAlign = TextAlign.Center,
                //color = Colors.noteText
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = KagaminTheme.textSecondary
            )

            IconButton(onClick = {
                filePicker.launch()
            }) {
                Icon(painterResource(Res.drawable.folder), "Select files from folder")
            }
        }
    }
}
