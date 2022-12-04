package com.umaia.movesense.model

sealed class MovesenseWifi {
    object AVAILABLE : MovesenseWifi()
    object UNAVAILABLE : MovesenseWifi()
}