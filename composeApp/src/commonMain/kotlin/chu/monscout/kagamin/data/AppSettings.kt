package chu.monscout.kagamin.data

import chu.monscout.kagamin.ui.theme.Colors
import kotlinx.serialization.Serializable

@Serializable
class AppSettings(
    var showTrackProgressBar: Boolean = true,
    var discordIntegration: Boolean = true,
    var japaneseTitle: Boolean = false,
    var theme: String = Colors.themes.first().name,
    var alwaysOnTop: Boolean = false,
    var showSingerIcons: Boolean = false,
    var volume: Float = 0.3f,
    var random: Boolean = false,
    var crossfade: Boolean = true,
    var repeat: Boolean = false,
)