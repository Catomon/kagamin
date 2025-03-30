package chu.monscout.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.PlaylistData
import chu.monscout.kagamin.audio.AudioPlayer
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    state: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(56.dp)) {
        if (state.currentPlaylistName == playlist.first) {
            Box(Modifier
                .fillMaxHeight()
                .clip(
                    RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                )
                .background(Colors.barsTransparent)
                .clickable {
                    state.onPlayPause()
                }, contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(if (state.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                    "current playlist playback state icon",
                    modifier = Modifier
                        .size(16.dp)
                        .fillMaxHeight(),
                    colorFilter = ColorFilter.tint(Colors.surface)
                )
            }
        }

        Column(
            Modifier
                .fillMaxHeight()
                .background(color = if (i % 2 == 0) Colors.currentYukiTheme.listItemA else Colors.currentYukiTheme.listItemB)
                .clickable {
                    state.currentPlaylistName = playlist.first
                    state.currentTab = Tabs.TRACKLIST
                }
                .padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    playlist.first, fontSize = 12.sp, color = Colors.text,
                    maxLines = 1
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    "Tracks: ${playlist.second.tracks.size}",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 10.sp,
                    color = Colors.text2
                )
//                    Text(
//                        "Duration: ???",
//                        modifier = Modifier.weight(0.5f),
//                        fontSize = 10.sp,
//                        color = Colors.text2
//                    )
            }
        }
    }
}
