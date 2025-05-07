package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.data.AppSettings
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.openInBrowser
import com.github.catomon.kagamin.saveSettings
import com.github.catomon.kagamin.ui.components.AppName
import com.github.catomon.kagamin.ui.theme.KagaminColors
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KagaminTheme.background),
        contentAlignment = Alignment.Center
    ) {
        AppName(Modifier
            .align(Alignment.TopCenter)
            .padding(top = 10.dp))

        Text(
            "ver. 1.0.7 github.com/Catomon",
            Modifier
                .clickable {
                    openInBrowser("https://github.com/Catomon")
                }
                .padding(start = 3.dp)
                .align(Alignment.BottomCenter),
            fontStyle = FontStyle.Italic,
            color = KagaminTheme.textSecondary,
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
        }, modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 10.dp)) {
            Text("Return", color = KagaminTheme.text)
        }

        Button(
            {
                val player = viewModel.audioPlayer
                viewModel.settings = settings.copy(
                    repeat = player.playMode.value == AudioPlayer.PlayMode.REPEAT_TRACK,
                    volume = player.volume.value,
                    random = player.playMode.value == AudioPlayer.PlayMode.RANDOM,
                    repeatPlaylist = player.playMode.value == AudioPlayer.PlayMode.REPEAT_PLAYLIST
                )
                saveSettings(settings)
                exitProcess(1)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp)
        ) {
            Text("Exit App", color = KagaminTheme.text)
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
        Text("KagaminTheme:", color = KagaminTheme.text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                theme == KagaminColors.Violet.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Violet.background,
                    KagaminColors.Violet.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.theme = KagaminColors.Violet
                    state.settings = settings.copy(theme = KagaminColors.Violet.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == KagaminColors.Pink.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Pink.background,
                    KagaminColors.Pink.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.theme = KagaminColors.Pink
                    state.settings = settings.copy(theme = KagaminColors.Pink.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            RadioButton(
                theme == KagaminColors.Blue.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Blue.background,
                    KagaminColors.Blue.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.theme = KagaminColors.Blue
                    state.settings = settings.copy(theme = KagaminColors.Blue.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White,
                        size.minDimension / 2.5f
                    )
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    theme == KagaminColors.KagaminDark.name,
                    colors = RadioButtonDefaults.colors(
                        KagaminColors.KagaminDark.background,
                        KagaminColors.KagaminDark.backgroundTransparent
                    ),
                    onClick = {
                        KagaminTheme.theme = KagaminColors.KagaminDark
                        state.settings = settings.copy(theme = KagaminColors.KagaminDark.name)
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