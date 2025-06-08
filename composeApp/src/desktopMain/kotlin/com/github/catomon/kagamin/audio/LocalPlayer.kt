package com.github.catomon.kagamin.audio

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.format.AudioPlayerInputStream
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class LocalPlayer(
    val player: AudioPlayer,
    val outputFormat: AudioDataFormat
) {
    val playback = AudioPlayback(AudioPlayerInputStream.createStream(player, outputFormat, 0, true))
}