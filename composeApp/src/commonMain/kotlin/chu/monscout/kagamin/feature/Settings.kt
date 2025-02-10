package chu.monscout.kagamin.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.Themes
import kotlinx.serialization.Serializable
import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.saveSettings
import kotlin.system.exitProcess

@Serializable
object SettingsDestination {
    override fun toString(): String {
        return "settings"
    }
}

@Composable
fun SettingsScreen(state: KagaminViewModel, navController: NavHostController, modifier: Modifier = Modifier) {
    val settings = state.settings
    val theme = state.settings.theme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Theme")
            RadioButton(
                theme == Themes.Violet.name,
                colors = RadioButtonDefaults.colors(Themes.Violet.bars, Themes.Violet.surface),
                onClick = {
                    Colors.currentYukiTheme = Themes.Violet
                    settings.theme = Themes.Violet.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                    Colors.updateTheme()
                })
            RadioButton(
                theme == Themes.Pink.name,
                colors = RadioButtonDefaults.colors(Themes.Pink.bars, Themes.Pink.surface),
                onClick = {
                    Colors.currentYukiTheme = Themes.Pink
                    settings.theme = Themes.Pink.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                    Colors.updateTheme()
                })
            RadioButton(
                theme == Themes.Blue.name,
                colors = RadioButtonDefaults.colors(Themes.Blue.bars, Themes.Blue.surface),
                onClick = {
                    Colors.currentYukiTheme = Themes.Blue
                    settings.theme = Themes.Blue.name
                    saveSettings(settings)
                    state.settings = loadSettings()
                    Colors.updateTheme()
                })
        }

        Button({
            navController.popBackStack()
        }) {
            Text("Return")
        }

        Button({
            //todo expect exitApp()

            val player = state.denpaPlayer
            settings.fade = player.fade.value
            settings.repeat = player.playMode.value == DenpaPlayer.PlayMode.REPEAT_TRACK
            settings.volume = player.volume.value
            settings.random = player.playMode.value == DenpaPlayer.PlayMode.RANDOM
            saveSettings(settings)
            exitProcess(1)
        },
            modifier = Modifier.padding(top = 12.dp)) {
            Text("Exit App")
        }
    }
}