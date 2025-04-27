package com.github.catomon.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.util.Tabs

@Composable
fun BottomBar(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .width(32.dp)
            .background(color = Colors.backgroundTransparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier
                .width(32.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PlaybackTabButton(
                {
                    if (state.currentTab != Tabs.PLAYBACK) {
                        state.currentTab = Tabs.PLAYBACK
                    }
                },
                color = if (state.currentTab == Tabs.PLAYBACK) Color.White else Colors.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            TracklistTabButton(
                {
                    if (state.currentTab != Tabs.TRACKLIST) {
                        state.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (state.currentTab == Tabs.TRACKLIST) Color.White else Colors.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (state.currentTab != Tabs.PLAYLISTS) {
                        state.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (state.currentTab == Tabs.PLAYLISTS) Color.White else Colors.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )
        }
    }
}