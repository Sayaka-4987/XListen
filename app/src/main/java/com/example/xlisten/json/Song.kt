package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Int,
    val name: String,
    val album: Album,
    val artist: List<Artist>,
    val fee: Int,
    val mv: Int,
)

@Serializable
data class Album(
    val id: Int,
    val name: String,
)

@Serializable
data class Artist(
    val id: Int,
    val name: String,
)

@Serializable
data class SongWithPic(
    val al: AlbumWithPic,
    val ar: List<Artist>,
    val fee: Int,
    val id: Int,
    val mv: Int,
    val name: String,
) {
    fun toSong() = Song(id, name, Album(al.id, al.name), ar, fee, mv)
}

@Serializable
data class AlbumWithPic(
    val id: Int,
    val name: String,
    val picUrl: String,
)