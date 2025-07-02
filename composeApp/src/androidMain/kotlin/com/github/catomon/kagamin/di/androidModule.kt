package com.github.catomon.kagamin.di

import androidx.media3.exoplayer.ExoPlayer
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.AudioPlayerServiceImpl
import org.koin.dsl.module

val androidModule = module {
    single { ExoPlayer.Builder(get()).build() }
    single<AudioPlayerService> { AudioPlayerServiceImpl(get()) }
}