package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import audio.DenpaTrack
import chu.monscout.kagamin.audio.songAuthorPlusTitle
import com.github.catomon.yukinotes.feature.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.yt_ic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

@Composable
fun Tracklist(state: KagaminViewModel, tracks: List<DenpaTrack>, modifier: Modifier = Modifier) {
    LazyColumn(modifier, state = rememberLazyListState()) {
        items(tracks.size) { i ->
            val track = tracks[i]
            val backColor = if (i % 2 == 0) Colors.dividers else Colors.background
            Box(
                modifier = Modifier.fillMaxWidth().height(32.dp)
                    .background(color = backColor)
                    .clickable {
                        if (state.isLoadingSong != null) return@clickable
                        CoroutineScope(Dispatchers.Default).launch {
                            state.isLoadingSong = track
                            state.denpaPlayer.play(track)
                            state.isLoadingSong = null
                        }
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    track.songAuthorPlusTitle,
                    fontSize = 12.sp,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart),
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (Random.nextInt(0, 4) == 1)
                        Image(
                            painterResource(Res.drawable.yt_ic),
                            "",
                            modifier = Modifier.background(
                                color = backColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                                .padding(horizontal = 2.dp)
                        )

//                    Text(
//                        "5:25", //remember { formatMilliseconds(track.duration).take(9) },
//                        fontSize = 12.sp,
//                        color = Colors.noteText,
//                        maxLines = 1,
//                        modifier = Modifier.padding(horizontal = 2.dp).background(
//                            color = backColor,
//                            shape = RoundedCornerShape(4.dp)
//                        )
//                            .padding(horizontal = 2.dp)
//                    )
                }
            }
        }
    }
}