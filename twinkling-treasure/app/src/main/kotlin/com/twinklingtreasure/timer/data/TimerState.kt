package com.twinklingtreasure.timer.data

data class TimerState(
    val currentPhaseIndex: Int = 0,
    val secondsRemaining: Int  = TimerCycle.phases[0].durationSeconds,
    val isRunning: Boolean     = false,
)
