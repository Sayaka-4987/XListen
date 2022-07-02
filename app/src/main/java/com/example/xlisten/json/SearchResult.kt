package com.example.xlisten.json
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val code: Int,
    val result: Result
) {
    @Serializable
    data class Result(
        val hasMore: Boolean,
        val songCount: Int,
        val songs: List<Song>
    ) {
        @Serializable
        data class Song(
            val album: Album,
            val artists: List<Artist>,
            val copyrightId: Int,
            val duration: Int,
            val fee: Int,
            val ftype: Int,
            val id: Int,
            val mark: Long,
            val mvid: Int,
            val name: String,
            val rtype: Int,
            val status: Int,
        ) {
            @Serializable
            data class Album(
                val artist: Artist,
                val copyrightId: Int,
                val id: Int,
                val mark: Int,
                val name: String,
                val picId: Long,
                val publishTime: Long,
                val size: Int,
                val status: Int
            ) {
                @Serializable
                data class Artist(
                    val albumSize: Int,
                    val id: Int,
                    val img1v1: Int,
                    val img1v1Url: String,
                    val name: String,
                    val picId: Int,
                )
            }

            @Serializable
            data class Artist(
                val albumSize: Int,
                val id: Int,
                val img1v1: Int,
                val img1v1Url: String,
                val name: String,
                val picId: Int,
            )
        }
    }
}