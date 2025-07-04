package com.github.catomon.kagamin

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.util.echoMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun fetchAudioTracks(context: Context): Map<String, List<AudioTrack>> = withContext(Dispatchers.IO) {
    echoMsg { "fetchAudioTracks.." }

    val audioTracks = mutableListOf<Pair<String, AudioTrack>>()

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA
    )

    val selection = null //"${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val queryUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        queryUri,
        projection,
        selection,
        null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol) ?: ""
            val artist = cursor.getString(artistCol) ?: ""
            val album = cursor.getString(albumCol) ?: ""
            val duration = cursor.getLong(durationCol)
            val albumId = cursor.getLong(albumIdCol)
            val filePath = cursor.getString(dataCol)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val artworkUri = ContentUris.withAppendedId(
                "content://media/external/audio/albumart".toUri(),
                albumId
            ).toString()

            val folderName = try {
                File(filePath).parentFile?.name ?: "Unknown"
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown Folder"
            }

            audioTracks.add(
                folderName to AudioTrack(
                    id = id.toString(),
                    uri = contentUri.toString(),
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    artworkUri = artworkUri
                )
            )
        }
    }

    echoMsg { "fetchAudioTracks done: " + audioTracks.joinToString() }

    audioTracks.groupBy { it.first }.map { it.key to it.value.map { it.second } }.toMap()
}