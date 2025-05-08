package com.github.catomon.kagamin.audio

actual fun <T : AudioTrack> createAudioTrack(uri: String, name: String): T {
    return AudioTrackJVM(uri = uri, overrideName = name) as T
}