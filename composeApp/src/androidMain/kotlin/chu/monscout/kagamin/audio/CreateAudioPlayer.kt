package chu.monscout.kagamin.audio

import chu.monscout.kagamin.playerContext

actual val createAudioPlayer: AudioPlayer<AudioTrack> = AudioPlayerAndy(
    playerContext?.invoke() ?: throw IllegalStateException("playerContext == null")
) as AudioPlayer<AudioTrack>