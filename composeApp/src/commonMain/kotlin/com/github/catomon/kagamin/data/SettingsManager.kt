package com.github.catomon.kagamin.data

import kotlinx.serialization.json.Json
import java.io.File

fun saveSettings(settings: AppSettings) {
    try {
        val settingsFolder = File(userDataFolder.path)
        if (!settingsFolder.exists())
            settingsFolder.mkdirs()

        val file = File(userDataFolder.path + "/settings.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(Json.encodeToString(settings))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadSettings(): AppSettings {
    try {
        val settingsFile = File(userDataFolder.path + "/settings.json")
        if (settingsFile.exists())
            return Json.decodeFromString(settingsFile.readText())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return AppSettings()
}

