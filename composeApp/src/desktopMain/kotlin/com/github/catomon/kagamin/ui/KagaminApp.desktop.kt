package com.github.catomon.kagamin.ui

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.ui.screens.CompactPlayerScreen
import com.github.catomon.kagamin.ui.screens.ControlsBottomPlayerScreen
import com.github.catomon.kagamin.ui.screens.PlayerScreen
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.screens.PlayerScreenDestination
import com.github.catomon.kagamin.ui.screens.SettingsDestination
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.screens.TinyPlayerScreen
import com.github.catomon.kagamin.ui.theme.KagaminTheme

@Composable
//Todo: not app, main window
actual fun KagaminApp(
    kagaminViewModel: KagaminViewModel,
    modifier: Modifier
) {
    val navController = rememberNavController()
    val layoutManager = LocalLayoutManager.current.currentLayout

    Scaffold(snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }, modifier = modifier, containerColor = KagaminTheme.colors.background) {
        NavHost(
            navController,
            startDestination = PlayerScreenDestination.toString(),
        ) {
            composable(PlayerScreenDestination.toString(),) {
                when (layoutManager.value) {
                    LayoutManager.Layout.Default -> {
                        PlayerScreen(kagaminViewModel.also {
                            it.currentTab = Tabs.TRACKLIST
                        }, navController)
                    }

                    LayoutManager.Layout.Compact -> {
                        CompactPlayerScreen(kagaminViewModel.also { it.currentTab = Tabs.PLAYBACK }, navController)
                    }

                    LayoutManager.Layout.Tiny -> {
                        TinyPlayerScreen(kagaminViewModel.also { it.currentTab = Tabs.PLAYBACK }, navController)
                    }

                    LayoutManager.Layout.BottomControls -> ControlsBottomPlayerScreen(kagaminViewModel, navController)
                }
            }

            composable(SettingsDestination.toString()) {
                com.github.catomon.kagamin.ui.screens.SettingsScreen(kagaminViewModel, navController)
            }
        }
    }
}