package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.util.logMsg
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener
import com.sedmelluq.discord.lavaplayer.player.event.PlayerPauseEvent
import com.sedmelluq.discord.lavaplayer.player.event.PlayerResumeEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

class AudioPlayerManager(
    val loaderListener: LoaderListener
) {
    private val loader = AudioPlayer(AudioLoadResulHandlerImpl())
    private val playback = AudioPlayback(loader.createAudioInputStream())
    private val eventListener = AudioEventListenerImpl()
    val playingTrack get() = loader.player.playingTrack
    val position: Long get() = playingTrack?.position ?: 0L

    init {
        startDiscordRich()
        discordRich(Rich.IDLE, null)
        loader.addAudioEventListener(eventListener)
    }

    fun play(track: AudioTrack) {
        loader.player.playTrack(track)
    }

    suspend fun load(uris: List<String>) {
        uris.forEach {
            loader.loadItem(it)
        }
    }

    fun pause() {
        logMsg("Pause.")

        loader.player.isPaused = true
    }

    fun resume() {
        logMsg("Resume.")

        playback.start()

        loader.player.isPaused = false
    }

    fun stop() {
        loader.player.stopTrack()

        playback.stop()
    }

    fun setVolume(volume: Float) {
        loader.player.volume = (50 * volume).toInt()
    }

    fun seek(position: Long) {
        val audio = loader.player.playingTrack ?: return
        if (!audio.isSeekable) return
        audio.position = position
    }

    fun shutdown() {
        logMsg("Shutdown..")

        stopDiscordRich()
        loader.player.stopTrack()
        loader.playerManager.shutdown()
        playback.stop()
    }

    interface LoaderListener {
        fun onTrackLoaded(track: AudioTrack)

        fun onPlaylistLoaded(playlist: AudioPlaylist)

        fun onLoadFailed()

        fun onTrackPlaybackEnded(track: AudioTrack)

        fun onTrackPlaybackEndedLoadFailed(track: AudioTrack)

        fun onTrackPlaybackStuck(track: AudioTrack)

        fun onTrackPlaybackError(track: AudioTrack)
    }

    inner class AudioLoadResulHandlerImpl : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            logMsg("Track loaded: ${track.info.uri}")

//            val loadingTrack = this@LavaAudioLoader.loadingTrack
//            if (loadingTrack != null && loadingTrack.info.uri == track.info.uri) {
//                this@LavaAudioLoader.loadingTrack = track

                loaderListener.onTrackLoaded(track)

//                this@LavaAudioLoader.loadingTrack = null
//                return
//            }
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            logMsg("Remote playlist loaded: ${playlist.name}, ${playlist.tracks.size} tracks")

            loaderListener.onPlaylistLoaded(playlist)
        }

        override fun noMatches() {

        }

        override fun loadFailed(throwable: FriendlyException) {
            logMsg("Loading is failed.")

            throwable.printStackTrace()

            loaderListener.onLoadFailed()
        }
    }

    inner class AudioEventListenerImpl : AudioEventListener {

        private val player = loader.player

        private fun updateRich() {
            val track = player.playingTrack
            val paused = player.isPaused

            val rich = when {
                track == null -> Rich.IDLE
                paused -> Rich.PAUSED
                !paused -> Rich.LISTENING
                else -> Rich.IDLE
            }

            discordRich(rich, null) //todo
        }

        override fun onEvent(event: AudioEvent) {
            when (event) {
                is PlayerPauseEvent -> {

                }

                is PlayerResumeEvent -> {

                }

                is TrackStartEvent -> {

                }

                is TrackEndEvent -> {
                    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                    when (event.endReason) {
                        AudioTrackEndReason.FINISHED -> {
                            loaderListener.onTrackPlaybackEnded(event.track)
                        }

                        AudioTrackEndReason.LOAD_FAILED -> {
                            loaderListener.onTrackPlaybackEndedLoadFailed(event.track)
                        }

                        AudioTrackEndReason.STOPPED -> {}
                        AudioTrackEndReason.REPLACED -> {}
                        AudioTrackEndReason.CLEANUP -> {}
                    }
                }

                is TrackStuckEvent -> {
                    loaderListener.onTrackPlaybackStuck(event.track)
                }

                is TrackExceptionEvent -> {
                    loaderListener.onTrackPlaybackError(event.track)

                    event.exception?.printStackTrace()
                    //nextTrack() TrackEndEvent
                }
            }

            updateRich()
        }
    }
}