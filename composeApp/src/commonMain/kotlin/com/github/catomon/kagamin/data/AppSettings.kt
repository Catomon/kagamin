package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.ui.theme.Colors
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val showTrackProgressBar: Boolean = true,
    val discordIntegration: Boolean = true,
    val japaneseTitle: Boolean = false,
    val theme: String = Colors.themes.first().name,
    val alwaysOnTop: Boolean = false,
    val showSingerIcons: Boolean = false,
    val volume: Float = 0.3f,
    val random: Boolean = false,
    val crossfade: Boolean = true,
    val repeat: Boolean = false,
    val lastPlaylistName: String = "default",
)