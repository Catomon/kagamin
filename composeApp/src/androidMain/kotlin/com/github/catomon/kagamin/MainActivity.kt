package com.github.catomon.kagamin

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = KagaminTheme.colors.background.toArgb()

        setContent {
            KagaminTheme {
                Scaffold {

//                    LaunchedEffect(Unit) {
//                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
//                        }
//                    }

                    LibraryScreen(Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun PlayerScreen() {

}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    var tracks by remember { mutableStateOf<Map<String, List<AudioTrack>>>(emptyMap()) }
    var isScanning by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tracks = fetchAudioTracks(context)

        isScanning = false
    }

    Box(
        modifier
            .fillMaxSize()
            .background(KagaminTheme.colors.background), contentAlignment = Alignment.Center
    ) {
        AnimatedContent(isScanning) {
            if (it) {
                Text("Scanning...")
            } else {
                if (tracks.isEmpty()) {
                    Text("Empty.")
                } else {
                    LazyColumn(Modifier.fillMaxHeight()) {
                        items(tracks.keys.size) {
                            Text(tracks.keys.elementAt(it))
                        }
                    }
                }
            }
        }
    }
}

suspend fun fetchAudioTracks(context: Context): Map<String, List<AudioTrack>> = withContext(Dispatchers.IO) {
    println("trying..")

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
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            ).toString()

            val folderName = try {
                File(filePath).parentFile?.name ?: "Unknown"
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown Folder"
            }

            println(title)

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

    println("done.." + audioTracks.joinToString())

    audioTracks.groupBy { it.first }.map { it.key to it.value.map { it.second } }.toMap()
}