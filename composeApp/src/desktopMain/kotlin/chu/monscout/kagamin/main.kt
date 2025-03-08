package chu.monscout.kagamin

import androidx.compose.ui.window.application
import chu.monscout.kagamin.util.echoMsg
import chu.monscout.kagamin.util.echoWarn
import com.github.catomon.yukinotes.di.appModule
import org.koin.core.context.GlobalContext.startKoin
import javax.swing.JOptionPane

fun main() {
    setDefaultExceptionHandler()

    application {
        setComposeExceptionHandler()

        startKoin {
            modules(appModule)
        }

        setRenderApi()

        AppContainer(::exitApplication)
    }
}

private fun setDefaultExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        JOptionPane.showMessageDialog(
            null,
            e.stackTraceToString(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}

private fun setRenderApi() {
    try {
        if (osName.contains("win")) {
            System.setProperty("skiko.renderApi", "OPENGL")
            echoMsg("skiko.renderApi = OPENGL")
        } else {
            System.setProperty("skiko.renderApi", "SOFTWARE_FAST")
            echoMsg("skiko.renderApi = SOFTWARE_FAST")
        }
        WindowConfig.isTransparent = true
    } catch (e: Exception) {
        echoWarn("Could not set render api. The window may not have transparency.")
        e.printStackTrace()
    }
}