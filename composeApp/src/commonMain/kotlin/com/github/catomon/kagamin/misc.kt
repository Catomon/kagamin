package com.github.catomon.kagamin

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import java.awt.Desktop
import java.net.URI
import java.util.Locale

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State provided")
}

fun formatMilliseconds(ms: Long): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return if (ms > 3596400000L) "999:99:99" else String.format("%1d:%02d", minutes, seconds)
}

fun openGitHub() {
    openInBrowser(URI.create("https://github.com/Catomon"))
}

fun openInBrowser(url: String) {
    openInBrowser(URI.create(url))
}

fun openInBrowser(uri: URI) {
    val osName by lazy(LazyThreadSafetyMode.NONE) {
        System.getProperty("os.name").lowercase(Locale.getDefault())
    }
    val desktop = Desktop.getDesktop()
    when {
        Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(
            uri
        )

        "mac" in osName -> Runtime.getRuntime().exec("open $uri")
        "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec("xdg-open $uri")
        //else -> throw RuntimeException("cannot open $uri")
    }
}

fun isValidFileName(name: String): Boolean {
    if (name.isBlank()) return false

    val forbiddenNames = listOf(
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "COM0",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", "LPT0"
    )
    val specialChars = listOf("<", ">", ":", "\"", "/", "\\", "|", "?", "*")

    return !(forbiddenNames.any { name.contains(it, true) } || specialChars.any {
        name.contains(it, true)
    })
}