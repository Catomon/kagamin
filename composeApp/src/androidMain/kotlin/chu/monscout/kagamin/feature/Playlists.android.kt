package chu.monscout.kagamin.feature

import chu.monscout.kagamin.PlaylistData
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors

@Composable
actual fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    state: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit
) {
        Column(
            Modifier.background(color = if (i % 2 == 0) Colors.dividers.copy(alpha = 0.50f) else Colors.background.copy(alpha = 0.50f))
                .clickable {
                    state.currentPlaylistName = playlist.first
                    state.currentTab = Tabs.TRACKLIST
                }.let {
                    if (state.currentPlaylistName == playlist.first) it.border(
                        2.dp,
                        Colors.bars
                    ) else it
                }.padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    playlist.first, fontSize = 12.sp, color = Color.White,
                    maxLines = 1
                )

//                    if (Random.nextInt(0, 4) == 1)
//                        Image(painterResource(Res.drawable.yt_ic), "")
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    "Tracks: ${playlist.second.tracks.size}",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 10.sp,
                    color = Colors.noteText
                )
                Text(
                    "Duration: ???",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 10.sp,
                    color = Colors.noteText
                )
            }
    }
}