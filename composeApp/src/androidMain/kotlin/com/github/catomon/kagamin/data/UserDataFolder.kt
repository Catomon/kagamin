package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.MainApplication
import com.github.catomon.kagamin.appContext
import java.io.File

actual val userDataFolder: File get() = File((appContext?.let { it() } as MainApplication).filesDir.toURI())