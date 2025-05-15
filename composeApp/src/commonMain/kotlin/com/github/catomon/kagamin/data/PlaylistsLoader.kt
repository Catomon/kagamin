package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.util.echoMsg
import kotlinx.serialization.json.Json
import java.io.File

object PlaylistsLoader {
    val playlistsFolder get() = userDataFolder.path + "/playlists/"
    private val json = Json { ignoreUnknownKeys = true;  }

    fun removePlaylist(name: String) {
        val playlistsFolder = File(userDataFolder.path + "/playlists")
        if (!playlistsFolder.exists())
            return

        val file = File(userDataFolder.path + "/playlists/$name.pl")
        if (!file.exists())
            return
        if (file.delete())
            echoMsg("Playlist saved: $name")
    }

    //todo suspend
    fun savePlaylist(playlist: Playlist) {
        val name = playlist.name

        val playlistsFolder = File(userDataFolder.path + "/playlists")
        if (!playlistsFolder.exists())
            playlistsFolder.mkdirs()

        val file = File(userDataFolder.path + "/playlists/$name.pl")
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(json.encodeToString(playlist))

        echoMsg("Playlist saved: $name")
    }

    fun loadPlaylists(): List<Playlist> {
        val playlists = mutableListOf<Playlist>()
        val playlistsFolder = File(playlistsFolder)

        for (file in playlistsFolder.listFiles() ?: return playlists) {
            if (file.isFile) {
                val playlist: Playlist = json.decodeFromString(file.readText())
                playlists.add(playlist)
            }
        }

        echoMsg("Playlists loaded.")

        return playlists
    }

    //todo forbid creation playlists with same name
    fun loadPlaylist(playlist: Playlist): Playlist? {
        val name = playlist.name
        val file = File("$playlistsFolder$name.pl")
        if (!file.exists())
            return null

        echoMsg("Playlist loaded: $name.")

        return json.decodeFromString(file.readText())
    }
}
