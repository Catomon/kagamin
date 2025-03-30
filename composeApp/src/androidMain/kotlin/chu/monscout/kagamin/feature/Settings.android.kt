package chu.monscout.kagamin.feature

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.Themes
import chu.monscout.kagamin.UserSettings
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.openInBrowser
import chu.monscout.kagamin.saveSettings
import kotlin.system.exitProcess

@Composable
actual fun SettingsScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    val settings = state.settings
    val theme = state.settings.theme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Colors.bars),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 6.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "ver. 1.0.4",
                color = Colors.text2
            )

            Text(
                "github.com/Catomon",
                Modifier.clickable {
                    openInBrowser("https://github.com/Catomon")
                },
                fontStyle = FontStyle.Italic,
                color = Colors.text2
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
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
            }
        }

        Column(Modifier.align(Alignment.BottomStart)) {
            Button({
                if (navController.currentDestination?.route == SettingsDestination.toString())
                    navController.popBackStack()
            }) {
                Text("Return", color = Colors.text)
            }

            Button(
                {
                    val player = state.denpaPlayer
                    settings.crossfade = player.crossfade.value
                    settings.repeat = player.playMode.value == DenpaPlayer.PlayMode.REPEAT_TRACK
                    settings.volume = player.volume.value
                    settings.random = player.playMode.value == DenpaPlayer.PlayMode.RANDOM
                    saveSettings(settings)
                    exitProcess(1)
                },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Exit App", color = Colors.text)
            }
        }
    }
}

@Composable
private fun ThemeRadioButtons(
    theme: String,
    settings: UserSettings,
    state: KagaminViewModel,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Theme", color = Colors.text)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
            }

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
            }
        }
    }
}