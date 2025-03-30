package chu.monscout.kagamin

import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioPlayerAndy
import chu.monscout.kagamin.audio.AudioTrack

actual val createAudioPlayer: AudioPlayer<AudioTrack> = AudioPlayerAndy(
    playerContext?.invoke() ?: throw IllegalStateException("playerContext == null")
) as AudioPlayer<AudioTrack>
