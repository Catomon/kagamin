package com.github.catomon.kagamin.data

import com.github.catomon.kagamin.util.echoMsg
import com.github.catomon.kagamin.util.logErr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import java.io.File

object PlaylistsLoader {
    val playlistsFolder get() = userDataFolder.path + "/playlists/"
    private val json = Json { ignoreUnknownKeys = true; }

    private val mutex = Mutex()

    suspend fun removePlaylist(playlist: Playlist) = mutex.withLock {
        val name = playlist.name

        val playlistsFolder = File(userDataFolder.path + "/playlists")
        if (!playlistsFolder.exists())
            return true

        val file = File(userDataFolder.path + "/playlists/$name.pl")
        if (!file.exists())
            return true


        file.delete().also {
            if (it) echoMsg("Playlist removed: $name")
        }
    }

    suspend fun savePlaylist(playlist: Playlist) = mutex.withLock {
        withContext(Dispatchers.IO) {
            try {
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

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadPlaylists(): List<Playlist> = mutex.withLock {
        withContext(Dispatchers.IO) {
            val playlists = mutableListOf<Playlist>()

            val playlistsFolder = File(playlistsFolder)

            for (file in playlistsFolder.listFiles() ?: emptyArray<File>()) {
                try {
                    if (file.isFile) {
                        val playlist: Playlist = json.decodeFromString(file.readText())
                        playlists.add(playlist)
                    }
                } catch (e: MissingFieldException) {
                    this@PlaylistsLoader.logErr(e) { "playlist(file=${file.nameWithoutExtension} parsing error" }
                }
            }

            echoMsg { "Playlists loaded." }

            playlists
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun loadPlaylist(playlist: Playlist): Playlist? = mutex.withLock {
        val name = playlist.name
        val file = File("$playlistsFolder$name.pl")
        if (!file.exists())
            return null

        echoMsg("Playlist loaded: $name.")

        return try {
            json.decodeFromString(file.readText())
        } catch (e: MissingFieldException) {
            this@PlaylistsLoader.logErr(e) { "playlist(file=${file.nameWithoutExtension} parsing error" }
            playlist
        }
    }

    fun exists(playlist: Playlist) = exists(playlist.name)

    fun exists(name: String) = File("$playlistsFolder$name.pl").exists()
}
