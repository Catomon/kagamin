package chu.monscout.kagamin.ui

import chu.monscout.kagamin.LocalSnackbarHostState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import chu.monscout.kagamin.ui.screens.PlayerScreen
import chu.monscout.kagamin.ui.screens.PlayerScreenDestination
import chu.monscout.kagamin.ui.screens.SettingsDestination
import chu.monscout.kagamin.ui.screens.SettingsScreen

@Composable
actual fun KagaminApp(
    kagaminViewModel: KagaminViewModel,
    modifier: Modifier
) {
    val navController = rememberNavController()

    Scaffold(snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }, modifier = modifier) {
        val padding = it
        NavHost(navController,
            startDestination = PlayerScreenDestination.toString()) {
            composable(PlayerScreenDestination.toString()) {
                PlayerScreen(kagaminViewModel, navController, Modifier.padding(padding))
            }

            composable(SettingsDestination.toString()) {
                SettingsScreen(kagaminViewModel, navController, Modifier.padding(padding))
            }
        }
    }
}