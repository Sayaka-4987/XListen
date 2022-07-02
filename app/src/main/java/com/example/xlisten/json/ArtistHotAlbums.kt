package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class ArtistHotAlbums(
    val artist: Artist,
    val code: Int,
    val hotAlbums: List<HotAlbum>,
    val more: Boolean
) {
    @Serializable
    data class Artist(
        val id: Int,
        val name: String,
        val picUrl: String,
    )

    @Serializable
    data class HotAlbum(
        val artist: Artist,
        val company: String,
        val description: String,
        val id: Int,
        val name: String,
        val picUrl: String,
        val subType: String,
    )
}