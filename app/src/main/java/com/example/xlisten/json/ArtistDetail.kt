package com.example.xlisten.json


import kotlinx.serialization.Serializable

@Serializable
data class ArtistDetail(
    val code: Int,
    val `data`: Data,
    val message: String
) {
    @Serializable
    data class Data(
        val artist: Artist,
        val blacklist: Boolean,
        val preferShow: Int,
        val showPriMsg: Boolean,
        val videoCount: Int
    ) {
        @Serializable
        data class Artist(
            val albumSize: Int,
            val briefDesc: String,
            val cover: String,
            val id: Int,
            val identities: List<String>,
            val musicSize: Int,
            val mvSize: Int,
            val name: String,
        )
    }
}