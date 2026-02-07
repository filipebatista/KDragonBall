package com.example.kdragonball.shared.core.di

import com.example.kdragonball.shared.core.network.DragonBallApiDataSource
import com.example.kdragonball.shared.core.network.HttpClientProvider
import org.koin.dsl.module

val coreModule =
    module {
        single { HttpClientProvider.create() }
        single { DragonBallApiDataSource(get()) }
    }
