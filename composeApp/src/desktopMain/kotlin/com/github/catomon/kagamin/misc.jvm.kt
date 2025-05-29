package com.github.catomon.kagamin

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
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
import com.github.catomon.kagamin.ui.util.LayoutManager
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.io.File
import kotlin.concurrent.thread

val osName = System.getProperty("os.name").lowercase()

fun ApplicationScope.setComposeExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        e.printStackTrace()

        try {
            File("last_error.txt").writeText(e.stackTraceToString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        thread {
            application {
                Window(
                    onCloseRequest = ::exitApplication,
                    state = rememberWindowState(width = 300.dp, height = 250.dp),
                    visible = true,
                    title = "Error",
                ) {
                    val clipboard = LocalClipboardManager.current

                    Box(contentAlignment = Alignment.Center) {
                        SelectionContainer {
                            Text(e.stackTraceToString(), Modifier.fillMaxSize().verticalScroll(
                                rememberScrollState()
                            ))
                        }
                        Button({
                            clipboard.setText(AnnotatedString(e.stackTraceToString()))
                        }, Modifier.align(Alignment.BottomCenter)) {
                            Text("Copy")
                        }
                    }
                }
            }
        }

        exitApplication()
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
