package chu.monscout.kagamin

import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaPlayerJVM
import chu.monscout.kagamin.audio.DenpaTrack

actual val createDenpaPlayer: DenpaPlayer<DenpaTrack> get() = DenpaPlayerJVM() as DenpaPlayer<DenpaTrack>