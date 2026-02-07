package com.example.kdragonball.shared

import com.example.kdragonball.shared.di.sharedModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin as koinStop

/**
 * Helper object for initializing Koin from iOS.
 */
object KoinHelper {
    fun doInitKoin() {
        startKoin {
            modules(sharedModules)
        }
    }

    fun doStopKoin() {
        koinStop()
    }
}
