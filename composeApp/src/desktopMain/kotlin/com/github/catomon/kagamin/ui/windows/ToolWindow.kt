package com.github.catomon.kagamin.ui.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import com.github.catomon.kagamin.WindowConfig
import com.github.catomon.kagamin.WindowDraggableArea
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.kagaminWindowDecoration
import com.github.catomon.kagamin.ui.EditPlaylist
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_icon64
import org.jetbrains.compose.resources.painterResource

val LocalToolWindow = compositionLocalOf<MutableState<ToolWindowState>> {
    error("No ToolWindowState provided")
}

data class ToolWindowState(
    val currentScreenState: ToolScreenState = ToolScreenState.EmptyScreen,
    val isVisible: Boolean = false,
    val onClose: () -> Unit = { }
)

sealed class ToolScreenState(
    val title: String = "Dialog"
) {
    object EmptyScreen : ToolScreenState("Empty screen")

    data class EditPlaylist(
        val playlist: Playlist, val onRename: (String) -> Unit, val onClose: () -> Unit
    ) : ToolScreenState("Edit playlist")
}

@Composable
fun ToolWindow(
    toolWindowState: ToolWindowState
) {
    DialogWindow(
        visible = toolWindowState.isVisible,
        onCloseRequest = {
            toolWindowState.onClose()
        },
        title = toolWindowState.currentScreenState.title,
        alwaysOnTop = true,
        resizable = false,
        undecorated = true,
        transparent = true,
        state = rememberDialogState(
            position = WindowPosition.PlatformDefault,
            size = DpSize(WindowConfig.HEIGHT.dp, WindowConfig.HEIGHT.dp)
        ),
        icon = painterResource(Res.drawable.kagamin_icon64)
    ) {
        WindowDraggableArea {
            KagaminTheme {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().kagaminWindowDecoration().background(color = KagaminTheme.background)) {
                    when (val currentScreenState = toolWindowState.currentScreenState) {
                        is ToolScreenState.EditPlaylist -> {
                            EditPlaylist(
                                playlist = currentScreenState.playlist,
                                onRename = currentScreenState.onRename,
                                onClose = {
                                    currentScreenState.onClose()
                                    toolWindowState.onClose()
                                }
                            )
                        }

                        else -> {
                            Button({
                                toolWindowState.onClose()
                            }) { Text("Close") }
                        }
                    }
                }
            }
        }
    }
}