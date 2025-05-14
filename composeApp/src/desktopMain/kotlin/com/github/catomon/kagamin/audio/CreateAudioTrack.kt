package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.TrackData

actual fun <T : AudioTrack> createAudioTrack(trackData: TrackData): T {
    return AudioTrackJVM(trackData) as T
}