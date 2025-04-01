package chu.monscout.kagamin.ui.windows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState

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
    Window(
        visible = true,
        onCloseRequest = {
            confirmWindowState.onCancel()
            confirmWindowState.onClose()
        },
        title = "Just asking.",
        alwaysOnTop = true,
        resizable = false,
        state = rememberWindowState(position = WindowPosition(Alignment.Center), size = DpSize(300.dp, 170.dp))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Are you sure?", modifier = Modifier.padding(bottom = 30.dp))

            Button({
                confirmWindowState.onCancel()
                confirmWindowState.onClose()
            }, modifier = Modifier.align(Alignment.BottomStart).padding(start = 12.dp)) {
                Text("No")
            }

            Button({
                confirmWindowState.onConfirm()
                confirmWindowState.onClose()
            }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp)) {
                Text("Yea")
            }
        }
    }
}