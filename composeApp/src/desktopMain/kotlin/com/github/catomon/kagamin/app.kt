package com.github.catomon.kagamin

import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.WindowConfig.isTraySupported
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.PlaylistsLoader
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.data.loadSettings
import com.github.catomon.kagamin.ui.KagaminApp
import com.github.catomon.kagamin.ui.audioExtensions
import com.github.catomon.kagamin.ui.compositionlocals.LocalAppSettings
import com.github.catomon.kagamin.ui.customShadow
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.AddTracksOrPlaylistsWindow
import com.github.catomon.kagamin.ui.windows.ConfirmWindow
import com.github.catomon.kagamin.ui.windows.ConfirmWindowState
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import com.github.catomon.kagamin.ui.windows.LocalToolWindow
import com.github.catomon.kagamin.ui.windows.ToolWindow
import com.github.catomon.kagamin.ui.windows.ToolWindowState
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_icon64
import kagamin.composeapp.generated.resources.pause_icon
import kagamin.composeapp.generated.resources.play_icon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.get
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun ApplicationScope.AppContainer(onCloseRequest: () -> Unit) {
    val kagaminViewModel: KagaminViewModel = remember { get(KagaminViewModel::class.java) }
    var openPlayerWindow by remember { mutableStateOf(true) }

    val layoutManager = remember {
        LayoutManager(
            try {
                LayoutManager.Layout.valueOf(
                    kagaminViewModel.settings.extra["layout"]
                        ?: LayoutManager.Layout.BottomControls.name
                )
            } catch (e: Exception) {
                e.printStackTrace()
                LayoutManager.Layout.BottomControls
            }
        )
    }
    val currentLayout by layoutManager.currentLayout

    val exitApp = {
        kagaminViewModel.saveSettings()
        onCloseRequest()
    }

    val windowSize = remember(currentLayout) {
        when (currentLayout) {
            LayoutManager.Layout.Default -> DpSize(
                width = WindowConfig.WIDTH.dp, height = WindowConfig.HEIGHT.dp
            )

            LayoutManager.Layout.Compact -> DpSize(
                width = WindowConfig.COMPACT_WIDTH.dp, height = WindowConfig.COMPACT_HEIGHT.dp
            )

            LayoutManager.Layout.Tiny -> DpSize(
                width = WindowConfig.TINY_WIDTH.dp, height = WindowConfig.TINY_HEIGHT.dp
            )

            LayoutManager.Layout.BottomControls -> DpSize(
                width = WindowConfig.BOTTOM_CONTROLS_WIDTH.dp,
                height = WindowConfig.BOTTOM_CONTROLS_HEIGHT.dp
            )
        }
    }

    if (openPlayerWindow) {
        val windowState =
            rememberWindowState(size = windowSize, position = WindowPosition(Alignment.Center))

        LaunchedEffect(windowSize) {
            windowState.size = windowSize
        }

        LaunchedEffect(windowState.isMinimized) {
            if (windowState.isMinimized)
                openPlayerWindow = false
        }

        CompositionLocalProvider(
            LocalLayoutManager provides layoutManager,
            LocalAppSettings provides kagaminViewModel.settings,
        ) {
            AppWindow(windowState, kagaminViewModel, exitApp)
        }
    }

    if (kagaminViewModel.createPlaylistWindow) {
        AddTracksOrPlaylistsWindow(kagaminViewModel)
    }

    if (isTraySupported) {
        val trayState = rememberTrayState()
        Tray(
            painterResource(if (kagaminViewModel.playState.value == AudioPlayerService.PlayState.PLAYING) Res.drawable.pause_icon else Res.drawable.play_icon),
            tooltip = kagaminViewModel.currentTrack.value?.title,
            onAction = {
                openPlayerWindow = !openPlayerWindow
            },
            state = trayState
        ) {
            Item("Kagamin", onClick = {
                openPlayerWindow = true
            })
            Item(
                if (kagaminViewModel.playState.value == AudioPlayerService.PlayState.PLAYING) "Pause" else "Play",
                onClick = {
                    kagaminViewModel.onPlayPause()
                })
            Item("Exit", onClick = {
                exitApp()
            })
        }
    }
}

