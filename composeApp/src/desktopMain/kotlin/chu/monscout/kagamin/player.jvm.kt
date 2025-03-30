package chu.monscout.kagamin

import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioPlayerJVM
import chu.monscout.kagamin.audio.AudioTrack

actual val createAudioPlayer: AudioPlayer<AudioTrack> get() = AudioPlayerJVM() as AudioPlayer<AudioTrack>