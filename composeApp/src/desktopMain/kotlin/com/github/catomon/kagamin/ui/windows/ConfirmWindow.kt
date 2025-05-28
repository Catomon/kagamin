package com.github.catomon.kagamin.ui.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import com.github.catomon.kagamin.WindowDraggableArea
import com.github.catomon.kagamin.kagaminWindowDecoration
import com.github.catomon.kagamin.ui.components.OutlinedTextButton
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_icon64
import org.jetbrains.compose.resources.painterResource

val LocalConfirmWindow = compositionLocalOf<MutableState<ConfirmWindowState>> {
    error("No ConfirmWindowState provided")
}

data class ConfirmWindowState(
    val isVisible: Boolean = false,
    val onConfirm: () -> Unit = { },
    val onCancel: () -> Unit = { },
    val onClose: () -> Unit = { }
)

@Composable
fun ConfirmWindow(
    confirmWindowState: ConfirmWindowState
) {
    DialogWindow(
        visible = confirmWindowState.isVisible,
        onCloseRequest = {
            confirmWindowState.onCancel()
            confirmWindowState.onClose()
        },
        title = "Just asking.",
        alwaysOnTop = true,
        resizable = false,
        undecorated = true,
        transparent = true,
        state = rememberDialogState(
            position = WindowPosition.PlatformDefault,
            size = DpSize(300.dp, 170.dp)
        ),
        icon = painterResource(Res.drawable.kagamin_icon64)
    ) {
        WindowDraggableArea {
            KagaminTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize().kagaminWindowDecoration().background(color = KagaminTheme.background)
                ) {
                    Text("Are you sure?", modifier = Modifier.padding(bottom = 30.dp))

                    OutlinedTextButton(
                        text = "Yea", {
                            confirmWindowState.onConfirm()
                            confirmWindowState.onClose()
                        }, modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
                    )

                    OutlinedTextButton(
                        text = "No", {
                            confirmWindowState.onCancel()
                            confirmWindowState.onClose()
                        }, modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                    )
                }
            }
        }
    }
}