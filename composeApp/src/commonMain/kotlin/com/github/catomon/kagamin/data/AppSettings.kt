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
    val random: Boolean = false,
    val crossfade: Boolean = true,
    val repeat: Boolean = false,
    val repeatPlaylist: Boolean = true,
    val lastPlaylistName: String = "default",
    val autoScrollNextTrack: Boolean = false,
    val useTrackImageAsBackground: Boolean = true,
    val showMediaFolderPane: Boolean = false,
    val mediaFolderPath: String = "${System.getProperty("user.home")}\\Music",
    val extra: Map<String, String> = emptyMap()
)