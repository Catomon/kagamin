package com.github.catomon.kagamin.ui

import com.github.catomon.kagamin.LocalSnackbarHostState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.screens.PlayerScreen
import com.github.catomon.kagamin.ui.screens.PlayerScreenDestination
import com.github.catomon.kagamin.ui.screens.SettingsDestination
import com.github.catomon.kagamin.ui.screens.SettingsScreen

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