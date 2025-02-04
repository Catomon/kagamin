package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.yukinotes.feature.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.yt_ic
import loadPlaylists
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

@Composable
fun Playlists(modifier: Modifier = Modifier) {
    val playlists by remember { mutableStateOf(loadPlaylists()) }
    LazyColumn(
        modifier,
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(playlists.size) {
            val playlist = playlists[it]
            Column(
                Modifier.background(color = if (it % 2 == 0) Colors.dividers else Colors.background)
                    .padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        playlist.first, fontSize = 12.sp, color = Color.White,
                        maxLines = 1
                    )

                    if (Random.nextInt(0, 4) == 1)
                        Image(painterResource(Res.drawable.yt_ic), "")
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
    }
}