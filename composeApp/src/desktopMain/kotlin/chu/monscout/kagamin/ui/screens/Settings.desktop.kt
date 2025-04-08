package chu.monscout.kagamin.ui.screens

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
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.LayoutManager
import chu.monscout.kagamin.LocalLayoutManager
import chu.monscout.kagamin.Themes
import chu.monscout.kagamin.UserSettings
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.openInBrowser
import chu.monscout.kagamin.saveSettings
import chu.monscout.kagamin.ui.components.AppName
import kotlin.system.exitProcess

@Composable
actual fun SettingsScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    val settings = state.settings
    val theme = state.settings.theme
    val currentLayout = LocalLayoutManager.current.currentLayout

    LaunchedEffect(null) {
        if (currentLayout.value == LayoutManager.Layout.Tiny)
            currentLayout.value = LayoutManager.Layout.Compact
    }

    Box(
        modifier = modifier.fillMaxSize().background(Colors.bars),
        contentAlignment = Alignment.Center
    ) {
        AppName(Modifier.align(Alignment.TopCenter).padding(top = 10.dp))

        Text(
            "ver. 1.0.7 github.com/Catomon",
            Modifier.clickable {
                openInBrowser("https://github.com/Catomon")
            }.padding(start = 3.dp).align(Alignment.BottomCenter),
            fontStyle = FontStyle.Italic,
            color = Colors.text2,
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
                ThemeRadioButtons(theme, settings, state)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Always on top")

                    Checkbox(settings.alwaysOnTop, {
                        settings.alwaysOnTop = it
                        saveSettings(settings)
                        state.settings = loadSettings()
                    })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Crossfade")

                    Checkbox(settings.crossfade, {
                        settings.crossfade = it
                        saveSettings(settings)
                        state.settings = loadSettings()
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
                val player = state.audioPlayer
                settings.crossfade = player.crossfade.value
                settings.repeat = player.playMode.value == AudioPlayer.PlayMode.REPEAT_TRACK
                settings.volume = player.volume.value
                settings.random = player.playMode.value == AudioPlayer.PlayMode.RANDOM
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
    settings: UserSettings,
    state: KagaminViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("Themes:", color = Colors.text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                theme == Themes.Violet.name,
                colors = RadioButtonDefaults.colors(
                    Themes.Violet.bars,
                    Themes.Violet.surface
                ),
                onClick = {
                    Colors.currentYukiTheme = Themes.Violet
                    settings.theme = Themes.Violet.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == Themes.Pink.name,
                colors = RadioButtonDefaults.colors(Themes.Pink.bars, Themes.Pink.surface),
                onClick = {
                    Colors.currentYukiTheme = Themes.Pink
                    settings.theme = Themes.Pink.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == Themes.Blue.name,
                colors = RadioButtonDefaults.colors(Themes.Blue.bars, Themes.Blue.surface),
                onClick = {
                    Colors.currentYukiTheme = Themes.Blue
                    settings.theme = Themes.Blue.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    theme == Themes.KagaminDark.name,
                    colors = RadioButtonDefaults.colors(
                        Themes.KagaminDark.bars,
                        Themes.KagaminDark.barsTransparent
                    ),
                    onClick = {
                        Colors.currentYukiTheme = Themes.KagaminDark
                        settings.theme = Themes.KagaminDark.name
                        saveSettings(settings)
                        state.settings = loadSettings()
                    },
                    modifier = Modifier.drawBehind {
                        drawCircle(
                            color = Color.White,
                            size.minDimension / 2.5f
                        )
                    })

//                RadioButton(
//                    theme == Themes.White.name,
//                    colors = RadioButtonDefaults.colors(
//                        Themes.White.bars,
//                        Themes.White.barsTransparent
//                    ),
//                    onClick = {
//                        Colors.currentYukiTheme = Themes.White
//                        settings.theme = Themes.White.name
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