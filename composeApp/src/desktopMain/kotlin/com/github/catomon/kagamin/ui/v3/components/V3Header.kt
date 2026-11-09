package com.github.catomon.kagamin.ui.v3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.appName
import com.github.catomon.kagamin.openInBrowser
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.tsukaremi_star
import org.jetbrains.compose.resources.painterResource
import kotlin.system.exitProcess

/**
 * Purple gradient title bar. "app name | theme name" is centered; the theme
 * name is clickable and cycles the v3 palettes.
 */
@Composable
fun V3Header(
    viewModel: KagaminViewModel,
    onOpenMenu: () -> Unit,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = V3Theme.colors
    val themeState = V3Theme.state
    val titleShape = RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(titleShape)
            .background(Brush.verticalGradient(listOf(colors.headerStart, colors.headerEnd)))
            .border(
                width = 2.dp,
                color = colors.controlContent.copy(alpha = 0.22f),
                shape = titleShape,
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(96.dp), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable(onClickLabel = "Open menu", onClick = onOpenMenu),
                contentAlignment = Alignment.Center,
            ) {
                V3Icon(
                    painter = painterResource(Res.drawable.tsukaremi_star),
                    tint = colors.headerContent,
                    size = 22.dp,
                    outlineColor = colors.headerOutline,
                    outlineScale = 1.3f,
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                V3OutlinedText(
                    text = appName,
                    fillColor = colors.headerContent,
                    outlineColor = colors.headerOutline,
                    fontSize = 21.sp,
                    outlineSize = 4.5f,
                    maxLines = 1,
                )
            }

            V3OutlinedText(
                text = "  |  ",
                fillColor = colors.headerContent,
                outlineColor = colors.headerOutline,
                fontSize = 19.sp,
                outlineSize = 4f,
                maxLines = 1,
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClickLabel = "Change theme") {
                            val next = themeState.next()
                            viewModel.settings = viewModel.settings.copy(
                                extra = viewModel.settings.extra + ("v3theme" to next.name)
                            )
                        }
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    V3OutlinedText(
                        text = colors.displayName,
                        fillColor = colors.headerContent,
                        outlineColor = colors.headerOutline,
                        fontSize = 21.sp,
                        outlineSize = 4.5f,
                        maxLines = 1,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.width(96.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClickLabel = "Minimize", onClick = onMinimize),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(7.5.dp)
                        .background(colors.headerOutline, RoundedCornerShape(percent = 50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(3.5.dp)
                            .background(colors.headerContent, RoundedCornerShape(percent = 50)),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClickLabel = "Hide in tray", onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(colors.headerOutline, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(colors.headerContent, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun V3MenuOverlay(
    viewModel: KagaminViewModel,
    onOpenOptions: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panelInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose,
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 18.dp, top = 68.dp)
                .width(216.dp)
                .v3Panel()
                .padding(vertical = 8.dp)
                .clickable(
                    interactionSource = panelInteraction,
                    indication = null,
                    onClick = {},
                ),
        ) {
            V3MenuEntry("Add tracks") {
                viewModel.currentTab = Tabs.ADD_TRACKS
                viewModel.createPlaylistWindow = true
                onClose()
            }

            V3MenuEntry("New playlist") {
                viewModel.currentTab = Tabs.CREATE_PLAYLIST
                viewModel.createPlaylistWindow = true
                onClose()
            }

            V3MenuEntry("Options") {
                onOpenOptions()
                onClose()
            }

            V3MenuEntry("GitHub") {
                openInBrowser("https://github.com/Catomon")
                onClose()
            }

            V3MenuEntry("Exit app") {
                viewModel.saveSettings()
                exitProcess(0)
            }
        }
    }
}

@Composable
private fun V3MenuEntry(
    text: String,
    onClick: () -> Unit,
) {
    val colors = V3Theme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isHovered)
                    Brush.horizontalGradient(listOf(colors.itemStart, colors.itemEnd))
                else
                    Brush.horizontalGradient(listOf(colors.panelStart, colors.panelEnd))
            )
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        V3OutlinedText(
            text = text,
            fillColor = colors.panelContent,
            outlineColor = colors.headerOutline,
            fontSize = 15.sp,
            outlineSize = 3.5f,
            maxLines = 1,
        )
    }
}
