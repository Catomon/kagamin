package com.github.catomon.kagamin

import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.request.crossfade
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.util.echoMsg
import com.github.catomon.kagamin.util.echoWarn
import com.github.catomon.kagamin.di.appModule
import io.github.vinceglb.filekit.FileKit
import okio.Path.Companion.toOkioPath
import org.koin.core.context.GlobalContext.startKoin
import javax.swing.JOptionPane

fun main() {
    setDefaultExceptionHandler()

    FileKit.init(appId = "Kagamin")

    application {
        setComposeExceptionHandler()

        startKoin {
            modules(appModule)
        }

        setRenderApi()

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .crossfade(false)
//                .memoryCachePolicy(policy = CachePolicy.DISABLED)
//                .precision(Precision.INEXACT)
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheFolder.toOkioPath().resolve("image_cache"))
                        .maxSizePercent(0.02)
                        .build()
                }
                .build()
        }

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