package chu.monscout.kagamin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
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
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }

    System.setProperty("skiko.renderApi", "OPENGL")

    val windowState = rememberWindowState(width = 480.dp, height = 200.dp)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kagamin",
        icon = painterResource(Res.drawable.kagamin32),
        state = windowState,
        undecorated = true,
        transparent = true,
    ) {
        CompositionLocalProvider(LocalWindow provides this.window) {
            App()
        }
    }
}

val LocalWindow = compositionLocalOf<ComposeWindow> {
    error("No window")
}

@Composable
fun WindowScope.App() {
    WindowDraggableArea {
        KagaminApp(modifier = Modifier.clip(RoundedCornerShape(12.dp)))
    }
}

@Composable
@Preview
private fun MainScreenPreview() {
    KagaminApp(KagaminViewModel(), Modifier.width(480.dp).height(200.dp))
}