package chu.monscout.kagamin.data

import chu.monscout.kagamin.ui.theme.Themes
import kotlinx.serialization.Serializable

@Serializable
class AppSettings(
    var showTrackProgressBar: Boolean = true,
    var discordIntegration: Boolean = true,
    var japaneseTitle: Boolean = false,
    var theme: String = Themes.list.first().name,
    var alwaysOnTop: Boolean = false,
    var showSingerIcons: Boolean = false,
    var volume: Float = 0.3f,
    var random: Boolean = false,
    var crossfade: Boolean = true,
    var repeat: Boolean = false,
)