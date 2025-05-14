package com.github.catomon.kagamin

import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.data.PlaylistData
import com.github.catomon.kagamin.data.TrackData
import com.github.catomon.kagamin.util.echoMsg
import kotlinx.serialization.json.Json
import java.io.File

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

@Deprecated(
    "AudioTrackJVM(trackData).trackData", ReplaceWith(
        "AudioTrackJVM(trackData).trackData",
    )
)
fun AudioTrack.toTrackData() = TrackData(uri, title, author)

fun savePlaylist(name: String, tracks: Array<AudioTrack>) {
    savePlaylist(name, tracks.map { it.trackData })
}

fun savePlaylist(name: String, tracks: List<TrackData>) {
    val playlistsFolder = File(userDataFolder.path + "/playlists")
    if (!playlistsFolder.exists())
        playlistsFolder.mkdirs()

    val file = File(userDataFolder.path + "/playlists/$name.pl")
    val playlistData = PlaylistData(tracks.toTypedArray())
    if (!file.exists()) {
        file.createNewFile()
    }
    file.writeText(json.encodeToString(playlistData))

    echoMsg("Playlist saved: $name")
}

fun loadPlaylists(): Map<String, PlaylistData> {
    val playlists = mutableMapOf<String, PlaylistData>()
    val playlistsFolder = File(playlistsFolder)

    for (file in playlistsFolder.listFiles() ?: return playlists) {
        if (file.isFile)
            playlists[file.nameWithoutExtension] = json.decodeFromString(file.readText())
    }

    echoMsg("Playlists loaded.")

    return playlists
}

fun loadPlaylist(name: String): PlaylistData? {
    val file = File("$playlistsFolder$name.pl")
    if (!file.exists())
        return null

    echoMsg("Playlist loaded: $name.")

    return json.decodeFromString(file.readText())
}