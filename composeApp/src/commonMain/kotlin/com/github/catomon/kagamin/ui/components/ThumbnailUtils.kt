package com.github.catomon.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import com.github.catomon.kagamin.audio.AudioTrack

expect fun getThumbnail(audioTrack: AudioTrack): ImageBitmap?