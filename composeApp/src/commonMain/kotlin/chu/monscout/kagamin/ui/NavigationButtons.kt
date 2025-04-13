package chu.monscout.kagamin.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.ui.components.ImageWithShadow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.music_note
import kagamin.composeapp.generated.resources.playlists
import kagamin.composeapp.generated.resources.tiny_star_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TracklistTabButton(
    onClick: () -> Unit,
    color: Color = Colors.theme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.music_note),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun PlaylistsTabButton(
    onClick: () -> Unit,
    color: Color = Colors.theme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.playlists),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun PlaybackTabButton(
    onClick: () -> Unit,
    color: Color = Colors.theme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.tiny_star_icon),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}