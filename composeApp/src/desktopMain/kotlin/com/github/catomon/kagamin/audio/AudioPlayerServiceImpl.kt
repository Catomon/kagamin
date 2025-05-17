package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.loadSettings
import com.github.catomon.kagamin.util.logMsg
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist as LavaAudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack as LavaAudioTrack

class AudioPlayerServiceImpl(
    val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
    val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    val dispatcherMain: CoroutineDispatcher = Dispatchers.Main
) : AudioPlayerService {

    private val _playState = MutableStateFlow(AudioPlayerService.PlayState.IDLE)
    override val playState: StateFlow<AudioPlayerService.PlayState> = _playState.asStateFlow()

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    override val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _volume = MutableStateFlow(0.25f)
    override val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _position = MutableStateFlow(0L)
    override val position: StateFlow<Long> = _position.asStateFlow()

    private val _crossfade = MutableStateFlow(false)
    override val crossfade: StateFlow<Boolean> = _crossfade.asStateFlow()

    override val playlistsManager: PlaylistsManager = PlaylistsManagerImpl(this)

    val audioLoader = LavaAudioLoader(LavaLoaderListener())

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(dispatcherDefault + job)

    private var positionUpdateJob: Job? = null

    private fun startPositionUpdates() {
        if (positionUpdateJob?.isActive == true) return
        positionUpdateJob = coroutineScope.launch {
            while (isActive && _playState.value == AudioPlayerService.PlayState.PLAYING) {
                _position.value = audioLoader.position
                delay(300)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    init {
        //todo move away
        val settings = loadSettings()
        when {
            settings.random -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.RANDOM)
            settings.repeat -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.REPEAT_TRACK)
            settings.repeatPlaylist -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.REPEAT_PLAYLIST)
        }

        _crossfade.value = settings.crossfade

        setVolume(settings.volume)
    }

    override suspend fun play(track: AudioTrack): Result<Boolean> {
        logMsg("Play track: ${track.uri}")

        _currentTrack.value = track

        withContext(dispatcherIO) {
            audioLoader.load(listOf(track.uri))
        }

        return Result.success(true)
    }

    override fun pause() {
        audioLoader.pause()

        if (currentTrack.value != null)
            _playState.value = AudioPlayerService.PlayState.PAUSED
        else
            _playState.value = AudioPlayerService.PlayState.IDLE

        stopPositionUpdates()
    }

    override fun resume() {
        audioLoader.resume()

        if (currentTrack.value != null)
            _playState.value = AudioPlayerService.PlayState.PLAYING
        else
            _playState.value = AudioPlayerService.PlayState.IDLE

        startPositionUpdates()
    }

    override fun stop() {
        audioLoader.stop()

        _currentTrack.value = null
        _playState.value = AudioPlayerService.PlayState.IDLE

        stopPositionUpdates()
    }

    override suspend fun seek(position: Long) {
        audioLoader.seek(position)
    }

    override fun setVolume(volume: Float) {
        audioLoader.setVolume(volume)
        _volume.value = volume
    }

    inner class LavaLoaderListener : LavaAudioLoader.LoaderListener {
        override fun onTrackLoaded(track: LavaAudioTrack) {
            val currentTrack = currentTrack.value ?: return

            if (currentTrack.uri == track.info.uri) {
                audioLoader.play(track)
                resume()
            }
        }

        override fun onPlaylistLoaded(playlist: LavaAudioPlaylist) {
            stop()
        }

        override fun onLoadFailed() {
            stop()
        }

        override fun onTrackPlaybackEnded(track: LavaAudioTrack) {
            if (playlistsManager.playMode.value != PlaylistsManager.PlayMode.ONCE) {
                coroutineScope.launch(dispatcherMain) {
                    playlistsManager.nextTrack()
                }
            } else {
                stop()
            }
        }

        override fun onTrackPlaybackEndedLoadFailed(track: LavaAudioTrack) {
            coroutineScope.launch(dispatcherMain) {
                playlistsManager.nextTrack()
            }
        }

        override fun onTrackPlaybackStuck(track: LavaAudioTrack) {
            coroutineScope.launch(dispatcherMain) {
                playlistsManager.nextTrack()

            }
        }

        override fun onTrackPlaybackError(track: LavaAudioTrack) {
            coroutineScope.launch(dispatcherMain) {
                playlistsManager.nextTrack()
            }
        }
    }
}