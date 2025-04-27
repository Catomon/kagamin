package com.github.catomon.kagamin.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
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