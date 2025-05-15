package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.playerContext

actual val createAudioPlayerService: AudioPlayerService<AudioTrack> = AudioPlayerAndy(
    playerContext?.invoke() ?: throw IllegalStateException("playerContext == null")
) as AudioPlayerService<AudioTrack>