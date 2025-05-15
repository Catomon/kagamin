package com.github.catomon.kagamin.audio

import android.app.Activity
import android.net.Uri
import androidx.media3.common.MediaItem
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.playerContext
import java.util.UUID

actual fun <T : AudioTrack> createAudioTrack(audioTrack: AudioTrack): T {
    val uri = Uri.parse(uri)
    val inputStream = (playerContext?.invoke() as Activity).contentResolver.let { contentResolver ->
        val input = contentResolver.openInputStream(uri)

        input
    }

    return (AudioTrackAndy(
        MediaItem.Builder().setUri(uri).setMediaId(UUID.randomUUID().toString()).build()
    ).also { it.title = name } as T).also { inputStream?.close() }
}