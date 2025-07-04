package com.github.catomon.kagamin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import com.github.catomon.kagamin.ui.PlaybackTabButton
import com.github.catomon.kagamin.ui.PlaylistsTabButton
import com.github.catomon.kagamin.ui.TracklistTabButton
import com.github.catomon.kagamin.ui.components.AddButton
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.screens.PlayerScreen
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import org.koin.compose.viewmodel.koinViewModel

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
                        PlayerScreen(viewModel, Modifier.padding(it))
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
