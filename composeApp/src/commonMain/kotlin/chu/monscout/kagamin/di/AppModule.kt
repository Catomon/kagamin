package com.github.catomon.yukinotes.di

import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import org.koin.dsl.module

val appModule = module {
    single { KagaminViewModel() }
}