package com.github.catomon.kagamin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.SortType
import com.github.catomon.kagamin.isValidFileName
import com.github.catomon.kagamin.ui.components.OutlinedTextButton
import com.github.catomon.kagamin.ui.screens.SortingToggleButton
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.arrow_left
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditPlaylist(playlist: Playlist, onSort: (SortType) -> Unit, onRename: (String) -> Unit, onClose: () -> Unit) {
    var playlistName by remember { mutableStateOf(playlist.name) }

    var isError by remember { mutableStateOf(false) }

    var sortType by remember {mutableStateOf(playlist.sortType)}

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text("Edit playlist: ${playlist.name}", modifier = Modifier.align(Alignment.TopCenter))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                playlistName,
                onValueChange = {
                    isError = !isValidFileName(it)
                    playlistName = it.take(64)
                },
                isError = isError,
                singleLine = true,
                label = { Text("Name") },
                modifier = Modifier.padding(8.dp),
                maxLines = 1
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sorting: ", color = KagaminTheme.colors.textSecondary)
                SortingToggleButton(sortType, {
                    val sortEntries = SortType.entries
                    sortType = (sortEntries.indexOf(sortType) + 1).let { nextIndex ->
                        sortEntries[if (nextIndex < sortEntries.size) nextIndex else 0]
                    }

                    onSort(sortType)
                })
            }

            OutlinedTextButton(text = "Save", {
                if (playlistName.isBlank() || !isValidFileName(playlistName)) {
                    isError = true
                    return@OutlinedTextButton
                }
                onRename(playlistName)
                onClose()
            })
        }

        IconButton({
            onClose()
        }, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(
                painterResource(Res.drawable.arrow_left),
                contentDescription = null,
                tint = KagaminTheme.colors.buttonIcon,
                modifier = Modifier.scale(-1f, 1f)
            )
        }
    }
}