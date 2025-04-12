package com.github.catomon.yukinotes.di

import chu.monscout.kagamin.ui.screens.KagaminViewModel
import org.koin.dsl.module

val appModule = module {
    single { KagaminViewModel() }
}