package com.github.catomon.kagamin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.WindowConfig.isTraySupported
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.ui.KagaminApp
import com.github.catomon.kagamin.ui.components.getThumbnail
import com.github.catomon.kagamin.ui.customShadow
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.AddTracksOrPlaylistsWindow
import com.github.catomon.kagamin.ui.windows.ConfirmWindow
import com.github.catomon.kagamin.ui.windows.ConfirmWindowState
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_icon64
import kagamin.composeapp.generated.resources.pause_icon
import kagamin.composeapp.generated.resources.play_icon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.get
import java.awt.datatransfer.DataFlavor
import java.io.File

@Composable
fun ApplicationScope.AppContainer(onCloseRequest: () -> Unit) {
    val kagaminViewModel: KagaminViewModel = remember { get(KagaminViewModel::class.java) }

    val layoutManager = remember {
        LayoutManager(
            LayoutManager.Layout.valueOf(
                kagaminViewModel.settings.extra["layout"] ?: "Default"
            )
        )
    }
    val currentLayout by layoutManager.currentLayout

    val confirmWindowState = remember { mutableStateOf(ConfirmWindowState()) }

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

    val windowState =
        rememberWindowState(size = windowSize)

    LaunchedEffect(windowSize) {
        windowState.size = windowSize
    }

    CompositionLocalProvider(
        LocalLayoutManager provides layoutManager, LocalConfirmWindow provides confirmWindowState
    ) {
        AppWindow(windowState, kagaminViewModel, onCloseRequest)
    }

    if (kagaminViewModel.createPlaylistWindow) {
        AddTracksOrPlaylistsWindow(kagaminViewModel)
    }

    if (isTraySupported) {
        val trayState = rememberTrayState()
        Tray(
            painterResource(if (kagaminViewModel.playState == AudioPlayer.PlayState.PLAYING) Res.drawable.pause_icon else Res.drawable.play_icon),
            tooltip = kagaminViewModel.currentTrack?.name,
            onAction = {
                kagaminViewModel.onPlayPause()
            },
            state = trayState
        ) {
            Item("Exit", onClick = {
                onCloseRequest()
            })
        }
    }

    if (confirmWindowState.value.isVisible) {
        ConfirmWindow(confirmWindowState.value)
    }
}

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
        CompositionLocalProvider(
            LocalWindow provides this.window,
        ) {
            KagaminTheme {
                AppFrame(kagaminViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WindowScope.AppFrame(kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java)) {
    WindowDraggableArea {
        val snackbar = LocalSnackbarHostState.current
        KagaminApp(
            kagaminViewModel,
            modifier = Modifier.kagaminWindowDecoration().dragAndDropTarget({ true }, remember {
                createTrackDragAndDropTarget(kagaminViewModel, snackbar)
            })
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
private fun createTrackDragAndDropTarget(
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
                    loadTrackFiles(droppedFiles)
                }

                return true
            } else {
                return false
            }
        }
    }

    private suspend fun loadTrackFiles(droppedFiles: List<File>): Boolean {
        try {
            val trackFiles = ArrayList<File>(1000)

            snackbar.showSnackbar("Filtering files...")

            withContext(Dispatchers.IO) {
                fun filterMusicFiles(files: List<File>) {
                    for (file in files) {
                        if (file.isDirectory) {
                            file.listFiles()?.let { filterMusicFiles(it.toList()) }
                        } else {
                            if (file.extension == "mp3" || file.extension == "wav") {
                                trackFiles.add(file)
                            }
                        }
                    }
                }

                filterMusicFiles(droppedFiles)
            }

            snackbar.showSnackbar("Caching thumbnails...")

            withContext(Dispatchers.IO) {
                trackFiles.forEach {
                    getThumbnail(it.path)
                }
            }

            snackbar.showSnackbar("Adding tracks...")

            kagaminViewModel.audioPlayer.load(trackFiles.map { it.path })

            withContext(Dispatchers.IO) {
                savePlaylist(
                    kagaminViewModel.currentPlaylistName,
                    kagaminViewModel.audioPlayer.playlist.value.toTypedArray()
                )
            }

            snackbar.showSnackbar("${trackFiles.size} tracks were added.")
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()

            snackbar.showSnackbar(ex.message ?: "null")
            return false
        }
    }
}