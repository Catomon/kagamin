package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.LayoutManager
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.data.AppSettings
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.openInBrowser
import com.github.catomon.kagamin.saveSettings
import com.github.catomon.kagamin.ui.components.AppName
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlin.system.exitProcess

@Composable
actual fun SettingsScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    val settings = viewModel.settings
    val theme = viewModel.settings.theme
    val currentLayout = LocalLayoutManager.current.currentLayout

    LaunchedEffect(null) {
        if (currentLayout.value == LayoutManager.Layout.Tiny)
            currentLayout.value = LayoutManager.Layout.Default
    }

    Box(
        modifier = modifier.fillMaxSize().background(Colors.background),
        contentAlignment = Alignment.Center
    ) {
        AppName(Modifier.align(Alignment.TopCenter).padding(top = 10.dp))

        Text(
            "ver. 1.0.7 github.com/Catomon",
            Modifier.clickable {
                openInBrowser("https://github.com/Catomon")
            }.padding(start = 3.dp).align(Alignment.BottomCenter),
            fontStyle = FontStyle.Italic,
            color = Colors.textSecondary,
            fontSize = 12.sp
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                ThemeRadioButtons(theme, settings, viewModel)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Always on top")

                    Checkbox(settings.alwaysOnTop, {
                        viewModel.settings = settings.copy(alwaysOnTop = it)
                    })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Crossfade")

                    Checkbox(settings.crossfade, {
                        viewModel.settings = settings.copy(crossfade = it)
                    })
                }
            }
        }

        Button({
            if (navController.currentDestination?.route == SettingsDestination.toString())
                navController.popBackStack()
            val prev = currentLayout.value
            currentLayout.value = LayoutManager.Layout.entries.first { it != prev }
            currentLayout.value = prev
        }, modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp)) {
            Text("Return", color = Colors.text)
        }

        Button(
            {
                val player = viewModel.audioPlayer
                viewModel.settings = settings.copy(
                    repeat = player.playMode.value == AudioPlayer.PlayMode.REPEAT_TRACK,
                    volume = player.volume.value,
                    random = player.playMode.value == AudioPlayer.PlayMode.RANDOM,
                )
                saveSettings(settings)
                exitProcess(1)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp)
        ) {
            Text("Exit App", color = Colors.text)
        }
    }
}

@Composable
private fun ThemeRadioButtons(
    theme: String,
    settings: AppSettings,
    state: KagaminViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("Theme:", color = Colors.text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                theme == KagaminTheme.Violet.name,
                colors = RadioButtonDefaults.colors(
                    KagaminTheme.Violet.background,
                    KagaminTheme.Violet.listItemA
                ),
                onClick = {
                    Colors.theme = KagaminTheme.Violet
                    state.settings = settings.copy(theme = KagaminTheme.Violet.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == KagaminTheme.Pink.name,
                colors = RadioButtonDefaults.colors(
                    KagaminTheme.Pink.background,
                    KagaminTheme.Pink.listItemA
                ),
                onClick = {
                    Colors.theme = KagaminTheme.Pink
                    state.settings = settings.copy(theme = KagaminTheme.Pink.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == KagaminTheme.Blue.name,
                colors = RadioButtonDefaults.colors(
                    KagaminTheme.Blue.background,
                    KagaminTheme.Blue.listItemA
                ),
                onClick = {
                    Colors.theme = KagaminTheme.Blue
                    state.settings = settings.copy(theme = KagaminTheme.Blue.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    theme == KagaminTheme.KagaminDark.name,
                    colors = RadioButtonDefaults.colors(
                        KagaminTheme.KagaminDark.background,
                        KagaminTheme.KagaminDark.backgroundTransparent
                    ),
                    onClick = {
                        Colors.theme = KagaminTheme.KagaminDark
                        state.settings = settings.copy(theme = KagaminTheme.KagaminDark.name)
                    },
                    modifier = Modifier.drawBehind {
                        drawCircle(
                            color = Color.White,
                            size.minDimension / 2.5f
                        )
                    })

//                RadioButton(
//                    theme == KagaminTheme.White.name,
//                    colors = RadioButtonDefaults.colors(
//                        KagaminTheme.White.bars,
//                        KagaminTheme.White.barsTransparent
//                    ),
//                    onClick = {
//                        Colors.currentYukiTheme = KagaminTheme.White
//                        settings.theme = KagaminTheme.White.name
//                        saveSettings(settings)
//                        state.settings = loadSettings()
//                    },
//                    modifier = Modifier.drawBehind {
//                        drawCircle(
//                            color = Color.White,
//                            size.minDimension / 2.5f
//                        )
//                    })
            }
        }
    }
}