//    //    if (isTraySupported) {
//    //        var lastClickTime by remember { mutableStateOf(0L) }
//    //        var clickJob by remember { mutableStateOf<Job?>(null) }
//    //
//    //        Tray(
//    //            iconContent = {
//    //                Image(
//    //                    painterResource(if (kagaminViewModel.playState.value == AudioPlayerService.PlayState.PLAYING) Res.drawable.pause_icon else Res.drawable.play_icon),
//    //                    null, modifier = Modifier.fillMaxSize()
//    //                )
//    //            },
//    //            tooltip = kagaminViewModel.currentTrack.value?.title ?: "Kagamin - Idle",
//    //            primaryAction = {
//    //                val currentTime = System.currentTimeMillis()
//    //                if (currentTime - lastClickTime < 300) {
//    //                    clickJob?.cancel()
//    //                    openPlayerWindow = !openPlayerWindow
//    //                } else {
//    //                    clickJob?.cancel()
//    //                    clickJob = CoroutineScope(Dispatchers.Main).launch {
//    //                        delay(300)
//    //                        kagaminViewModel.onPlayPause()
//    //                    }
//    //                }
//    //                lastClickTime = currentTime
//    //            },
//    //        ) {
//    //            Item("Kagamin", onClick = {
//    //                openPlayerWindow = true
//    //            })
//    //            Item(
//    //                if (kagaminViewModel.playState.value == AudioPlayerService.PlayState.PLAYING) "Pause" else "Play",
//    //                onClick = {
//    //                    kagaminViewModel.onPlayPause()
//    //                })
//    //            Item("Exit", onClick = {
//    //                onCloseRequest()
//    //            })
//    //        }
//    //    }

@Composable
private fun AppWindow(
    windowState: WindowState, kagaminViewModel: KagaminViewModel, onCloseRequest: () -> Unit
) {
    val layoutManager = LocalLayoutManager.current

    val resizable: Boolean
    when (layoutManager.currentLayout.value) {
        LayoutManager.Layout.Default -> {
            resizable = true
        }

        LayoutManager.Layout.Compact -> {
            resizable = false
        }

        LayoutManager.Layout.Tiny -> {
            resizable = false
        }

        else -> {
            resizable = true
        }
    }

    val confirmWindowState = remember { mutableStateOf(ConfirmWindowState()) }

    val toolWindowState = remember { mutableStateOf(ToolWindowState()) }

    Window(
        onCloseRequest = onCloseRequest,
        title = "Kagamin",
        icon = painterResource(Res.drawable.kagamin_icon64),
        state = windowState,
        undecorated = true,
        resizable = resizable,
        transparent = WindowConfig.isTransparent,
        alwaysOnTop = loadSettings().alwaysOnTop,
        onPreviewKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.F2) {
                when (layoutManager.currentLayout.value) {
                    LayoutManager.Layout.Default -> {
                        layoutManager.currentLayout.value = LayoutManager.Layout.Compact
                    }

                    LayoutManager.Layout.Compact -> {
                        layoutManager.currentLayout.value = LayoutManager.Layout.Tiny
                    }

                    LayoutManager.Layout.Tiny -> {
                        layoutManager.currentLayout.value = LayoutManager.Layout.Default
                    }

                    LayoutManager.Layout.BottomControls -> LayoutManager.Layout.Default
                }
                false
            } else {
                false
            }
        }) {
        window.minimumSize = WindowConfig.minSize
        window.maximumSize = WindowConfig.maxSize

        CompositionLocalProvider(
            LocalWindow provides this.window,
            LocalConfirmWindow provides confirmWindowState,
            LocalToolWindow provides toolWindowState
        ) {
            KagaminTheme {
                AppFrame(kagaminViewModel)
            }
        }

        if (confirmWindowState.value.isVisible) {
            ConfirmWindow(confirmWindowState.value)
        }

        if (toolWindowState.value.isVisible) {
            ToolWindow(toolWindowState.value)
        }
    }
}

@Composable
private fun WindowScope.AppFrame(kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java)) {
    val settings = kagaminViewModel.settings //trigger recomposition
    WindowDraggableArea {
        val snackbar = LocalSnackbarHostState.current
        KagaminApp(
            kagaminViewModel,
            modifier = Modifier.kagaminWindowDecoration().dragAndDropTarget({ true }, remember {
                createTrackDragAndDropTarget(kagaminViewModel, snackbar)
            }).onPreviewKeyEvent {
                return@onPreviewKeyEvent when (it.key) {
                    Key.MediaPlay -> {
                        kagaminViewModel.onPlayPause(); true
                    }

                    else -> false
                }
            }
        )
    }
}

fun Modifier.kagaminWindowDecoration() =
    if (WindowConfig.isTransparent) this.padding(8.dp).customShadow().drawBehind {
        drawRoundRect(
            color = KagaminTheme.colors.thinBorder,
            topLeft = Offset(0f, 2f),
            size = this.size.copy(),
            cornerRadius = CornerRadius(12f)
        )
    }.clip(RoundedCornerShape(12.dp))
    else this.customShadow(cornerRadius = 0.dp).border(
        2.dp, KagaminTheme.colors.thinBorder, RectangleShape
    )


@OptIn(ExperimentalComposeUiApi::class)
fun createTrackDragAndDropTarget(
    kagaminViewModel: KagaminViewModel, snackbar: SnackbarHostState
) = object : DragAndDropTarget {
    override fun onStarted(event: DragAndDropEvent) {

    }

    override fun onEnded(event: DragAndDropEvent) {

    }

    override fun onDrop(event: DragAndDropEvent): Boolean {
        event.awtTransferable.let {
            if (it.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                val droppedFiles = it.getTransferData(DataFlavor.javaFileListFlavor) as List<File>

                kagaminViewModel.viewModelScope.launch {
                    loadTrackFilesToCurrentPlaylist(droppedFiles, kagaminViewModel, snackbar)
                }

                return true
            } else {
                return false
            }
        }
    }
}

