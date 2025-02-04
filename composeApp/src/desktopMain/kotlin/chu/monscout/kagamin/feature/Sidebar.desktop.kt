package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.LocalWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.minimize_window
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun MinimizeButton(modifier: Modifier) {
    val window = LocalWindow.current
    TextButton({
        window.isMinimized = true
    }, modifier = modifier) {
        Image(
            painterResource(Res.drawable.minimize_window),
            "Minimize",
            modifier = Modifier.size(32.dp)
        )
    }
}