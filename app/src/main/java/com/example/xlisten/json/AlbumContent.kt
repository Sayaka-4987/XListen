package com.example.xlisten.json
import kotlinx.serialization.Serializable

@Serializable
data class AlbumContent(
    val album: AlbumWithPic,
    val code: Int,
    val resourceState: Boolean,
    val songs: List<SongWithPic>
)