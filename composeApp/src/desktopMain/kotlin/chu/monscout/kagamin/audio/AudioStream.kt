package chu.monscout.kagamin.audio

import chu.monscout.kagamin.util.logMsg
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread

class AudioStream(
    private val audioInputStream: AudioInputStream,
    private val audioFormat: AudioFormat = audioInputStream.format
) {
    private val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)
    private val sourceDataLine: SourceDataLine?
        get() =
            if (AudioSystem.isLineSupported(dataLineInfo))
                AudioSystem.getLine(dataLineInfo) as SourceDataLine?
            else
                null

    var isActive = false
        private set

    private var playbackThread = newPlaybackThread(start = true)

    private fun newPlaybackThread(start: Boolean = false) = thread(start) {
        playbackLoop()
    }.apply {
        name = "AudioStream-PlaybackThread"
    }

    private fun playbackLoop() {
        while (true) {
            if (!isActive) return

            Thread.sleep(1000)
            while (!AudioSystem.isLineSupported(dataLineInfo)) {
                Thread.sleep(1000)
            }

            val sourceDataLine: SourceDataLine = sourceDataLine ?: continue
            sourceDataLine.open(audioFormat)
            sourceDataLine.start()

            this@AudioStream.logMsg("DataLine is open, started.")

            var size: Int
            val buffer = ByteArray(audioFormat.channels * 960 * 2)
            while (sourceDataLine.isOpen) {
                size = audioInputStream.read(buffer)
                if (size >= 0) {
                    sourceDataLine.write(buffer, 0, size)
                }

                if (!isActive) return
            }

            this@AudioStream.logMsg("DataLine is closed.")
        }
    }


    fun stop() {
        if (playbackThread.isAlive) {
            isActive = false
            playbackThread.join()
        }
    }

    private fun start() {
        if (playbackThread.isAlive) return

        playbackThread = newPlaybackThread(start = true)
    }
}
