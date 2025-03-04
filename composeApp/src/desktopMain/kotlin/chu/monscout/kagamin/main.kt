package chu.monscout.kagamin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.feature.KagaminApp
import chu.monscout.kagamin.feature.KagaminViewModel
import com.github.catomon.yukinotes.di.appModule
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_icon64
import kagamin.composeapp.generated.resources.pause_icon
import kagamin.composeapp.generated.resources.play_icon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.swing.JOptionPane

var isOpenGl = false
val isTraySupported = androidx.compose.ui.window.isTraySupported

fun main() {

    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        JOptionPane.showMessageDialog(
            null,
            e.stackTraceToString(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }

    application {
        setExceptionHandler()

        startKoin {
            modules(appModule)
        }

        try {
            System.setProperty("skiko.renderApi", "OPENGL")
            isOpenGl = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        AppContainer(::exitApplication)
    }
}

@Composable
fun ApplicationScope.AppContainer(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(width = 600.dp, height = 350.dp) // 212 328
    val kagaminViewModel: KagaminViewModel = remember { get(KagaminViewModel::class.java) }
    val layoutManager = remember { LayoutManager() }
    val currentLayout by layoutManager.currentLayout

    LaunchedEffect(currentLayout) {
        when (currentLayout) {
            LayoutManager.Layout.Default -> {
                windowState.size = DpSize(600.dp, 350.dp)
            }

            LayoutManager.Layout.Compact -> {
                windowState.size = DpSize(212.dp, 328.dp)
            }

            LayoutManager.Layout.Tiny -> {
                windowState.size = DpSize(200.dp, 200.dp)
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutManager provides layoutManager
    ) {
        PlayerWindow(windowState, kagaminViewModel, onCloseRequest)
    }

    if (isTraySupported) {
        val trayState = rememberTrayState()
        Tray(
            painterResource(if (kagaminViewModel.playState == DenpaPlayer.PlayState.PLAYING) Res.drawable.pause_icon else Res.drawable.play_icon),
            tooltip = kagaminViewModel.currentTrack?.name,
            onAction = {
                kagaminViewModel.onPlayPause()
            },
            state = trayState
        )
    }
}

@Composable
private fun PlayerWindow(
    windowState: WindowState,
    kagaminViewModel: KagaminViewModel,
    onCloseRequest: () -> Unit
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
        transparent = isOpenGl,
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
                }
                false
            } else {
                false
            }
        }
    ) {
        CompositionLocalProvider(
            LocalWindow provides this.window,
        ) {
            YukiTheme {
                App(kagaminViewModel)
            }
        }
    }
}

val LocalLayoutManager = compositionLocalOf<LayoutManager> {
    error("no layout manager provided")
}

val LocalWindow = compositionLocalOf<ComposeWindow> {
    error("No window")
}

class LayoutManager(
    val currentLayout: MutableState<Layout> = mutableStateOf(Layout.Default)
) {
    enum class Layout {
        Default,
        Compact,
        Tiny,
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun WindowScope.App(kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java)) {
    WindowDraggableArea {
        val snackbar =
            LocalSnackbarHostState.current
        KagaminApp(
            kagaminViewModel,
            modifier = Modifier.let { if (isOpenGl) it.clip(RoundedCornerShape(12.dp)) else it }
                .dragAndDropTarget({ true }, remember {
                    object : DragAndDropTarget {
                        override fun onStarted(event: DragAndDropEvent) {

                        }

                        override fun onEnded(event: DragAndDropEvent) {

                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            event.awtTransferable.let {
                                if (it.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                    try {
                                        val droppedFiles =
                                            it.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                                        val musicFiles = ArrayList<File>(1000)
                                        fun filterMusicFiles(files: List<File>) {
                                            for (file in files) {
                                                if (file.isDirectory) {
                                                    file.listFiles()
                                                        ?.let { filterMusicFiles(it.toList()) }
                                                } else {
                                                    if (file.extension == "mp3" || file.extension == "wav") {
                                                        musicFiles.add(file)
                                                    }
                                                }
                                            }
                                        }

                                        filterMusicFiles(droppedFiles)

                                        kagaminViewModel.denpaPlayer.load(musicFiles.map { it.path })
                                        savePlaylist(
                                            kagaminViewModel.currentPlaylistName,
                                            kagaminViewModel.denpaPlayer.playlist.value.toTypedArray()
                                        )

                                        //fixme
                                        GlobalScope.launch {
                                            snackbar.showSnackbar("${musicFiles.size} files were added.")
                                        }
                                        return true
                                    } catch (ex: Exception) {
                                        ex.printStackTrace()

                                        //fixme
                                        GlobalScope.launch {
                                            snackbar.showSnackbar(ex.message ?: "null")
                                        }
                                        return false
                                    }
                                } else {
                                    return false
                                }
                            }
                        }
                    }
                })
        )
    }
}

@Composable
@Preview
private fun MainScreenPreview() {
    YukiTheme {
        KagaminApp(KagaminViewModel(), Modifier.width(480.dp).height(200.dp))
    }
}