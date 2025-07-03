package com.github.catomon.kagamin

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.PlaybackTabButton
import com.github.catomon.kagamin.ui.PlaylistsTabButton
import com.github.catomon.kagamin.ui.TracklistTabButton
import com.github.catomon.kagamin.ui.components.AddButton
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.components.ThumbnailTrackItem
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.util.TracklistManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

private const val REQUEST_READ_STORAGE: Int = 100

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = KagaminTheme.colors.background.toArgb()

        setContent {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .crossfade(false)
                    .build()
            }

            KagaminTheme {
                Scaffold {

                    val viewModel: KagaminViewModel = koinViewModel()

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_READ_STORAGE
                            )
                        }
                    }

                    CompositionLocalProvider(
                        LocalAppSettings provides viewModel.settings,
                    ) {
                        LibraryScreen(viewModel, Modifier.padding(it))
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                result.value = true
            }
        }
    }
}

val result = mutableStateOf(false)

@Composable
fun PlayerScreen() {

}

fun CurrentTrackHeader() {

}

@Composable
fun Tracklist(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?,
    tracks: List<AudioTrack>,
    onPlay: (AudioTrack) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }

    Column(Modifier
        .fillMaxSize()
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                color = KagaminTheme.backgroundTransparent,
                size = size,
                blendMode = BlendMode.SrcOut
            )
            drawContent()
        }, horizontalAlignment = Alignment.CenterHorizontally) {
        TrackThumbnail(
            currentTrack, Modifier.size(300.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(4.dp),
            modifier = Modifier
        ) {
            items(tracks.size) {
                val track = tracks[it]
                ThumbnailTrackItem(
                    it,
                    track,
                    tracklistManager,
                    viewModel,
                    currentTrack == track,
                    { onPlay(track) })
            }
        }
    }
}

//@Composable
//fun CompactPlayerScreen(
//    viewModel: KagaminViewModel,
//    modifier: Modifier = Modifier,
//) {
//    val currentTrack by viewModel.currentTrack.collectAsState()
//    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
//
//    val tabTransition: (Tabs) -> ContentTransform = { tab ->
//        when (tab) {
//            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
//            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
//            Tabs.TRACKLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
//            Tabs.PLAYLISTS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
//
//            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
//        }
//    }
//
//    Box(modifier) {
//        Background(currentTrack, Modifier.fillMaxSize())
//
//        Row {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .weight(0.99f)
//            ) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(color = KagaminTheme.backgroundTransparent)
//                ) {
////                    AppName(
////                        Modifier
////                            .height(25.dp).graphicsLayer(translationY = 2f)
////                            .clip(RoundedCornerShape(8.dp))
////                            .clickable(onClickLabel = "Open options") {
////                                navController.navigate(SettingsDestination.toString())
////                            })
//                    AppLogo(
//                        Modifier
//                            .padding(horizontal = 12.dp)
//                            .height(30.dp)
//                            .graphicsLayer(translationY = 2f)
//                            .clip(RoundedCornerShape(8.dp))
//                            .clickable {
//
//                            })
//                }
//
//                Box(Modifier
//                    .weight(0.99f)
//                    .fillMaxHeight()) {
//                    AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
//                        tabTransition(viewModel.currentTab)
//                    }) {
//                        when (it) {
//                            Tabs.PLAYBACK -> {
//                                CurrentTrackFrame(
//                                    viewModel,
//                                    currentTrack,
//                                    Modifier
//                                        .width(160.dp)
//                                        .fillMaxHeight()
//                                        .background(color = KagaminTheme.backgroundTransparent)
//                                )
//                            }
//
//                            Tabs.PLAYLISTS -> {
//                                Playlists(
//                                    viewModel,
//                                    Modifier
//                                        .align(Alignment.Center)
//                                        .fillMaxHeight()//.padding(start = 4.dp, end = 4.dp)
//                                )
//                            }
//
//                            Tabs.TRACKLIST -> {
//                                if (currentPlaylist.tracks.isEmpty()) {
//                                    Box(
//                                        Modifier
//                                            .fillMaxHeight()
//                                            .background(KagaminTheme.colors.backgroundTransparent),
//                                        contentAlignment = Alignment.Center
//                                    ) {
//                                        Text(
//                                            "Drop files or folders here",
//                                            textAlign = TextAlign.Center,
//                                            color = KagaminTheme.textSecondary
//                                        )
//                                    }
//                                } else {
//                                    Tracklist(viewModel, ) { }
//                                }
//                            }
//
//                            Tabs.OPTIONS -> TODO()
//
//                            Tabs.ADD_TRACKS -> {
//                                AddTracksTab(
//                                    viewModel, Modifier
//                                        .fillMaxHeight()
//                                        .align(Alignment.Center)
//                                )
//                            }
//
//                            Tabs.CREATE_PLAYLIST -> {
//                                CreatePlaylistTab(
//                                    viewModel, Modifier
//                                        .fillMaxHeight()
//                                        .align(Alignment.Center)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            Sidebar(viewModel)
//        }
//    }
//}

