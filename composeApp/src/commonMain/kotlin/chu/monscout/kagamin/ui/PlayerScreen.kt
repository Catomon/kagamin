package chu.monscout.kagamin.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.createAudioPlayer
import kotlinx.serialization.Serializable
import chu.monscout.kagamin.loadSettings

enum class Tabs {
    TRACKLIST, PLAYLISTS, OPTIONS, ADD_TRACKS, CREATE_PLAYLIST, PLAYBACK
}

class KagaminViewModel : ViewModel() {
    val denpaPlayer = createAudioPlayer
    val playlist by denpaPlayer.playlist
    val currentTrack by denpaPlayer.currentTrack
    val playState by denpaPlayer.playState
    val playMode by denpaPlayer.playMode
    var currentPlaylistName by mutableStateOf("default")
    var showSongUrlInput by mutableStateOf(false)
    var showPlaylistsPane by mutableStateOf(false)
    var showOptionsPane by mutableStateOf(false)
    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<AudioTrack?>(null)
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    var height by mutableStateOf(0)
    var width by mutableStateOf(0)

    fun onPlayPause() {
        when (denpaPlayer.playState.value) {
            AudioPlayer.PlayState.PLAYING -> denpaPlayer.pause()
            AudioPlayer.PlayState.PAUSED -> denpaPlayer.resume()
            AudioPlayer.PlayState.IDLE -> denpaPlayer.resume()
        }
    }
}

@Serializable
object PlayerScreenDestination {
    override fun toString(): String {
        return "player_screen"
    }
}

@Composable
expect fun PlayerScreen(
    state: KagaminViewModel = viewModel { KagaminViewModel() },
    navController: NavHostController,
    modifier: Modifier = Modifier,
)