package ru.netology.mediaplayer

data class Track(
    val id: Long,
    val file: String,
    var isPlaying: Boolean = false,
    var isLoading: Boolean = false
)
