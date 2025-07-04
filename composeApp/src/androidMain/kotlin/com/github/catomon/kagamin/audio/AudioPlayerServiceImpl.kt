package com.github.catomon.kagamin.audio

import android.media.MediaMetadataRetriever
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.catomon.kagamin.data.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerServiceImpl(
    private val player: ExoPlayer
) : AudioPlayerService {

    private val _playState = MutableStateFlow(AudioPlayerService.PlayState.IDLE)
    override val playState: StateFlow<AudioPlayerService.PlayState> = _playState.asStateFlow()

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    override val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    override val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _position = MutableStateFlow(0L)
    override val position: StateFlow<Long> = _position.asStateFlow()

    private val _crossfade = MutableStateFlow(false)
    override val crossfade: StateFlow<Boolean> = _crossfade.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override val playlistsManager: PlaylistsManager = PlaylistsManagerImpl(currentTrack, player)

    init {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val index = player.currentMediaItemIndex
                playlistsManager.queueState.value.getOrNull(index)?.let {
                    _currentTrack.value = it
                }
            }
        })

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playState.value = when {
                    isPlaying -> AudioPlayerService.PlayState.PLAYING
                    player.playbackState == ExoPlayer.STATE_ENDED -> AudioPlayerService.PlayState.IDLE
                    else -> AudioPlayerService.PlayState.PAUSED
                }
            }
        })

        scope.launch {
            while (true) {
                _position.value = player.currentPosition
                delay(500)
            }
        }
    }

    override suspend fun loadTracks(uris: List<String>): List<AudioTrack> {
        return uris.map { uri ->
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(uri)
                AudioTrack(
                    uri = uri,
                    title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "",
                    artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "",
                    album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "",
                    duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
                        ?: 0L,
                    artworkUri = null
                )
            } catch (e: Exception) {
                AudioTrack(uri = uri)
            } finally {
                retriever.release()
            }
        }
    }

    override suspend fun play(track: AudioTrack): Result<Boolean> {
        return try {
            val i = playlistsManager.currentPlaylist.value.tracks.indexOf(track)
            if (i != -1) {
                player.seekToDefaultPosition(i)
            }
            player.playWhenReady = true
            _currentTrack.value = track
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun resume() {
        player.play()
    }

    override fun stop() {
        player.stop()
        _playState.value = AudioPlayerService.PlayState.IDLE
        _currentTrack.value = null
        _position.value = 0L
    }

    override suspend fun seek(position: Long) {
        player.seekTo(position)
        _position.value = position
    }

    override fun setVolume(volume: Float) {
        _volume.value = volume
        player.volume = volume
    }

    override fun setCrossfade(enabled: Boolean) {
        _crossfade.value = enabled
    }
}