package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.TrackData

expect fun <T : AudioTrack> createAudioTrack(trackData: TrackData): T