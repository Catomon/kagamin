package chu.monscout.kagamin.audio

actual val createAudioPlayer: AudioPlayer<AudioTrack> get() = AudioPlayerJVM() as AudioPlayer<AudioTrack>