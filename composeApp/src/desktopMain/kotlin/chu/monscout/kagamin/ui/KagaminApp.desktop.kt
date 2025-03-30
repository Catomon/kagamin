package chu.monscout.kagamin.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chu.monscout.kagamin.LayoutManager
import chu.monscout.kagamin.LocalLayoutManager
import chu.monscout.kagamin.LocalSnackbarHostState

@Composable
actual fun KagaminApp(
    kagaminViewModel: KagaminViewModel,
    modifier: Modifier
) {
    val navController = rememberNavController()

    Scaffold(snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }, modifier = modifier) {
        NavHost(
            navController,
            startDestination = PlayerScreenDestination.toString()
        ) {
            composable(PlayerScreenDestination.toString()) {
                when (LocalLayoutManager.current.currentLayout.value) {
                    LayoutManager.Layout.Default -> {
                        PlayerScreen(kagaminViewModel.also { it.currentTab = Tabs.TRACKLIST }, navController)
                    }

                    LayoutManager.Layout.Compact -> {
                        CompactPlayerScreen(kagaminViewModel.also { it.currentTab = Tabs.PLAYBACK }, navController)
                    }

                    LayoutManager.Layout.Tiny -> {
                        TinyPlayerScreen(kagaminViewModel.also { it.currentTab = Tabs.PLAYBACK }, navController)
                    }
                }
            }

            composable(SettingsDestination.toString()) {
                SettingsScreen(kagaminViewModel, navController)
            }
        }
    }
}