package chu.monscout.kagamin.audio

import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.util.logMsg
import chu.monscout.kagamin.util.logWarn
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
import java.io.File

class AudioPlayerJVM : BaseAudioPlayer<AudioTrackJVM>() {
    private val loader = AudioLoader(AudioLoadResulHandlerImpl())
    private val stream = AudioStream(loader.createAudioInputStream())
    private val audioEventListener = AudioEventListenerImpl()
    override val position: Long get() = loader.player.playingTrack?.position ?: 0L

    private var loadingTrack: AudioTrackJVM? = null

    init {
        startDiscordRich()
        discordRich(Rich.IDLE, null)
        loader.addAudioEventListener(audioEventListener)

//        val nipah = ClassLoader.getSystemClassLoader().getResource("nipah.mp3")?.toURI().toString()
//        loader.playerManager.loadItem(nipah, EmptyAudioResultHandler())

        val settings = loadSettings()
        crossfade.value = settings.crossfade
        if (settings.random)
            playMode.value = AudioPlayer.PlayMode.RANDOM
        else
            if (settings.repeat)
                playMode.value = AudioPlayer.PlayMode.REPEAT_TRACK
        setVolume(settings.volume)
    }

    override fun create() {
        super.create()
    }

    override fun load(uris: List<String>) {
        uris.forEach {
            loader.loadItem(it)
        }
    }

    override fun play(track: AudioTrackJVM): Boolean {
        logMsg("Play track: ${track.uri}")

        if (track.audioTrack == null) {
            logMsg("audioTrack == null, loading audio track: ${track.uri}")
//            super.play(track)
            loadingTrack = track
            load(listOf(track.uri))
            return true
        }

        val isStarted = loader.player.startTrack(track.audioTrack!!.makeClone(), false)
        return if (isStarted) {
            logMsg("Playback started.")
            super.play(track)
        } else {
            val nextTrackAvailable = nextTrack() != null

            if (nextTrackAvailable) {
                logMsg("Could not start playback, will try playing next track.")
            } else {
                logMsg("Could not start playback and no next track available.")
            }

            nextTrackAvailable
        }
    }

    override fun prevTrack(): AudioTrackJVM? {
        logMsg("Prev track.")

        val nextAudioTrack = super.prevTrack()

        loader.player.stopTrack()
        play(nextAudioTrack ?: return null)

        return nextAudioTrack
    }

    private var filePlayTried = 0

    override fun nextTrack(): AudioTrackJVM? {
        logMsg("Next track.")

        val nextAudioTrack = super.nextTrack()

        nextAudioTrack ?: let {
            stop()
            return null
        }

        loader.player.stopTrack()

        if (!nextAudioTrack.uri.startsWith("http")) {
            if (filePlayTried >= playlist.value.size) {
                logWarn("Playlist has no files to play.")
                return null
            }
            filePlayTried++

            if (!File(nextAudioTrack.uri).exists()) {
                logWarn("Track file does not exist: ${nextAudioTrack.uri}")

                return nextTrack()
            }
        }

        filePlayTried = 0

        play(nextAudioTrack)

        return nextAudioTrack
    }

    override fun queue(track: AudioTrackJVM) {
        logMsg("Queue track.")

        super.queue(track)

        //scheduler.queue(track.uri)
    }

    override fun freeQueue() {
        super.freeQueue()
    }

    override fun addToPlaylist(track: AudioTrackJVM) {
        super.addToPlaylist(track)
    }

    override fun removeFromPlaylist(track: AudioTrackJVM) {
        super.removeFromPlaylist(track)
    }

    override fun pause() {
        logMsg("Pause.")

        loader.player.isPaused = true

        super.pause()
    }

    override fun resume() {
        logMsg("Resume.")

        loader.player.isPaused = false

        if (currentTrack.value == null)
            nextTrack()

        super.resume()
    }

    override fun stop() {
        logMsg("Stop.")

        super.stop()

        loader.player.stopTrack()
    }

    override fun setVolume(volume: Float) {
        super.setVolume(volume)

        loader.player.volume = (50 * volume).toInt()
    }

    override fun seek(position: Long) {
        val audio = loader.player.playingTrack ?: return
        if (!audio.isSeekable) return
        audio.position = position
    }

    override fun shutdown() {
        logMsg("Shutdown..")

        super.shutdown()

        stopDiscordRich()
        loader.player.stopTrack()
        loader.playerManager.shutdown()
        stream.stop = true
    }

    inner class AudioLoadResulHandlerImpl : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack) {
            logMsg("Track loaded: ${track.info.uri}")

            val loadingTrack = this@AudioPlayerJVM.loadingTrack
            if (loadingTrack != null && loadingTrack.uri == track.info.uri) {
                loadingTrack.audioTrack = track
                play(loadingTrack)

                this@AudioPlayerJVM.loadingTrack = null
                return
            }

            addToPlaylist(AudioTrackJVM(track))
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            logMsg("Playlist loaded: ${playlist.name}")

            playlist.tracks.forEach {
                logMsg("Track added: remote playlist: ${playlist.name}, track: ${it.info.uri}")
                addToPlaylist(AudioTrackJVM(it))
            }
        }

        override fun noMatches() {

        }

        override fun loadFailed(throwable: FriendlyException) {
            logMsg("Loading is failed.")

            throwable.printStackTrace()
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

            discordRich(rich, track)
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
                            if (playMode.value != AudioPlayer.PlayMode.ONCE)
                                nextTrack()
                        }

                        AudioTrackEndReason.LOAD_FAILED -> {
                            nextTrack()
                        }

                        AudioTrackEndReason.STOPPED -> {}
                        AudioTrackEndReason.REPLACED -> {}
                        AudioTrackEndReason.CLEANUP -> {}
                    }
                }

                is TrackStuckEvent -> {
                    nextTrack()
                }

                is TrackExceptionEvent -> {
                    //nextTrack() TrackEndEvent
                }
            }

            updateRich()
        }
    }

    private class EmptyAudioResultHandler : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack?) {}
        override fun playlistLoaded(playlist: AudioPlaylist?) {}
        override fun noMatches() {}
        override fun loadFailed(exception: FriendlyException?) {}
    }
}