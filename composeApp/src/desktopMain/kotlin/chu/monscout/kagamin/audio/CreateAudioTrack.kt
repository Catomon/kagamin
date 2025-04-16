package chu.monscout.kagamin.audio

actual fun <T : AudioTrack> createAudioTrack(uri: String, name: String): T {
    return AudioTrackJVM(uri = uri, name = name) as T
}