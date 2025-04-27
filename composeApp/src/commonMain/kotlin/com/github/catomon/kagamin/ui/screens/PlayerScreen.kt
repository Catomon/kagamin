package com.github.catomon.kagamin.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Serializable
object PlayerScreenDestination {
    override fun toString(): String {
        return "player_screen"
    }
}

@Composable
expect fun PlayerScreen(
    viewModel: KagaminViewModel = viewModel { KagaminViewModel() },
    navController: NavHostController,
    modifier: Modifier = Modifier,
)