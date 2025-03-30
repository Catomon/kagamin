package com.github.catomon.yukinotes.di

import chu.monscout.kagamin.ui.KagaminViewModel
import org.koin.dsl.module

val appModule = module {
    factory { KagaminViewModel() }
}