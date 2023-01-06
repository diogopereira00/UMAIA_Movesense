package com.umaia.movesense.model

sealed class MovesenseTimerEvent{
    object START : MovesenseTimerEvent()
    object END : MovesenseTimerEvent()
}