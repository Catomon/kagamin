package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.util.logMsg
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.math.log10
import kotlin.math.sqrt

class AudioPlayback(
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

    var isActive = true
        private set

    private var playbackThread: Thread? = newPlaybackThread(start = true)

    private fun newPlaybackThread(start: Boolean = false) = thread(start) {
        playbackLoop()
    }.apply {
        name = "AudioStream-PlaybackThread"
    }

    // ---v

    val amplitudeChannel = Channel<Float>(5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//    private var amplitudeListener: ((Float) -> Unit)? = null
//
//    fun interface AmplitudeListener {
//        fun onAmplitude(rms: Float)
//    }

//    fun setAmplitudeListener(listener: AmplitudeListener) {
//        amplitudeListener = { rms ->
//            CoroutineScope(Dispatchers.Main).launch {
//                listener.onAmplitude(rms)
//            }
//        }
//    }

    private fun processAmplitude(buffer: ByteArray, bytesRead: Int) {
        val samples = ByteBuffer.wrap(buffer, 0, bytesRead)
            .order(
                if (audioFormat.isBigEndian) ByteOrder.BIG_ENDIAN
                else ByteOrder.LITTLE_ENDIAN
            )
            .asShortBuffer()

        val floatSamples = FloatArray(samples.remaining())
        for (i in 0 until samples.remaining()) {
            floatSamples[i] = samples.get(i) / 32768f
        }

        val sumSquares = floatSamples.fold(0f) { acc, sample -> acc + sample * sample }
        val rms = sqrt(sumSquares / floatSamples.size)

//        amplitudeListener?.invoke(rms)
        amplitudeChannel.trySend(rms * 10)
    }

    // ---^


    private var gainControl: FloatControl? = null
    var volume =  0f
        @Synchronized set(value) {
            check(value in 0f..1f)

            field = value
            gainControl?.let {
                val minGain = it.minimum
                val maxGain = it.maximum
                val dB = 20 * log10(value)
                it.value = dB.coerceIn(minGain, maxGain)
            }
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

            this@AudioPlayback.logMsg { "DataLine is running." }

            gainControl = try {
                sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            } catch (e: Exception) {
                this@AudioPlayback.logMsg { "Volume control not supported: ${e.message}" }
                null
            }

            volume = volume

            var size: Int
            val buffer = ByteArray(audioFormat.channels * 960 * 2)
            while (sourceDataLine.isOpen) {
                size = audioInputStream.read(buffer)
                if (size >= 0) {
                    if (gainControl == null) {
                        applySoftwareVolume(buffer, size)
                    }

                    sourceDataLine.write(buffer, 0, size)

                    processAmplitude(buffer, size)
                }

                if (!isActive) return
            }

            this@AudioPlayback.logMsg { "DataLine is closed." }
        }
    }

    private fun applySoftwareVolume(buffer: ByteArray, bytesRead: Int) {
        val bufferView = ByteBuffer.wrap(buffer, 0, bytesRead)
            .order(if (audioFormat.isBigEndian) ByteOrder.BIG_ENDIAN else ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()

        for (i in 0 until bufferView.remaining()) {
            bufferView.put(i, (bufferView.get(i) * volume).toInt().toShort())
        }
    }

    fun stop() {
        val playbackThread = playbackThread
        if (playbackThread?.isAlive == true) {
            isActive = false
            playbackThread.join()
        }
    }

    fun start() {
        if (playbackThread?.isAlive == true) return

        playbackThread = newPlaybackThread(start = true)
    }
}
