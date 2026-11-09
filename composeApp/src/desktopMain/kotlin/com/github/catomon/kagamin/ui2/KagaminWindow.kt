package com.github.catomon.kagamin.ui2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.github.catomon.kagamin.WindowDraggableArea
import com.github.catomon.kagamin.ui2.components.Ui2OutlinedText
import com.github.catomon.kagamin.ui2.components.ui2WindowDecoration
import com.github.catomon.kagamin.ui2.theme.Ui2Theme
import com.github.catomon.kagamin.ui2.theme.Ui2ThemeState
import com.github.catomon.kagamin.ui2.theme.Ui2Themes
import com.github.catomon.kagamin.ui2.theme.rememberUi2ThemeState
import java.awt.Dimension

/**
 * The base ui2 window: undecorated + transparent, Tsukaremi-style gradient
 * frame with a glowing border, a themed header and an empty content slot.
 *
 * Usage:
 * ```
 * fun main() = application {
 *     KagaminWindow(onCloseRequest = ::exitApplication) {
 *         // your ui2 content
 *     }
 * }
 * ```
 */
@Composable
fun KagaminWindow(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Kagamin",
    themeState: Ui2ThemeState = rememberUi2ThemeState(Ui2Themes.Tsukaremi),
    state: WindowState = rememberWindowState(
        size = DpSize(600.dp, 600.dp),
        position = WindowPosition(Alignment.Center),
    ),
    content: @Composable () -> Unit = {},
) {
    Window(
        onCloseRequest = onCloseRequest,
        title = title,
        state = state,
        undecorated = true,
        transparent = true,
        resizable = true,
    ) {
        window.minimumSize = Dimension(280, 280)

        Ui2Theme(themeState) {
            val colors = Ui2Theme.colors

            WindowDraggableArea(
                modifier = modifier
                    .fillMaxSize()
                    .ui2WindowDecoration()
                    .background(
                        Brush.verticalGradient(
                            listOf(colors.background, colors.backgroundEnd)
                        )
                    )
            ) {
                Column(Modifier.fillMaxSize()) {
                    Ui2WindowHeader(
                        title = title,
                        onMinimize = { window.isMinimized = true },
                        onClose = onCloseRequest,
                        onToggleTheme = { themeState.next() },
                    )
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun Ui2WindowHeader(
    title: String,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onToggleTheme: () -> Unit,
) {
    val colors = Ui2Theme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(colors.header, RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ui2OutlinedText(
            text = title,
            color = colors.headerContent,
            outlineColor = colors.outline,
            fontSize = 18.sp,
            outlineSize = 3f,
        )

        Spacer(Modifier.weight(1f))

        Ui2OutlinedText(
            text = "theme",
            color = colors.headerContent,
            outlineColor = colors.outline,
            fontSize = 11.sp,
            outlineSize = 2f,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onToggleTheme() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )

        Ui2OutlinedText(
            text = "-",
            color = colors.headerContent,
            outlineColor = colors.outline,
            fontSize = 20.sp,
            outlineSize = 4f,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onMinimize() }
                .padding(horizontal = 8.dp),
        )

        Box(
            modifier = Modifier.size(26.dp).clickable { onClose() },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(14.dp).background(colors.accent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(6.dp).background(Color.White, CircleShape))
            }
        }
    }
}
