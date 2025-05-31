package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.AudioTagsReader
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.filterAudioFiles
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
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.awt.image.BufferedImage
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
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

    val loaderListener = LavaLoaderListener()
    val audioPlayerManager = AudioPlayerManager(loaderListener)

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(dispatcherDefault + job)

    private var positionUpdateJob: Job? = null

    private fun startPositionUpdates() {
        if (positionUpdateJob?.isActive == true) return
        positionUpdateJob = coroutineScope.launch {
            while (isActive && _playState.value == AudioPlayerService.PlayState.PLAYING) {
                _position.value = audioPlayerManager.position
                delay(300)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override suspend fun loadTracks(uris: List<String>): List<AudioTrack> {
        loaderListener.collectNextTracks = true
        val collected = withContext(Dispatchers.IO) {
            val (remoteList, localList) = uris.groupBy { it.startsWith("http") }
                .let { (it[true] ?: emptyList()) to (it[false] ?: emptyList()) }

            audioPlayerManager.load(remoteList)

            val localTracksLoaded = loadLocalTracks(localList)

            loaderListener.collectedTracks.toList() + localTracksLoaded
        }

        loaderListener.collectNextTracks = false

        return collected
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun loadLocalTracks(files: List<String>): List<AudioTrack> {
        val files = files.map { File(it) }
        val cachingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        try {
            val trackFiles = mutableListOf<File>()

            withContext(Dispatchers.IO) {
                filterAudioFiles(files, trackFiles)
            }

            val loadedTracks = withContext(Dispatchers.IO) {
                trackFiles.map { audioFile ->
                    val path = audioFile.path
                    val audioHeader: AudioHeader?
                    val tag: Tag?
                    AudioTagsReader.read(audioFile).let {
                        audioHeader = it?.header
                        tag = it?.tag
                    }

                    cachingScope.launch {
                        ThumbnailCacheManager.cacheThumbnail(trackUri = path, retrieveImage = {
                            tag?.firstArtwork?.image as BufferedImage?
                        })
                    }

                    fun Tag.getOrNull(key: FieldKey): String? =
                        if (tag?.hasField(key) == true) getFirst(key) else null

                    val preciseLengthInSeconds: Double = audioHeader?.preciseTrackLength ?: 0.0
                    val preciseLengthInMilliseconds = (preciseLengthInSeconds * 1000).toLong()

                    AudioTrack(
                        id = Uuid.random().toString(),
                        uri = path,
                        title = tag?.getOrNull(FieldKey.TITLE)?.ifBlank { null }
                            ?: audioFile.nameWithoutExtension,
                        artist = tag?.getOrNull(FieldKey.ARTIST) ?: "",
                        album = tag?.getOrNull(FieldKey.ALBUM) ?: "",
                        duration = preciseLengthInMilliseconds,
                        artworkUri = null
                    )
                }
            }

            return loadedTracks
        } catch (ex: Exception) {
            ex.printStackTrace()

            return emptyList()
        }
    }

    override suspend fun play(track: AudioTrack): Result<Boolean> {
        logMsg("Play track: ${track.uri}")

        _currentTrack.value = track

        /** if loaded track uri == [_currentTrack].uri it'll be started by [loaderListener] */
        withContext(dispatcherIO) {
            audioPlayerManager.load(listOf(track.uri))
        }

        return Result.success(true)
    }

    override fun pause() {
        audioPlayerManager.pause()

        if (currentTrack.value != null)
            _playState.value = AudioPlayerService.PlayState.PAUSED
        else
            _playState.value = AudioPlayerService.PlayState.IDLE

        stopPositionUpdates()
    }

    override fun resume() {
        audioPlayerManager.resume()

        if (currentTrack.value != null)
            _playState.value = AudioPlayerService.PlayState.PLAYING
        else
            _playState.value = AudioPlayerService.PlayState.IDLE

        startPositionUpdates()
    }

    override fun stop() {
        audioPlayerManager.stop()

        _currentTrack.value = null
        _playState.value = AudioPlayerService.PlayState.IDLE

        stopPositionUpdates()
    }

    override suspend fun seek(position: Long) {
        audioPlayerManager.seek(position)
    }

    override fun setVolume(volume: Float) {
        audioPlayerManager.setVolume(volume)
        _volume.value = volume
    }

    override fun setCrossfade(enabled: Boolean) {
        _crossfade.value = enabled
    }

    inner class LavaLoaderListener : AudioPlayerManager.LoaderListener {

        val collectedTracks = mutableListOf<AudioTrack>()
        var collectNextTracks = false
            set(value) {
                collectedTracks.clear()
                field = value
            }

        private fun getYoutubeThumbnailUrl(
            youtubeUrl: String,
            size: String = "mqdefault"
        ): String? {
            val regex =
                Regex("(?:v=|be/|embed/|v/|youtu.be/|/v/|/embed/|watch\\?v=|&v=)([\\w-]{11})")
            val match = regex.find(youtubeUrl)
            val videoId = match?.groups?.get(1)?.value
            return videoId?.let { "https://i.ytimg.com/vi/$it/$size.jpg" }
        }

        @OptIn(ExperimentalUuidApi::class)
        override fun onTrackLoaded(track: LavaAudioTrack) {
            if (collectNextTracks) {
                collectedTracks.add(track.toAudioTrack())
                return
            }

            val currentTrack = currentTrack.value ?: return

            if (currentTrack.uri == track.info.uri) {
                audioPlayerManager.play(track)

                if (_playState.value != AudioPlayerService.PlayState.PLAYING)
                    resume()
            }
        }

        @OptIn(ExperimentalUuidApi::class)
        fun LavaAudioTrack.toAudioTrack(): AudioTrack {
            val title = info.title
            val author = info.author
            return AudioTrack(
                id = Uuid.random().toString(),
                uri = info.uri,
                title = if (title == "Unknown title") info.uri.substringAfterLast("/") else title,
                artist = if (author == "Unknown artist") "" else author,
                album = "",
                duration = duration,
                artworkUri = getYoutubeThumbnailUrl(info.uri)
            )
        }


        @OptIn(ExperimentalUuidApi::class)
        override fun onPlaylistLoaded(playlist: LavaAudioPlaylist) {
            if (collectNextTracks) {
                playlist.tracks.forEach { track ->
                    collectedTracks.add(track.toAudioTrack())
                }
                return
            }
        }

        override fun onLoadFailed() {
//            stop()
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
            logMsg { "onTrackPlaybackEndedLoadFailed: ${track.info.uri}" }

            tryPlayNextTrack()
        }

        override fun onTrackPlaybackStuck(track: LavaAudioTrack) {
            logMsg { "onTrackPlaybackStuck: ${track.info.uri}" }

            tryPlayNextTrack()
        }

        override fun onTrackPlaybackError(track: LavaAudioTrack) {
            logMsg { "onTrackPlaybackError: ${track.info.uri}" }

//            tryPlayNextTrack()
        }

        private var nextOnErrorJob: Job? = null
        private fun tryPlayNextTrack() {
            if (nextOnErrorJob == null) {
                nextOnErrorJob = coroutineScope.launch(dispatcherMain) {
                    delay(1000)
                    if (playState.value == AudioPlayerService.PlayState.PLAYING)
                        playlistsManager.nextTrack()
                    nextOnErrorJob = null
                }
            }
        }
    }
}