package com.example.xlisten.json


import kotlinx.serialization.Serializable

@Serializable
data class PlayerDetail(
    val code: Int,
    val songs: List<SongWithPic>
)