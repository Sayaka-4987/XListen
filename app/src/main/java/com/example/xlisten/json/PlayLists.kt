package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class PlayLists(
    val playlists: List<Playlist>,
) {
    @Serializable
    data class Playlist(
        val coverImgUrl: String,    // 歌单封面图片链接
        val creator: Creator,       // 制作者
        val description: String?,   // 歌单描述
        val id: Long,               // 歌单编号
        val name: String,           // 歌单名称
        val playCount: Int,         // 播放次数
        val tags: List<String>,     // 标签
        val updateTime: Long,       // 更新时间
    ) {
        @Serializable
        data class Creator(
            val nickname: String,   // 用户名
            val userId: Int         // 用户 id
        )
    }
}

val developerPlayLists = listOf(
    PlayLists.Playlist(
        coverImgUrl = "https://p1.music.126.net/-KkPsEp-5BA6nlxxc25zgw==/109951167500865955.jpg",
        creator = PlayLists.Playlist.Creator("ZX_6677", 104635274),
        description = "",
        id = 7462306755,
        name = "One for Rock and Roll",
        playCount = 0,
        tags = listOf(""),
        updateTime = 1653927204025
    ),
    PlayLists.Playlist(
        coverImgUrl = "https://p1.music.126.net/iOeMIf1fhlHotBAx-Vooyw==/3404088002870760.jpg",
        creator = PlayLists.Playlist.Creator("RyannWang_", 1915813458),
        description = "",
        id = 7462391489,
        name = "air",
        playCount = 0,
        tags = listOf(""),
        updateTime = 1653930243842
    ),
    PlayLists.Playlist(
        coverImgUrl = "https://p1.music.126.net/pghfIxxBpoi4OSvkej__LQ==/109951167495523353.jpg",
        creator = PlayLists.Playlist.Creator("Svartalfheim_", 342109865),
        description = "",
        id = 7189483916,
        name = "ヾ(￣0￣； )ノ",
        playCount = 0,
        tags = listOf(""),
        updateTime = 1653927823531
    )
)


