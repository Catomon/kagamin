package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
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
import com.github.catomon.kagamin.ui.components.AppIcon
import com.github.catomon.kagamin.ui.components.OutlinedTextButton
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminColors
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.arrow_left
import org.jetbrains.compose.resources.painterResource
import kotlin.system.exitProcess

@Composable
actual fun SettingsScreen(
    viewModel: KagaminViewModel, navController: NavHostController, modifier: Modifier
) {
    val settings = viewModel.settings
    val theme = viewModel.settings.theme
    val currentLayout = LocalLayoutManager.current.currentLayout
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (currentLayout.value == LayoutManager.Layout.Tiny) currentLayout.value =
            LayoutManager.Layout.Default
    }

    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        TrackThumbnail(
            viewModel.currentTrack?.uri,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            blur = true,
        )

        Box(Modifier.fillMaxSize().background(color = KagaminTheme.backgroundTransparent))

        Text(
            "ver. 1.1.0",
            Modifier.padding(10.dp).align(Alignment.TopEnd),
            fontStyle = FontStyle.Italic,
            color = KagaminTheme.textSecondary,
            fontSize = 12.sp
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            VerticalScrollbar(
                rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd).clickable { })

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.verticalScroll(scrollState)
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

        OutlinedTextButton(
            text = "GitHub",
            onClick = {
                openInBrowser("https://github.com/Catomon")
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            IconButton(onClick = {
                if (navController.currentDestination?.route == SettingsDestination.toString()) navController.popBackStack()
                val prev = currentLayout.value
                currentLayout.value = LayoutManager.Layout.entries.first { it != prev }
                currentLayout.value = prev
            }) {
                Icon(
                     painterResource(Res.drawable.arrow_left),
                    "Return",
                    tint = KagaminTheme.colors.buttonIcon
                )
            }

            AppIcon()
        }

        OutlinedTextButton(
            text = "Exit App",
            onClick = {
                val player = viewModel.audioPlayer
                viewModel.settings = settings.copy(
                    repeat = player.playMode.value == AudioPlayer.PlayMode.REPEAT_TRACK,
                    volume = player.volume.value,
                    random = player.playMode.value == AudioPlayer.PlayMode.RANDOM,
                )
                saveSettings(settings)
                exitProcess(0)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
        )
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
        Text("Layout:", color = KagaminTheme.text, modifier = Modifier.align(Alignment.Start))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Simple", color = KagaminTheme.textSecondary)
            RadioButton(
                selected = currentLayout != LayoutManager.Layout.BottomControls,
                onClick = {
                    onLayoutSelected(LayoutManager.Layout.Default)
                },
            )

            Text("Extended", color = KagaminTheme.textSecondary)
            RadioButton(
                selected = currentLayout == LayoutManager.Layout.BottomControls,
                onClick = {
                    onLayoutSelected(LayoutManager.Layout.BottomControls)
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
        Text("Theme:", color = KagaminTheme.text, modifier = Modifier.align(Alignment.Start))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = theme == KagaminColors.Violet.name,
                colors = RadioButtonDefaults.colors(
                    KagaminColors.Violet.background, KagaminColors.Violet.disabled
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
                    KagaminColors.Pink.background, KagaminColors.Pink.disabled
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
                    KagaminColors.Blue.background, KagaminColors.Blue.disabled
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