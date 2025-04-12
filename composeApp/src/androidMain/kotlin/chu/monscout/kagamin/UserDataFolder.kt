package chu.monscout.kagamin

import java.io.File

actual val userDataFolder: File get() = File((playerContext?.let { it() } as MainActivity).filesDir.toURI())