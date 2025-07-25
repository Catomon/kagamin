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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.data.AppSettings
import com.github.catomon.kagamin.data.defaultMediaFolder
import com.github.catomon.kagamin.ui.components.AppIcon
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.theme.KagaminColors
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.arrow_left
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
object SettingsDestination {
    override fun toString(): String {
        return "settings"
    }
}

@Composable
fun SettingsScreen(
    viewModel: KagaminViewModel, navController: NavHostController, modifier: Modifier = Modifier
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
        Background(currentTrack = viewModel.currentTrack.value, modifier = Modifier.fillMaxSize())

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

                CheckboxOption("Always on top", settings.alwaysOnTop) {
                    viewModel.settings = settings.copy(alwaysOnTop = it)
                }

                CheckboxOption("Track image as background", settings.useTrackImageAsBackground) {
                    viewModel.settings = settings.copy(useTrackImageAsBackground = it)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Crossfade")

                    Checkbox(settings.crossfade, {
                        viewModel.setCrossfade(it)
                        viewModel.settings = settings.copy(crossfade = it)
                    })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Scroll to next track")

                    Checkbox(settings.autoScrollNextTrack, {
                        viewModel.settings = settings.copy(autoScrollNextTrack = it)
                    })
                }

                CheckboxOption("Show Media Folder Pane", settings.showMediaFolderPane) {
                    viewModel.settings = settings.copy(showMediaFolderPane = it)
                }

                val directoryPicker = rememberDirectoryPickerLauncher(
                    "Pick Media Folder",
                    directory = _root_ide_package_.io.github.vinceglb.filekit.PlatformFile(
                        defaultMediaFolder
                    )
                ) { folder ->
                    viewModel.settings = settings.copy(
                        mediaFolderPath = folder?.absolutePath()
                            ?: return@rememberDirectoryPickerLauncher
                    )
                }
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable {
                        directoryPicker.launch()
                    }
                ) {
                    Text("Media Folder location:")

                    Text(
                        settings.mediaFolderPath,
                        color = KagaminTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

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
    }
}

@Composable
fun CheckboxOption(text: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text)
        Checkbox(checked, onChange)
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
                    KagaminColors.Violet.background, KagaminColors.Violet.backgroundTransparent
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
                    KagaminColors.Pink.background, KagaminColors.Pink.backgroundTransparent
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
                    KagaminColors.Blue.background, KagaminColors.Blue.backgroundTransparent
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