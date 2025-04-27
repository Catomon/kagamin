package com.github.catomon.kagamin.audio

actual val createAudioPlayer: AudioPlayer<AudioTrack> get() = AudioPlayerJVM() as AudioPlayer<AudioTrack>