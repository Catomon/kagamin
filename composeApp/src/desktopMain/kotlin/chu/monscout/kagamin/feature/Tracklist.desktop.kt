package chu.monscout.kagamin.feature

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun TrackItem(
    index: Int,
    track: DenpaTrack,
    tracklistManager: TracklistManager,
    state: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val clipboard = LocalClipboardManager.current
    val isHeader = index == -1
    val backColor = if (isHeader) Colors.bars.copy(alpha = 0.50f) else
        if (index % 2 == 0) Colors.dividers.copy(alpha = 0.50f) else Colors.background.copy(alpha = 0.50f)
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Select") {
                if (!isHeader)
                tracklistManager.select(index, track)
            },
            if (tracklistManager.isAnySelected) {
                ContextMenuItem("Deselect All") {
                    tracklistManager.deselectAll()
                }
            } else {
                ContextMenuItem("Copy URI") {
                    clipboard.setText(AnnotatedString(track.uri))
                }
            },
            ContextMenuItem(if (tracklistManager.isAnySelected) "Remove selected" else "Remove") {
                tracklistManager.contextMenuRemovePressed(state, track)
            },
        )
    }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(32.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            if (index > -1 && state.currentTrack == track) {
                Box(Modifier.height(32.dp).clip(
                    RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)).background(Colors.bars.copy(0.5f))
                , contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(if (state.playState == DenpaPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                        "track playback state icon",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Colors.noteBackground)
                    )
                }
            }

            Box(
                modifier = modifier.fillMaxWidth().height(32.dp)
                    .background(color = backColor)
                    .clickable {
                        onClick()
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    track.name,
                    fontSize = 12.sp,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart)//.let {
//                        if (state.currentTrack == track && !isHeader) it.drawBehind {
//                            val strokeWidthPx = 1.dp.toPx()
//                            val verticalOffset = size.height - 2.sp.toPx()
//                            drawLine(
//                                color = Colors.noteText,
//                                strokeWidth = strokeWidthPx,
//                                start = Offset(0f, verticalOffset),
//                                end = Offset(size.width, verticalOffset)
//                            )
//                        } else it
//                    },
                            ,
                    overflow = TextOverflow.Ellipsis,
                )



                Row(
                    Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tracklistManager.selected.contains(index))
                        Icon(painterResource(Res.drawable.selected), null)
                }
            }
        }
    }
}