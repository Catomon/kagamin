package com.github.catomon.kagamin.audio

actual fun getAudioPlayerServiceImpl(): AudioPlayerService {
    return AudioPlayerServiceImpl()
}