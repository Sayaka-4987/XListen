package com.example.xlisten.json

import kotlinx.serialization.Serializable

// 从 API 接收复数歌单的类型
@Serializable
data class Albums(
    val albums: List<Album>,
    val code: Int,
    val total: Int
) {
    @Serializable
    data class Album(
        val artists: List<Artist>,
        val description: String?,   // 专辑描述
        val id: Int,                // 专辑 id
        val name: String,           // 专辑名
        val picUrl: String,         // 专辑封面
    ) {
        @Serializable
        data class Artist(
            val id: Int,
            val name: String,
            val picUrl: String
        )
    }
}