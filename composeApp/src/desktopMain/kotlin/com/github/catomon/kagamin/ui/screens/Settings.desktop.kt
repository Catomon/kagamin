package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.data.AppSettings
import com.github.catomon.kagamin.openInBrowser
import com.github.catomon.kagamin.saveSettings
import com.github.catomon.kagamin.ui.components.AppName
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminColors
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlin.system.exitProcess

@Composable
actual fun SettingsScreen(
    viewModel: KagaminViewModel, navController: NavHostController, modifier: Modifier
) {
    val settings = viewModel.settings
    val theme = viewModel.settings.theme
    val currentLayout = LocalLayoutManager.current.currentLayout

    LaunchedEffect(Unit) {
        if (currentLayout.value == LayoutManager.Layout.Tiny) currentLayout.value =
            LayoutManager.Layout.Default
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TrackThumbnail(
            viewModel.trackThumbnail,
            onSetProgress = {
                if (viewModel.currentTrack != null)
                    viewModel.audioPlayer.seek((viewModel.currentTrack!!.duration * it).toLong())
            },
            0f,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            blur = true,
            controlProgress = false
        )

        Box(Modifier.fillMaxSize().background(color = KagaminTheme.backgroundTransparent))

        Text(
            "ver. 1.1.0",
            Modifier.padding(10.dp).align(Alignment.TopEnd),
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

                LayoutRadioButtons(currentLayout.value, {
                    viewModel.settings =
                        settings.copy(extra = settings.extra.plus("layout" to it.name))
                    currentLayout.value = it
                })

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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Scroll to next track")

                    Checkbox(settings.autoScrollNextTrack, {
                        viewModel.settings = settings.copy(autoScrollNextTrack = it)
                    })
                }
            }
        }

        Button(onClick = {
            openInBrowser("https://github.com/Catomon")
        }, modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp)) {
            Text("GitHub", color = KagaminTheme.text)
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.TopStart)) {
            IconButton(onClick = {
                if (navController.currentDestination?.route == SettingsDestination.toString()) navController.popBackStack()
                val prev = currentLayout.value
                currentLayout.value = LayoutManager.Layout.entries.first { it != prev }
                currentLayout.value = prev
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Return", tint = KagaminTheme.colors.buttonIcon)
            }

            AppName()
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
                exitProcess(0)
            }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp)
        ) {
            Text("Exit App", color = KagaminTheme.text)
        }
    }
}

@Composable
private fun LayoutRadioButtons(
    currentLayout: LayoutManager.Layout,
    onLayoutSelected: (LayoutManager.Layout) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("Layout:", color = KagaminTheme.text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = currentLayout != LayoutManager.Layout.BottomControls,
                onClick = {
                    onLayoutSelected(LayoutManager.Layout.Default)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White, size.minDimension / 2.5f,
                    )
                },
            )

            RadioButton(
                selected = currentLayout == LayoutManager.Layout.BottomControls,
                onClick = {
                    onLayoutSelected(LayoutManager.Layout.BottomControls)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White, size.minDimension / 2.5f,
                    )
                },
            )
        }
    }
}

@Composable
private fun ThemeRadioButtons(
    theme: String, settings: AppSettings, state: KagaminViewModel, modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text("Theme:", color = KagaminTheme.text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = theme == KagaminColors.Violet.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Violet.background, KagaminColors.Violet.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.colors = KagaminColors.Violet
                    state.settings = settings.copy(theme = KagaminColors.Violet.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White, size.minDimension / 2.5f
                    )
                })
            RadioButton(
                selected = theme == KagaminColors.Pink.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Pink.background, KagaminColors.Pink.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.colors = KagaminColors.Pink
                    state.settings = settings.copy(theme = KagaminColors.Pink.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White, size.minDimension / 2.5f
                    )
                })
            RadioButton(
                selected = theme == KagaminColors.Blue.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Blue.background, KagaminColors.Blue.forDisabledMostlyIdk
                ),
                onClick = {
                    KagaminTheme.colors = KagaminColors.Blue
                    state.settings = settings.copy(theme = KagaminColors.Blue.name)
                },
                modifier = Modifier.drawBehind {
                    drawCircle(
                        color = Color.White, size.minDimension / 2.5f
                    )
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == KagaminColors.KagaminDark.name,
                    colors = RadioButtonDefaults.colors(
                        KagaminColors.KagaminDark.background,
                        KagaminColors.KagaminDark.backgroundTransparent
                    ),
                    onClick = {
                        KagaminTheme.colors = KagaminColors.KagaminDark
                        state.settings = settings.copy(theme = KagaminColors.KagaminDark.name)
                    },
                    modifier = Modifier.drawBehind {
                        drawCircle(
                            color = Color.White, size.minDimension / 2.5f
                        )
                    })

//                RadioButton(
//                    theme == KagaminColors.White.name,
//                    colors = RadioButtonDefaults.colors(
//                        KagaminColors.White.bars,
//                        KagaminColors.White.barsTransparent
//                    ),
//                    onClick = {
//                        Colors.currentYukiTheme = KagaminColors.White
//                        settings.theme = KagaminColors.White.name
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