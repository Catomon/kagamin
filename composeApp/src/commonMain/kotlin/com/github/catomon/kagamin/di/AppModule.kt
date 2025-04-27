package com.github.catomon.kagamin.di

import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import org.koin.dsl.module

val appModule = module {
    single { KagaminViewModel() }
}