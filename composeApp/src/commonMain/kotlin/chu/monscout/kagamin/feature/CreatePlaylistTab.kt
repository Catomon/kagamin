package chu.monscout.kagamin.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import isValidFileName
import savePlaylist

@Composable
fun CreatePlaylistTab(state: KagaminViewModel, modifier: Modifier) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(name, onValueChange = {
            name = it
        }, isError = isError)

        Button(onClick = {
            if (isValidFileName(name)) {
                state.currentPlaylistName = name
                savePlaylist(name, emptyArray())
                //state.playlists = loadPlaylists()
                //name = ""

                state.currentTab = Tabs.TRACKLIST
            } else
                isError = true
        }) {
            Text("Create")
        }
    }
}