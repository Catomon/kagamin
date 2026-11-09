package com.github.catomon.kagamin.ui.v3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.WindowConfig
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.components.rememberTrackThumbnail
import com.github.catomon.kagamin.ui.v3.components.V3Header
import com.github.catomon.kagamin.ui.v3.components.V3MenuOverlay
import com.github.catomon.kagamin.ui.v3.components.V3OutlinedText
import com.github.catomon.kagamin.ui.v3.components.V3Starfall
import com.github.catomon.kagamin.ui.v3.components.V3Thumbnail
import com.github.catomon.kagamin.ui.v3.components.V3TransportControls
import com.github.catomon.kagamin.ui.v3.components.LocalV3ItemAlpha
import com.github.catomon.kagamin.ui.v3.components.LocalV3PanelAlpha
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.gami_bg
import kagamin.composeapp.generated.resources.lucky_star_bg
import kagamin.composeapp.generated.resources.nata_bg
import kagamin.composeapp.generated.resources.tsuka_bg
import kagamin.composeapp.generated.resources.yuki_bg
import org.jetbrains.compose.resources.painterResource
import kotlin.system.exitProcess

/**
 * Tsukaremi layout: title bar on top, the track image and its controls on the
 * left, playlists and the tracklist on the right.
 */
@Composable
fun TsukaremiPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    onOpenOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = V3Theme.colors
    val currentTrack by viewModel.currentTrack.collectAsState()
    val position by viewModel.position.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val window = LocalWindow.current

    var isMenuOpen by remember { mutableStateOf(false) }

    val trackArtwork = rememberTrackThumbnail(currentTrack, 512)
    val showTrackBackground =
        viewModel.settings.useTrackImageAsBackground && trackArtwork != null
    val hasImageBackground =
        showTrackBackground || viewModel.settings.characterImageBackground
    val panelAlpha =
        if (hasImageBackground) viewModel.settings.v3PanelOpacityImage else viewModel.settings.v3PanelOpacity
    val itemAlpha =
        if (hasImageBackground) viewModel.settings.v3ItemOpacityImage else viewModel.settings.v3ItemOpacity
    val overlayAlpha =
        (if (hasImageBackground) viewModel.settings.v3BackgroundOverlayImage
        else viewModel.settings.v3BackgroundOverlay).coerceIn(0f, 1f)

    val customBackground = remember(viewModel.settings.v3CustomBackground) {
        loadStoredPainter("backgrounds", viewModel.settings.v3CustomBackground)
    }

    CompositionLocalProvider(
        LocalV3PanelAlpha provides panelAlpha.coerceIn(0f, 1f),
        LocalV3ItemAlpha provides itemAlpha.coerceIn(0f, 1f),
    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.backgroundMid),
    ) {
        if (showTrackBackground) {
            Image(
                bitmap = trackArtwork!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize().blur(5.dp),
            )

            Box(
                Modifier
                    .matchParentSize()
                    .background(colors.backgroundMid.copy(alpha = overlayAlpha))
            )
        } else if (viewModel.settings.characterImageBackground) {
            val backgroundRes = when (colors.name) {
                "miyuki" -> Res.drawable.yuki_bg
                "kagami" -> Res.drawable.gami_bg
                "konata" -> Res.drawable.nata_bg
                "lucky-star-dark" -> Res.drawable.lucky_star_bg
                else -> Res.drawable.tsuka_bg
            }

            Image(
                painter = customBackground ?: painterResource(backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )

            Box(
                Modifier
                    .matchParentSize()
                    .background(colors.backgroundMid.copy(alpha = overlayAlpha))
            )
        }

        V3Starfall(Modifier.matchParentSize())

        Column(Modifier.fillMaxSize()) {
            V3Header(
                viewModel = viewModel,
                onOpenMenu = { isMenuOpen = true },
                onMinimize = { window.isMinimized = true },
                onClose = {
                    viewModel.saveSettings()
                    if (WindowConfig.isTraySupported) viewModel.hideInTray()
                    else exitProcess(0)
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                V3CenterPlayer(
                    viewModel = viewModel,
                    currentTrack = currentTrack,
                    position = position,
                    volume = volume,
                    modifier = Modifier
                        .width(240.dp)
                        .fillMaxHeight(),
                )

                V3LibraryPane(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }

        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            V3MenuOverlay(
                viewModel = viewModel,
                onOpenOptions = onOpenOptions,
                onClose = { isMenuOpen = false },
            )
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .size(30.dp),
                color = colors.accent,
            )
        }
    }
    }
}

@Composable
private fun V3CenterPlayer(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?,
    position: Long,
    volume: Float,
    modifier: Modifier = Modifier,
) {
    val colors = V3Theme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val side = minOf(maxWidth, maxHeight * 0.94f, 240.dp)
            V3Thumbnail(
                track = currentTrack,
                position = position,
                onSeek = viewModel::seek,
                side = side,
                showProgress = viewModel.settings.showTrackProgressBar,
            )
        }

        Spacer(Modifier.height(10.dp))

        V3OutlinedText(
            text = currentTrack?.title ?: "Nothing playing",
            fillColor = colors.text,
            outlineColor = colors.outline,
            fontSize = 21.sp,
            outlineSize = 5f,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )

        if (currentTrack?.artist?.isNotBlank() == true) {
            V3OutlinedText(
                text = currentTrack.artist,
                fillColor = colors.text,
                outlineColor = colors.outline,
                fontSize = 13.sp,
                outlineSize = 3.5f,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))

        V3TransportControls(
            viewModel = viewModel,
            volume = volume,
        )

        Spacer(Modifier.height(8.dp))
    }
}