class AudioTagsReader private constructor(
    val header: AudioHeader?,
    val tag: Tag?,
) {
    companion object {
        val supportedExtensions = listOf(
            "mp3",
            "flac",
            "ogg",
            "mp4",
            "aiff",
            "wav",
            "wma",
            "dsf",
            "opus",
        )

        fun isFormatSupported(ext: String) =
            ext.lowercase() in supportedExtensions

        fun read(file: File): AudioTagsReader? =
            try {
                if (isFormatSupported(file.extension)) {
                    val audioFile = AudioFileIO.read(file)
                    AudioTagsReader(audioFile.audioHeader, audioFile.tag)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
    }

    operator fun component1() = header
    operator fun component2() = tag
}

@OptIn(ExperimentalUuidApi::class)
suspend fun loadTrackFilesToCurrentPlaylist(
    files: List<File>,
    kagaminViewModel: KagaminViewModel,
    snackbar: SnackbarHostState? = null
): Boolean {
    val snackbarScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    val cachingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    try {
        val trackFiles = mutableListOf<File>()

        println("Filtering files...")
        if (snackbar != null)
            snackbarScope.launch {
                launch {
                    snackbar.currentSnackbarData?.dismiss()
                    snackbar.showSnackbar(
                        "Filtering files...",
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }

        withContext(Dispatchers.IO) {
            filterAudioFiles(files, trackFiles)
        }

        println("Reading metadata...")
        if (snackbar != null)
            snackbarScope.launch {
                launch {
                    snackbar.currentSnackbarData?.dismiss()
                    snackbar.showSnackbar(
                        "Reading metadata...",
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }

        val loadedTracks = withContext(Dispatchers.IO) {
            trackFiles.map { audioFile ->
                val path = audioFile.path

                val audioHeader: AudioHeader?
                val tag: Tag?
                AudioTagsReader.read(audioFile).let {
                    audioHeader = it?.header
                    tag = it?.tag
                }

                cachingScope.launch {
                    ThumbnailCacheManager.cacheThumbnail(trackUri = path, retrieveImage = {
                        tag?.firstArtwork?.image as BufferedImage?
                    })
                }

                fun Tag.getOrNull(key: FieldKey): String? =
                    if (tag?.hasField(key) == true) getFirst(key) else null

                val preciseLengthInSeconds: Double = audioHeader?.preciseTrackLength ?: 0.0
                val preciseLengthInMilliseconds = (preciseLengthInSeconds * 1000).toLong()

                AudioTrack(
                    id = Uuid.random().toString(),
                    uri = path,
                    title = tag?.getOrNull(FieldKey.TITLE)?.ifBlank { null }
                        ?: audioFile.nameWithoutExtension,
                    artist = tag?.getOrNull(FieldKey.ARTIST) ?: "",
                    album = tag?.getOrNull(FieldKey.ALBUM) ?: "",
                    duration = preciseLengthInMilliseconds,
                    artworkUri = null
                )
            }
        }

        println("Adding tracks...")
        if (snackbar != null)
            snackbarScope.launch {
                launch {
                    snackbar.currentSnackbarData?.dismiss()
                    snackbar.showSnackbar(
                        "Adding tracks...",
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }

        val currentTracks = kagaminViewModel.currentPlaylist.value.tracks
        val uniqueTracks =
            loadedTracks.filter { loadedTrack -> currentTracks.none { it.uri == loadedTrack.uri } }

        if (uniqueTracks.isNotEmpty()) {
            val updatedPlaylist =
                kagaminViewModel.currentPlaylist.value.copy(tracks = uniqueTracks + currentTracks)

            withContext(Dispatchers.IO) {
                PlaylistsLoader.savePlaylist(updatedPlaylist)
            }

            withContext(Dispatchers.Main) {
                kagaminViewModel.updatePlaylist(updatedPlaylist)
            }
        }

        println("${uniqueTracks.size} tracks were added.")
        if (snackbar != null)
            snackbarScope.launch {
                launch {
                    snackbar.currentSnackbarData?.dismiss()
                    snackbar.showSnackbar("${uniqueTracks.size} tracks were added.")
                }
            }

        return true
    } catch (ex: Exception) {
        ex.printStackTrace()

        if (snackbar != null)
            snackbarScope.launch {
                snackbar.currentSnackbarData?.dismiss()
                snackbar.showSnackbar(ex.message ?: "null")
            }
        return false
    }
}

fun filterAudioFiles(files: List<File>, filteredFiles: MutableList<File>) {
    for (file in files) {
        if (file.isDirectory) {
            file.listFiles()?.let { filterAudioFiles(it.toList(), filteredFiles) }
        } else {
            if (file.extension in audioExtensions) {
                filteredFiles.add(file)
            }
        }
    }
}
