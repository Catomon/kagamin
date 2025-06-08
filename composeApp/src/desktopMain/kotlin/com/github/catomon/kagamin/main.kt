package com.github.catomon.kagamin

import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.request.crossfade
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.di.appModule
import com.github.catomon.kagamin.util.Rogga
import com.github.catomon.kagamin.util.Rogga.LogLevel
import com.github.catomon.kagamin.util.echoMsg
import com.github.catomon.kagamin.util.echoWarn
import io.github.vinceglb.filekit.FileKit
import okio.Path.Companion.toOkioPath
import org.jaudiotagger.tag.id3.AbstractID3Tag
import org.jetbrains.skiko.GraphicsApi
import org.koin.core.context.GlobalContext.startKoin
import java.util.logging.Level
import javax.swing.JOptionPane

fun main() {
    setDefaultExceptionHandler()

    FileKit.init(appId = "Kagamin")
    AbstractID3Tag.logger.level = Level.OFF

    Rogga.logLevel = LogLevel.TRACE
    Rogga.timestamp = true

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
            val renderApi = GraphicsApi.OPENGL.name
            System.setProperty("skiko.renderApi", renderApi)
            echoMsg { "skiko.renderApi = $renderApi" }
        } else {
            System.setProperty("skiko.renderApi", GraphicsApi.SOFTWARE_FAST.name)
            echoMsg { "skiko.renderApi = ${GraphicsApi.SOFTWARE_FAST.name}" }
        }
        WindowConfig.isTransparent = true
    } catch (e: Exception) {
        echoWarn { "Could not set desired render api. The window may not have transparency." }
        e.printStackTrace()
    }
}