package com.seeker.musicplayer.model


data class LocalMusicBean(
    val id: String,
    val music: String,
    val artist: String,
    val album: String,
    val duration: String,
    val path: String
)