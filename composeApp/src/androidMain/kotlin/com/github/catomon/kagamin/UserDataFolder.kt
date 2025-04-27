package com.github.catomon.kagamin

import java.io.File

actual val userDataFolder: File get() = File((playerContext?.let { it() } as MainActivity).filesDir.toURI())