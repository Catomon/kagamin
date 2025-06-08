package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.util.logMsg
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener
import com.sedmelluq.discord.lavaplayer.player.event.PlayerPauseEvent
import com.sedmelluq.discord.lavaplayer.player.event.PlayerResumeEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackExceptionEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackStartEvent
import com.sedmelluq.discord.lavaplayer.player.event.TrackStuckEvent
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.lavalink.youtube.YoutubeAudioSourceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AudioPlayerManager(
    val loaderListener: LoaderListener
) {
    private var outputFormat: AudioDataFormat = StandardAudioDataFormats.COMMON_PCM_S16_BE
    private val playerManager = DefaultAudioPlayerManager()
    private var ytManager: YoutubeAudioSourceManager = YoutubeAudioSourceManager()
    private val loadResultHandler = AudioLoadResulHandlerImpl()

    private var player1 = LocalPlayer(playerManager.createPlayer(), outputFormat)
    private var player2 = LocalPlayer(playerManager.createPlayer(), outputFormat)

    private var players = player1 to player2

    private val player get() = players.first.player
    private val playback get() = players.first.playback

    private val eventListener = AudioEventListenerImpl()
    val playingTrack: AudioTrack? get() = player.playingTrack
    val position: Long get() = playingTrack?.position ?: 0L

    private var mVolume = 0.5f

    var crossfade = true

    companion object {
        lateinit var amplitudeChannel: Channel<Float>
            private set
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var crossfadeJob: Job? = null

    fun startCrossfade() {
        player.removeListener(eventListener)
        players = players.copy(players.second, players.first)
        player.addListener(eventListener)

        crossfadeJob?.cancel()
        players.first.player.volume = 0
        players.second.player.volume = (50 * mVolume).toInt()

        crossfadeJob = coroutineScope.launch {
            val steps = 60
            val delayPerStep = 3000L / steps
            for (step in 0..steps) {
                val fraction = step / steps.toFloat()

                players.first.player.volume = (50 * mVolume * fraction).toInt()
                players.second.player.volume = (50 * mVolume).toInt() + (-50 * mVolume * fraction).toInt()

                delay(delayPerStep)
            }

            players.first.player.volume = (50 * mVolume).toInt()
            players.second.player.volume = 0
        }
    }

    init {
        player.addListener(eventListener)

        //lavaplayer
        playerManager.configuration.outputFormat = outputFormat
        ytManager.setPlaylistPageCount(400)
        playerManager.registerSourceManager(ytManager)
        AudioSourceManagers.registerLocalSource(playerManager)
        try {
            AudioSourceManagers.registerRemoteSources(playerManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        startDiscordRich()
        discordRich(Rich.IDLE, null)

        amplitudeChannel = playback.amplitudeChannel
    }

    fun play(track: AudioTrack) {
        if (crossfade)
            startCrossfade()

        player.playTrack(track)
    }

    suspend fun load(uris: List<String>) {
        uris.forEach {
            withContext(Dispatchers.IO) {
                playerManager.loadItemSync(it, loadResultHandler as AudioLoadResultHandler)
            }
        }
    }

    fun pause() {
        logMsg("Pause.")

        player.isPaused = true
    }

    fun resume() {
        logMsg("Resume.")

//        playback.start()

        player.isPaused = false
    }

    fun stop() {
        player.stopTrack()

//        playback.stop()
    }

    fun setVolume(volume: Float) {
        mVolume = volume
        player.volume = (50 * volume).toInt()
    }

    fun seek(position: Long) {
        val audio = player.playingTrack ?: return
        if (!audio.isSeekable) return
        audio.position = position
    }

    fun shutdown() {
        logMsg("Shutdown..")

        stopDiscordRich()
        player.stopTrack()
        playerManager.shutdown()
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