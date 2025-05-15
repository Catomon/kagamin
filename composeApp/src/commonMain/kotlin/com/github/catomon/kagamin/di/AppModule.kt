package com.github.catomon.kagamin.di

import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.getAudioPlayerServiceImpl
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import org.koin.dsl.module

val appModule = module {
    single<AudioPlayerService> { getAudioPlayerServiceImpl() }

    single<KagaminViewModel> { KagaminViewModel(get()) }
}
