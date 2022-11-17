package com.umaia.movesense.model

sealed class MoveSenseEvent {
    object START : MoveSenseEvent()
    object STOP : MoveSenseEvent()
}