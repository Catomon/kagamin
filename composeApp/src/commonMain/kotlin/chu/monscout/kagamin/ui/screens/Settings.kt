package chu.monscout.kagamin.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Serializable
object SettingsDestination {
    override fun toString(): String {
        return "settings"
    }
}

@Composable
expect fun SettingsScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
)