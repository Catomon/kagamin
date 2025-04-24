package chu.monscout.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import chu.monscout.kagamin.audio.AudioTrack

expect fun getThumbnail(audioTrack: AudioTrack): ImageBitmap?