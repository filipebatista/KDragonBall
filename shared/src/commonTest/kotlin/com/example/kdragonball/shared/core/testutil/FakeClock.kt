package com.example.kdragonball.shared.core.testutil

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeClock(private var currentTime: Instant = Instant.fromEpochMilliseconds(1000000)) : Clock {
    override fun now(): Instant = currentTime

    fun setTime(time: Instant) {
        currentTime = time
    }

    fun advanceTimeBy(milliseconds: Long) {
        currentTime = Instant.fromEpochMilliseconds(currentTime.toEpochMilliseconds() + milliseconds)
    }
}
