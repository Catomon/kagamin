package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.osName
import java.io.File

actual val userDataFolder: File =
    File(
        System.getProperty("user.home"),
        if (osName.contains("win")) "AppData/Roaming/Kagamin/" else ".local/share/Kagamin/"
    )

val cacheFolder = File(System.getenv("LOCALAPPDATA"), "Kagamin/cache/")

val defaultMediaFolder = File("${System.getProperty("user.home")}\\Music")