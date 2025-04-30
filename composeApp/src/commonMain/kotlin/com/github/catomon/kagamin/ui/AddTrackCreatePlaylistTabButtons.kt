package com.github.catomon.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun AddTrackCreatePlaylistTabButtons(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(KagaminTheme.backgroundTransparent)
            .height(32.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            "Add tracks",
            fontSize = 10.sp,
            color = if(viewModel.currentTab == Tabs.ADD_TRACKS) Color.White else KagaminTheme.theme.buttonIcon,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    viewModel.currentTab = Tabs.ADD_TRACKS
                }
        )

        Text(
            "Create playlist",
            fontSize = 10.sp,
            color = if(viewModel.currentTab == Tabs.CREATE_PLAYLIST) Color.White else KagaminTheme.theme.buttonIcon,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    viewModel.currentTab = Tabs.CREATE_PLAYLIST
                }
        )
    }
}