package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val showTrackProgressBar: Boolean = true,
    val discordIntegration: Boolean = true,
    val japanese: Boolean = false,
    val theme: String = KagaminTheme.themes.first().name,
    val alwaysOnTop: Boolean = false,
    val showSingerIcons: Boolean = false,
    val volume: Float = 0.3f,
    val random: Boolean = true,
    val crossfade: Boolean = true,
    val repeat: Boolean = false,
    val repeatPlaylist: Boolean = true,
    val lastPlaylistName: String = "default",
    val autoScrollNextTrack: Boolean = true,
    val characterImageBackground: Boolean = true,
    val useTrackImageAsBackground: Boolean = true,
    val showThumbnails: Boolean = true,
    val itemBackgroundColor: Boolean = true,
    val showMediaFolderPane: Boolean = false,
    val mediaFolderPath: String = "${System.getProperty("user.home")}\\Music",
    val v3PanelOpacity: Float = 1f,
    val v3ItemOpacity: Float = 1f,
    val v3PanelOpacityImage: Float = 0.5f,
    val v3ItemOpacityImage: Float = 0.75f,
    val v3BackgroundOverlay: Float = 0f,
    val v3BackgroundOverlayImage: Float = 0f,
    val v3CustomBackground: String = "",
    val v3CustomThumbnail: String = "",
    val extra: Map<String, String> = mapOf(
        "layout" to "Tsukaremi",
        "v3theme" to "kagami",
    ),
)
