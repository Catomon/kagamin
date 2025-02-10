package chu.monscout.kagamin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import chu.monscout.kagamin.feature.KagaminApp
import chu.monscout.kagamin.feature.KagaminViewModel
import com.github.catomon.yukinotes.di.appModule
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin32
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get
import java.awt.datatransfer.DataFlavor
import java.io.File

fun main() = application {
    startKoin {
        modules(appModule)
    }

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState(width = 600.dp, height = 350.dp)
    val kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Kagamin",
        icon = painterResource(Res.drawable.kagamin32),
        state = windowState,
        undecorated = true,
        transparent = true,
    ) {
        CompositionLocalProvider(
            LocalWindow provides this.window,
        ) {
            YukiTheme {
                App()
            }
        }
    }
}

val LocalWindow = compositionLocalOf<ComposeWindow> {
    error("No window")
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun WindowScope.App(kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java)) {
    WindowDraggableArea {
        val snackbar =
            LocalSnackbarHostState.current
        KagaminApp(
            kagaminViewModel,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                .dragAndDropTarget({ true }, remember {
                    object : DragAndDropTarget {
                        override fun onStarted(event: DragAndDropEvent) {

                        }

                        override fun onEnded(event: DragAndDropEvent) {

                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            event.awtTransferable.let {
                                if (it.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                    val droppedFiles =
                                        it.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                                    try {
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
                                            snackbar.showSnackbar("${musicFiles.size} file's were added.")
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