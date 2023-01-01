package com.umaia.movesense.model

sealed class MovesenseInternet {
    object AVAILABLE : MovesenseInternet()
    object UNAVAILABLE : MovesenseInternet()
}