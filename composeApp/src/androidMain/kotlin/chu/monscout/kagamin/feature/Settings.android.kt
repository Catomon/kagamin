package chu.monscout.kagamin.feature

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
actual fun SettingsScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    Text("TODO")
    Button({
        navController.popBackStack()
    }) {
        Text("Return")
    }
}