package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import audio.DenpaTrack
import com.github.catomon.yukinotes.feature.Colors
import createDenpaPlayer
import createDenpaTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import loadPlaylist
import loadSettings

enum class Tabs {
    TRACKLIST, PLAYLISTS, OPTIONS, ADD_TRACKS, CREATE_PLAYLIST,
}

class KagaminViewModel : ViewModel() {
    val denpaPlayer = createDenpaPlayer
    val playlist by denpaPlayer.playlist
    val currentTrack by denpaPlayer.currentTrack
    val playState by denpaPlayer.playState
    val playMode by denpaPlayer.playMode
    var currentPlaylistName by mutableStateOf("default")
    var showSongUrlInput by mutableStateOf(false)
    var showPlaylistsPane by mutableStateOf(false)
    var showOptionsPane by mutableStateOf(false)
    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<DenpaTrack?>(null)
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    var height by mutableStateOf(0)
    var width by mutableStateOf(0)
}

@Serializable
object PlayerScreenDestination {
    override fun toString(): String {
        return "player_screen"
    }
}

@Composable
fun PlayerScreen(
    state: KagaminViewModel = viewModel { KagaminViewModel() },
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    val denpaPlayer = state.denpaPlayer
    val playlist = state.playlist
    val currentTrack = state.currentTrack
    val playState = state.playState
    val playMode = state.playMode
    val currentPlaylistName = state.currentPlaylistName

    LaunchedEffect(currentPlaylistName) {
        CoroutineScope(Dispatchers.Default).launch {
            state.isLoadingPlaylistFile = true
            try {
                val trackUris = loadPlaylist(currentPlaylistName)?.tracks
                if (trackUris != null) {
                    denpaPlayer.playlist.value = mutableListOf()
                    trackUris.forEach {
                        denpaPlayer.addToPlaylist(createDenpaTrack(it.uri, it.name))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            state.isLoadingPlaylistFile = false
        }
    }

    Row(modifier.background(color = Colors.background)) {
        // Column(modifier.background(color = Colors.bars).fillMaxHeight()) {
        CurrentTrackFrame(currentTrack, denpaPlayer, Modifier.width(200.dp).fillMaxHeight())
        // }

        Box(Modifier.weight(0.75f).background(color = Colors.background)) {
            AnimatedContent(state.currentTab) {
                when (it) {
                    Tabs.PLAYLISTS -> {
                        Playlists(
                            state,
                            Modifier.align(Alignment.Center)
                                .fillMaxSize()//.padding(start = 4.dp, end = 4.dp)
                        )
                    }

                    Tabs.TRACKLIST -> {
                        if (state.playlist.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Drop files or folders here",
                                    textAlign = TextAlign.Center,
                                    color = Colors.noteText
                                )
                            }
                        } else {
                            Tracklist(
                                state,
                                state.playlist,
                                Modifier.align(Alignment.Center)
                                    .fillMaxSize()//.padding(start = 16.dp, end = 16.dp)
                            )
                        }
                    }

                    Tabs.OPTIONS -> TODO()

                    Tabs.ADD_TRACKS -> {
                        AddTracksTab(state, Modifier.fillMaxSize().align(Alignment.Center))
                    }

                    Tabs.CREATE_PLAYLIST -> {
                        CreatePlaylistTab(state, Modifier.fillMaxSize().align(Alignment.Center))
                    }
                }
            }
        }

        Sidebar(state, navController)
    }
}
