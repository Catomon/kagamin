package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.playerContext

actual val createAudioPlayer: AudioPlayer<AudioTrack> = AudioPlayerAndy(
    playerContext?.invoke() ?: throw IllegalStateException("playerContext == null")
) as AudioPlayer<AudioTrack>