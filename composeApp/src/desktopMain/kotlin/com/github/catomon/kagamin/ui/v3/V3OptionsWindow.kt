package com.github.catomon.kagamin.ui.v3

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.ui.v3.components.V3OutlinedText
import com.github.catomon.kagamin.ui.v3.components.v3Panel
import com.github.catomon.kagamin.ui.v3.theme.V3Colors
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import com.github.catomon.kagamin.ui.v3.theme.V3Themes
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import org.jetbrains.compose.resources.imageResource
import kotlin.math.roundToInt

/** Standalone options window for the Tsukaremi layout. */
@Composable
fun V3OptionsWindow(
    viewModel: KagaminViewModel,
    onClose: () -> Unit,
) {
    val colors = V3Theme.colors
    val themeState = V3Theme.state
    val window = LocalWindow.current
    val settings = viewModel.settings

    val withImage = settings.characterImageBackground
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.backgroundMid, colors.backgroundEnd)))
            .padding(14.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .v3Panel()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            V3OutlinedText(
                text = "OPTIONS",
                fillColor = colors.panelContent,
                outlineColor = colors.headerOutline,
                fontSize = 20.sp,
                outlineSize = 4.5f,
                maxLines = 1,
            )

            Spacer(Modifier.height(2.dp))

            V3OutlinedText(
                text = "THEME",
                fillColor = colors.panelContent,
                outlineColor = colors.headerOutline,
                fontSize = 12.sp,
                outlineSize = 3f,
                maxLines = 1,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                V3Themes.all.chunked(3).forEach { themeRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        themeRow.forEach { theme ->
                            V3ThemeTile(
                                theme = theme,
                                selected = theme.name == themeState.colors.name,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    themeState.select(theme)
                                    viewModel.settings = viewModel.settings.copy(
                                        extra = viewModel.settings.extra + ("v3theme" to theme.name)
                                    )
                                },
                            )
                        }
                        repeat(3 - themeRow.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    V3OutlinedText(
                        text = "CUSTOMIZATION",
                        fillColor = colors.panelContent,
                        outlineColor = colors.headerOutline,
                        fontSize = 12.sp,
                        outlineSize = 3f,
                        maxLines = 1,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        V3OutlinedText(
                            text = "Character background",
                            fillColor = colors.panelContent,
                            outlineColor = colors.headerOutline,
                            fontSize = 12.sp,
                            outlineSize = 3f,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = settings.characterImageBackground,
                            onCheckedChange = {
                                viewModel.settings = settings.copy(characterImageBackground = it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onAccent,
                                checkedTrackColor = colors.accent,
                                checkedBorderColor = colors.panelBorderStart,
                                uncheckedThumbColor = colors.panelContentSecondary,
                                uncheckedTrackColor = colors.panelContent.copy(alpha = 0.25f),
                                uncheckedBorderColor = colors.panelBorderStart,
                            ),
                        )
                    }

                    if (withImage) {
                        V3MediaRow(
                            label = "Background image",
                            isCustom = settings.v3CustomBackground.isNotBlank(),
                            onChoose = {
                                pickAndStoreImage(window, "backgrounds", "background")?.let {
                                    viewModel.settings = settings.copy(v3CustomBackground = it)
                                }
                            },
                            onDefault = {
                                viewModel.settings = settings.copy(v3CustomBackground = "")
                            },
                        )
                    }

                    V3SliderRow(
                        label = "Background visibility",
                        value = 1f - (if (withImage) settings.v3BackgroundOverlayImage
                        else settings.v3BackgroundOverlay),
                        onValueChange = { visibility ->
                            val overlay = 1f - visibility
                            viewModel.settings = if (withImage) {
                                settings.copy(v3BackgroundOverlayImage = overlay)
                            } else {
                                settings.copy(v3BackgroundOverlay = overlay)
                            }
                        },
                    )

                    V3SliderRow(
                        label = "Panel opacity",
                        value = if (withImage) settings.v3PanelOpacityImage else settings.v3PanelOpacity,
                        onValueChange = {
                            viewModel.settings = if (withImage) {
                                settings.copy(v3PanelOpacityImage = it)
                            } else {
                                settings.copy(v3PanelOpacity = it)
                            }
                        },
                    )

                    V3SliderRow(
                        label = "Track item opacity",
                        value = if (withImage) settings.v3ItemOpacityImage else settings.v3ItemOpacity,
                        onValueChange = {
                            viewModel.settings = if (withImage) {
                                settings.copy(v3ItemOpacityImage = it)
                            } else {
                                settings.copy(v3ItemOpacity = it)
                            }
                        },
                    )

                    Spacer(Modifier.height(4.dp))

                    V3OutlinedText(
                        text = "DEFAULT THUMBNAIL",
                        fillColor = colors.panelContent,
                        outlineColor = colors.headerOutline,
                        fontSize = 12.sp,
                        outlineSize = 3f,
                        maxLines = 1,
                    )

                    V3MediaRow(
                        label = "Track thumbnail",
                        isCustom = settings.v3CustomThumbnail.isNotBlank(),
                        onChoose = {
                            pickAndStoreImage(window, "thumbnails", "thumbnail")?.let {
                                viewModel.settings = settings.copy(v3CustomThumbnail = it)
                            }
                        },
                        onDefault = {
                            viewModel.settings = settings.copy(v3CustomThumbnail = "")
                        },
                    )

                    Spacer(Modifier.height(4.dp))

                    V3CheckboxRow(
                        text = "Always on top",
                        checked = settings.alwaysOnTop,
                    ) {
                        viewModel.settings = settings.copy(alwaysOnTop = it)
                    }
                    V3CheckboxRow(
                        text = "Track image as background",
                        checked = settings.useTrackImageAsBackground,
                    ) {
                        viewModel.settings = settings.copy(useTrackImageAsBackground = it)
                    }
                    V3CheckboxRow(
                        text = "Show thumbnails",
                        checked = settings.showThumbnails,
                    ) {
                        viewModel.settings = settings.copy(showThumbnails = it)
                    }
                    V3CheckboxRow(
                        text = "Item background color",
                        checked = settings.itemBackgroundColor,
                    ) {
                        viewModel.settings = settings.copy(itemBackgroundColor = it)
                    }
                    V3CheckboxRow(
                        text = "Crossfade",
                        checked = settings.crossfade,
                    ) {
                        viewModel.setCrossfade(it)
                        viewModel.settings = settings.copy(crossfade = it)
                    }
                    V3CheckboxRow(
                        text = "Scroll to next track",
                        checked = settings.autoScrollNextTrack,
                    ) {
                        viewModel.settings = settings.copy(autoScrollNextTrack = it)
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                V3OptionsButton(text = "Close", onClick = onClose)
            }
        }
    }
}

@Composable
private fun V3MediaRow(
    label: String,
    isCustom: Boolean,
    onChoose: () -> Unit,
    onDefault: () -> Unit,
) {
    val colors = V3Theme.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            V3OutlinedText(
                text = label,
                fillColor = colors.panelContent,
                outlineColor = colors.headerOutline,
                fontSize = 12.sp,
                outlineSize = 3f,
                maxLines = 1,
            )
            V3OutlinedText(
                text = if (isCustom) "Custom image" else "Theme default",
                fillColor = colors.panelContentSecondary,
                outlineColor = colors.headerOutline,
                fontSize = 10.sp,
                outlineSize = 2.5f,
                maxLines = 1,
            )
        }
        V3OptionsButton(text = "Choose image", onClick = onChoose)
        if (isCustom) {
            V3OptionsButton(text = "Theme default", onClick = onDefault)
        }
    }
}

