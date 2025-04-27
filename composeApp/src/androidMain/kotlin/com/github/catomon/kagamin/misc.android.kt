package com.github.catomon.kagamin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
actual fun MultiFilePicker(
    show: MutableState<Boolean>,
    audioPlayer: AudioPlayer<AudioTrack>,
    currentPlaylistName: String
) {
    val a = LocalSnackbarHostState.current
    val fileType = listOf("mp3", "wav")
    MultipleFilePicker(show = show.value, fileExtensions = fileType) { files ->
        show.value = false
        if (files != null) {
            //it.platformFile desk - File, android - Uri
            audioPlayer.load(files.map {
                val uri = it.platformFile as Uri
                (playerContext?.invoke() as Activity).contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION// or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                uri.toString()
            })

            savePlaylist(currentPlaylistName, audioPlayer.playlist.value.toTypedArray())
        }

        //fixme
        GlobalScope.launch {
            a.showSnackbar("${files?.size ?: 0} tracks were added.")
        }
    }
}