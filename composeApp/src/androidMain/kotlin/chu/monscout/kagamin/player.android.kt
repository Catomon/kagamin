package chu.monscout.kagamin

import android.content.Context
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaPlayerAndy
import chu.monscout.kagamin.audio.DenpaTrack

actual val createDenpaPlayer: DenpaPlayer<DenpaTrack> = DenpaPlayerAndy(
    playerContext?.invoke() ?: throw IllegalStateException("playerContext == null")
) as DenpaPlayer<DenpaTrack>
