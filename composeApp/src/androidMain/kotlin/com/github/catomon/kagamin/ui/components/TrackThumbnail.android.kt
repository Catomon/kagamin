package com.github.catomon.kagamin.ui.components

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.github.catomon.kagamin.data.AudioTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal actual fun rememberTrackThumbnail(track: AudioTrack?, size: Int): ImageBitmap? {
    track ?: return null

    val artUri = remember(track) { track.uri.toUri() }
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(track) {
        bitmap = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(artUri, Size(200, 200), null).asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()

            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(track.uri)
                val artBytes = mmr.embeddedPicture
                if (artBytes != null) BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size).asImageBitmap() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                mmr.release()
            }
        }
    }

    return bitmap
}