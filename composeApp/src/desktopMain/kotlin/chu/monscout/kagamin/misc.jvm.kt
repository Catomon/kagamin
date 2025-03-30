package chu.monscout.kagamin

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.audio.AudioTrackJVM
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File

val osName = System.getProperty("os.name").lowercase()

actual val userDataFolder: File =
    File(System.getProperty("user.home"), if (osName.contains("win")) "AppData/Roaming/Kagamin" else ".local/share/Kagamin")

actual fun <T : AudioTrack> createDenpaTrack(uri: String, name: String): T {
    return AudioTrackJVM(uri = uri, name = name) as T
}

fun ApplicationScope.setComposeExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        File("last_error.txt").writeText(e.stackTraceToString())
        e.printStackTrace()

        exitApplication()

        application {
            Window(
                onCloseRequest = ::exitApplication,
                state = rememberWindowState(width = 300.dp, height = 250.dp),
                visible = true,
                title = "Error",
            ) {
                val clipboard = LocalClipboardManager.current

                Box(contentAlignment = Alignment.Center) {
                    Text(e.stackTraceToString(), Modifier.fillMaxSize())
                    Button({
                        clipboard.setText(AnnotatedString(e.stackTraceToString()))
                    }, Modifier.align(Alignment.BottomCenter)) {
                        Text("Copy")
                    }
                }
            }
        }
    }
}

@Composable
actual fun DenpaFilePicker(
    show: MutableState<Boolean>,
    audioPlayer: AudioPlayer<AudioTrack>,
    currentPlaylistName: String
) {
    val a = LocalSnackbarHostState.current
    val fileType = listOf("mp3", "wav")
    MultipleFilePicker(show = show.value, fileExtensions = fileType) { files ->
        show.value = false
        if (files != null) {
            //it.platformFile desk - File, android - Uri
            audioPlayer.load(files.map { it.path })

            savePlaylist(currentPlaylistName, audioPlayer.playlist.value.toTypedArray())
        }

        //fixme
        GlobalScope.launch {
            a.showSnackbar("${files?.size ?: 0} tracks were added.")
        }
    }
}

@Composable
fun WindowScope.WindowDraggableArea(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val handler = remember { DragHandler(window) }

    Box(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown()
                handler.register()
            }
        }
    ) {
        content()
    }
}

private class DragHandler(private val window: Window) {
    private var location = window.location.toComposeOffset()
    private var pointStart = MouseInfo.getPointerInfo().location.toComposeOffset()

    private val dragListener = object : MouseMotionAdapter() {
        override fun mouseDragged(event: MouseEvent) = drag()
    }
    private val removeListener = object : MouseAdapter() {
        override fun mouseReleased(event: MouseEvent) {
            window.removeMouseMotionListener(dragListener)
            window.removeMouseListener(this)
        }
    }

    fun register() {
        location = window.location.toComposeOffset()
        pointStart = MouseInfo.getPointerInfo().location.toComposeOffset()
        window.addMouseListener(removeListener)
        window.addMouseMotionListener(dragListener)
    }

    private fun drag() {
        val point = MouseInfo.getPointerInfo().location.toComposeOffset()
        val location = location + (point - pointStart)
        window.setLocation(location.x, location.y)
    }

    private fun Point.toComposeOffset() = IntOffset(x, y)
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