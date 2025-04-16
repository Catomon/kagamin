package chu.monscout.kagamin.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.serialization.Serializable

@Serializable
object SettingsDestination {
    override fun toString(): String {
        return "settings"
    }
}

@Composable
expect fun SettingsScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
)