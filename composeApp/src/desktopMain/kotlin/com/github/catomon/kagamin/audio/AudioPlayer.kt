package com.github.catomon.kagamin.audio

import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.AndroidMusicWithThumbnail
import dev.lavalink.youtube.clients.AndroidVr
import dev.lavalink.youtube.clients.AndroidVrWithThumbnail
import dev.lavalink.youtube.clients.AndroidWithThumbnail
import dev.lavalink.youtube.clients.MWebWithThumbnail
import dev.lavalink.youtube.clients.Music
import dev.lavalink.youtube.clients.MusicWithThumbnail
import dev.lavalink.youtube.clients.Web
import dev.lavalink.youtube.clients.WebEmbedded
import dev.lavalink.youtube.clients.WebEmbeddedWithThumbnail
import dev.lavalink.youtube.clients.WebWithThumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.sampled.AudioInputStream


class AudioPlayer(
    var resultHandler: AudioLoadResultHandler
) {
    var outputFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE
    val playerManager = DefaultAudioPlayerManager()
    val player = playerManager.createPlayer()

    var ytManager: YoutubeAudioSourceManager = YoutubeAudioSourceManager(
//        true,
//        Music(),
//        AndroidVr(),
//        Web(),
//        WebEmbedded(),
//        MusicWithThumbnail(),
//        WebWithThumbnail(),
//        WebEmbeddedWithThumbnail(),
//        MWebWithThumbnail(),
//        AndroidMusicWithThumbnail(),
//        AndroidVrWithThumbnail(),
//        AndroidWithThumbnail(),
    )

    var remoteSourcesRegistered = false

    init {
        playerManager.configuration.outputFormat = outputFormat

        ytManager.setPlaylistPageCount(400)
        playerManager.registerSourceManager(ytManager)
        AudioSourceManagers.registerLocalSource(playerManager)
        registerRemoteSources()
    }

    fun registerRemoteSources() {
        try {
            AudioSourceManagers.registerRemoteSources(playerManager)
            remoteSourcesRegistered = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addAudioEventListener(listener: AudioEventListener) {
        player.addListener(listener)
    }

    suspend fun loadItem(identifier: String) = withContext(Dispatchers.IO) {
        playerManager.loadItemSync(identifier, resultHandler)
    }

    fun createAudioInputStream(): AudioInputStream =
        AudioPlayerInputStream.createStream(player, outputFormat, 0, true)
}
