package com.github.catomon.kagamin.ui.components

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.github.catomon.kagamin.audio.AudioTrackAndy

actual fun getThumbnail(trackUri: String): ImageBitmap? {
    val artworkData = (audioTrack as AudioTrackAndy).mediaItem?.mediaMetadata?.artworkData ?: return null
    if (artworkData != null) {
        val bitmap = BitmapFactory.decodeByteArray(artworkData, 0, artworkData.size)
        return bitmap?.asImageBitmap()
    }

    return null
}