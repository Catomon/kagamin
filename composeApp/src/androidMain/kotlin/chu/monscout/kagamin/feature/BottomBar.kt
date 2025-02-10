package chu.monscout.kagamin.feature

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomBar(onPlaylistButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    Button({
        onPlaylistButtonClick()
    }) {
        Text("Playlist")
    }
}