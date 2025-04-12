package chu.monscout.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.isValidFileName
import chu.monscout.kagamin.savePlaylist
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import chu.monscout.kagamin.ui.util.Tabs

@Composable
fun CreatePlaylistTab(state: KagaminViewModel, modifier: Modifier) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(Colors.currentYukiTheme.listItemB),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(name, onValueChange = {
            name = it
        }, isError = isError, singleLine = true, label = { Text("New playlist") }, modifier = Modifier.padding(horizontal = 8.dp))

        Button(onClick = {
            if (isValidFileName(name)) {
                state.currentPlaylistName = name
                savePlaylist(name, emptyArray())

                state.currentTab = Tabs.TRACKLIST
            } else
                isError = true
        }) {
            Text("Create")
        }
    }
}