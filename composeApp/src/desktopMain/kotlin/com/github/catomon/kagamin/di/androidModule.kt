package com.github.catomon.kagamin.di

import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.AudioPlayerServiceImpl
import org.koin.dsl.module

val desktopModule = module {
    single<AudioPlayerService> { AudioPlayerServiceImpl() }
}