package com.github.catomon.kagamin.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
expect fun KagaminApp(
    kagaminViewModel: KagaminViewModel = get(KagaminViewModel::class.java),
    modifier: Modifier = Modifier
)