@Composable
private fun V3SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    val colors = V3Theme.colors

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            V3OutlinedText(
                text = label,
                fillColor = colors.panelContent,
                outlineColor = colors.headerOutline,
                fontSize = 12.sp,
                outlineSize = 3f,
                maxLines = 1,
            )
            V3OutlinedText(
                text = "${(value.coerceIn(0f, 1f) * 100).roundToInt()}%",
                fillColor = colors.panelContentSecondary,
                outlineColor = colors.headerOutline,
                fontSize = 11.sp,
                outlineSize = 2.5f,
                maxLines = 1,
            )
        }
        Slider(
            value = value.coerceIn(0f, 1f),
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = colors.accent,
                activeTrackColor = colors.accent,
                activeTickColor = colors.accent,
                inactiveTrackColor = colors.panelContent.copy(alpha = 0.25f),
                inactiveTickColor = colors.panelContent.copy(alpha = 0.25f),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun V3CheckboxRow(
    text: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    val colors = V3Theme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V3OutlinedText(
            text = text,
            fillColor = colors.panelContent,
            outlineColor = colors.headerOutline,
            fontSize = 12.sp,
            outlineSize = 3f,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Checkbox(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun V3ThemeTile(
    theme: V3Colors,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = V3Theme.colors
    val shape = RoundedCornerShape(8.dp)
    val thumbnail = v3ThumbnailSet(theme.name).thumb

    Column(
        modifier = modifier
            .clip(shape)
            .clickable(onClickLabel = "Use ${theme.displayName} theme", onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) colors.accentSoft else colors.itemStart,
                    shape = shape,
                ),
        ) {
            Image(
                bitmap = imageResource(thumbnail),
                contentDescription = theme.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Spacer(Modifier.height(4.dp))

        V3OutlinedText(
            text = theme.displayName,
            fillColor = if (selected) colors.panelContent else colors.panelContentSecondary,
            outlineColor = colors.headerOutline,
            fontSize = 12.sp,
            outlineSize = 3f,
            maxLines = 1,
        )
    }
}

@Composable
private fun V3OptionsButton(
    text: String,
    onClick: () -> Unit,
) {
    val colors = V3Theme.colors
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.controlStart, colors.controlEnd)), shape)
            .border(2.dp, colors.controlContent.copy(alpha = 0.22f), shape)
            .clickable(onClickLabel = text, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        V3OutlinedText(
            text = text,
            fillColor = colors.controlContent,
            outlineColor = colors.headerOutline,
            fontSize = 12.sp,
            outlineSize = 3.5f,
            maxLines = 1,
        )
    }
}
