package chu.monscout.kagamin.feature

import chu.monscout.kagamin.DenpaFilePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.folder
import org.jetbrains.compose.resources.painterResource

@Composable
fun AddTracksTab(state: KagaminViewModel, modifier: Modifier = Modifier) {
    val showFilePicker = remember { mutableStateOf(false) }
    DenpaFilePicker(showFilePicker, state.denpaPlayer, state.currentPlaylistName)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Drop files or folders here,\nor select from folder:",
            textAlign = TextAlign.Center,
            //color = Colors.noteText
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(onClick = {
            showFilePicker.value = true
        }) {
            Icon(painterResource(Res.drawable.folder), "Select files from folder")
        }
    }
}