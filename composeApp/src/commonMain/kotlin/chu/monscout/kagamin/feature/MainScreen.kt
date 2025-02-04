package chu.monscout.kagamin.feature

import DenpaFilePicker
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import audio.DenpaTrack
import com.github.catomon.yukinotes.feature.Colors
import createDenpaPlayer
import createDenpaTrack
import isValidFileName
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.folder
import kagamin.composeapp.generated.resources.menu
import kagamin.composeapp.generated.resources.playlists
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import loadPlaylist
import loadSettings
import org.jetbrains.compose.resources.painterResource
import savePlaylist

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

@Composable
fun MainScreen(
    state: KagaminViewModel = viewModel { KagaminViewModel() },
    modifier: Modifier = Modifier
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

        Sidebar(state)
    }
}

@Composable
private fun Sidebar(state: KagaminViewModel) {
    Column(Modifier.fillMaxHeight().width(32.dp).background(color = Colors.bars)) {
        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {

            }
        ) {
            Image(
                painterResource(Res.drawable.menu),
                "Menu",
                modifier = Modifier.size(32.dp)
            )
        }

        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {
                if (state.currentTab == Tabs.TRACKLIST)
                    state.currentTab = Tabs.PLAYLISTS
                else state.currentTab = Tabs.TRACKLIST
            }
        ) {
            Image(
                painterResource(Res.drawable.playlists),
                "Playlists",
                modifier = Modifier.size(32.dp)
            )
        }

        if (state.currentTab == Tabs.PLAYLISTS || state.currentTab == Tabs.TRACKLIST) {
            TextButton(
                modifier = Modifier.weight(0.3f),
                onClick = {
                    when (state.currentTab) {
                        Tabs.PLAYLISTS -> {
                            state.currentTab = Tabs.CREATE_PLAYLIST
                        }

                        Tabs.TRACKLIST -> {
                            state.currentTab = Tabs.ADD_TRACKS
                        }

                        else -> {
                            //unreachable
                        }
                    }
                }
            ) {
                Image(
                    painterResource(Res.drawable.add),
                    "Add button",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CreatePlaylistTab(state: KagaminViewModel, modifier: Modifier) {
    var name by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(name, onValueChange = {
            name = it
        }, isError = isError)

        Button(onClick = {
            if (isValidFileName(name)) {
                state.currentPlaylistName = name
                savePlaylist(name, emptyArray())
                //state.playlists = loadPlaylists()
                //name = ""

                state.currentTab = Tabs.PLAYLISTS
            } else
                isError = true
        }) {
            Text("Create")
        }
    }
}

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
