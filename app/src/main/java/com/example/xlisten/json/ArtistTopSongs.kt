package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class ArtistTopSongs(
    val code: Int,
    val more: Boolean,
    val songs: List<SongWithPic>
)