@Composable
fun Sidebar(
    viewModel: KagaminViewModel, modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxHeight()
            .width(32.dp)
            .background(color = KagaminTheme.backgroundTransparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier
                .width(32.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            PlaybackTabButton(
                {
                    if (viewModel.currentTab != Tabs.PLAYBACK) {
                        viewModel.currentTab = Tabs.PLAYBACK
                    }
                },
                color = if (viewModel.currentTab == Tabs.PLAYBACK) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            TracklistTabButton(
                {
                    if (viewModel.currentTab != Tabs.TRACKLIST) {
                        viewModel.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (viewModel.currentTab == Tabs.TRACKLIST) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (viewModel.currentTab != Tabs.PLAYLISTS) {
                        viewModel.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (viewModel.currentTab == Tabs.PLAYLISTS) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            AddButton(
                onClick = {
                    when (viewModel.currentTab) {
                        Tabs.PLAYLISTS -> {
                            viewModel.currentTab = Tabs.CREATE_PLAYLIST
                        }

                        Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                            viewModel.currentTab = Tabs.ADD_TRACKS
                        }

                        else -> {
                            viewModel.currentTab =
                                if (viewModel.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                        }
                    }
                },
                modifier = Modifier.weight(0.333f),
                color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall
            )
        }
    }
}

@Composable
fun LibraryScreen(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    var tracks by remember { mutableStateOf<Map<String, List<AudioTrack>>>(emptyMap()) }
    var isScanning by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var currentFolder by remember { mutableStateOf("") }

    val currentTrack by viewModel.currentTrack.collectAsState()

    LaunchedEffect(result) {
        tracks = fetchAudioTracks(context)

        isScanning = false
    }

    Box(
        modifier
            .fillMaxSize()
            .background(KagaminTheme.colors.background), contentAlignment = Alignment.Center
    ) {
        Background(currentTrack, Modifier.matchParentSize())

        if (currentFolder.isEmpty())
            AnimatedContent(isScanning) {
                if (it) {
                    Text("Scanning...")
                } else {
                    if (tracks.isEmpty()) {
                        Text("Empty.")
                    } else {
                        LazyColumn(Modifier.fillMaxHeight()) {
                            items(tracks.keys.size) {
                                Text(tracks.keys.elementAt(it), modifier = Modifier.clickable {
                                    currentFolder = tracks.keys.elementAt(it)
                                })
                            }
                        }
                    }
                }
            }
        else
            Tracklist(
                viewModel,
                currentTrack,
                tracks = tracks[currentFolder] ?: emptyList(),
                onPlay = { viewModel.play(it) })
    }
}

suspend fun fetchAudioTracks(context: Context): Map<String, List<AudioTrack>> = withContext(Dispatchers.IO) {
    println("trying..")

    val audioTracks = mutableListOf<Pair<String, AudioTrack>>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA
    )

    val selection = null //"${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val queryUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        queryUri,
        projection,
        selection,
        null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol) ?: ""
            val artist = cursor.getString(artistCol) ?: ""
            val album = cursor.getString(albumCol) ?: ""
            val duration = cursor.getLong(durationCol)
            val albumId = cursor.getLong(albumIdCol)
            val filePath = cursor.getString(dataCol)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val artworkUri = ContentUris.withAppendedId(
                "content://media/external/audio/albumart".toUri(),
                albumId
            ).toString()

            val folderName = try {
                File(filePath).parentFile?.name ?: "Unknown"
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown Folder"
            }

            println(title)

            audioTracks.add(
                folderName to AudioTrack(
                    id = id.toString(),
                    uri = contentUri.toString(),
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    artworkUri = artworkUri
                )
            )
        }
    }

    println("done.." + audioTracks.joinToString())

    audioTracks.groupBy { it.first }.map { it.key to it.value.map { it.second } }.toMap()
}