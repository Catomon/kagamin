package chu.monscout.kagamin.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.util.UUID

class AudioTrackAndy(
    override val uri: String,
    override val id: String = UUID.randomUUID().toString(),
    override var author: String = "",
    override var name: String = "",
    override var duration: Long = Long.MAX_VALUE
) : AudioTrack {

    var mediaItem: MediaItem? = null
    set(value) {
        field = value
        author =  mediaItem?.mediaMetadata?.artist.toString() ?: ""
        name = mediaItem?.mediaMetadata?.title.toString() ?: ""
        duration = Long.MAX_VALUE
    }

    fun createMediaItem() : MediaItem {
        mediaItem = MediaItem.Builder().setUri(uri).setMediaId(id).build()

        return mediaItem!!
    }

    constructor(mediaItem: MediaItem) : this(
        mediaItem.localConfiguration?.uri.toString(),
        mediaItem.mediaId,
    ) {
        this.mediaItem = mediaItem
    }
}

class AudioPlayerAndy(context: Context) : BaseAudioPlayer<AudioTrackAndy>() {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    override val position: Long get() = player.contentPosition

    init {
//        player.addListener(object : Player.Listener {
//            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                nextTrack()
//            }
//        })
    }

    override fun create() {
        if (player.isCommandAvailable(Player.COMMAND_PREPARE))
            player.prepare()
    }

    override fun load(uris: List<String>) {
        uris.forEach { uri ->
            addToPlaylist(AudioTrackAndy(MediaItem.fromUri(uri)))
        }
    }

    override fun play(track: AudioTrackAndy): Boolean {
        if (!player.isCommandAvailable(Player.COMMAND_STOP)) return false

        player.stop()

        if (!player.isCommandAvailable(Player.COMMAND_CHANGE_MEDIA_ITEMS)) return false
        if (!player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) return false

        player.addMediaItem(track.mediaItem ?: track.createMediaItem())

        if (player.isCommandAvailable(Player.COMMAND_PREPARE))
            player.prepare()

        //player.play() is being called in resume() in super.play()

        return super.play(track)
    }

    override fun prevTrack(): AudioTrackAndy? {
        val nextAudioTrack = super.prevTrack()

        player.stop()

        if (nextAudioTrack != null) {
            player.addMediaItem(nextAudioTrack.mediaItem ?: nextAudioTrack.createMediaItem())
            player.prepare()
            player.play()
        }

        return nextAudioTrack
    }

    override fun nextTrack(): AudioTrackAndy? {
        val nextAudioTrack = super.nextTrack()

        player.stop()
        if (nextAudioTrack != null) {
            player.addMediaItem(nextAudioTrack.mediaItem ?: nextAudioTrack.createMediaItem())
            player.prepare()
            player.play()
        }

        return nextAudioTrack
    }

    override fun addToPlaylist(track: AudioTrackAndy) {
        super.addToPlaylist(track)
    }

    override fun removeFromPlaylist(track: AudioTrackAndy) {
        super.removeFromPlaylist(track)
    }

    override fun queue(track: AudioTrackAndy) {
        super.queue(track)

        //player.addMediaItem(track.mediaItem)
    }

    override fun freeQueue() {
        super.freeQueue()
        //player.clearMediaItems()
    }

    override fun pause() {
        if (player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE))
            player.pause()

        super.pause()
    }

    override fun resume() {
        if (player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE))
            player.play()

        super.resume()
    }

    override fun stop() {
        super.stop()

        if (player.isCommandAvailable(Player.COMMAND_STOP))
            player.stop()
    }

    override fun seek(position: Long) {
        player.seekTo(position)
    }

    override fun shutdown() {
        player.release()
    }
}