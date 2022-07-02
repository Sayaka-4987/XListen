package com.example.xlisten.json


import kotlinx.serialization.Serializable

/*
未登录状态只能获取不完整的歌单，但1000首够用了是吧
trackIds 是完整的，tracks 则是不完整的，
可拿全部 trackIds 请求一次 song/detail 接口获取所有歌曲的详情
(https://github.com/Binaryify/NeteaseCloudMusicApi/issues/452)
*/

// 歌单详情
@Serializable
data class PlayListDetail(
    val code: Int,
    val playlist: Playlist,
) {
    @Serializable
    data class Playlist(
        val coverImgUrl: String,    // 歌单封面图片链接
        val creator: Creator,       // 制作者
        val description: String?,   // 歌单描述
        val id: Long,               // 歌单编号
        val name: String,           // 歌单名称
        // val playCount: Int,         // 播放次数
        // val tags: List<String>,     // 标签
        // val updateTime: Long,       // 更新时间
        val tracks: List<Track>,    // 歌单列表
    ) {
        @Serializable
        data class Creator(
            val nickname: String,
            val userId: Long,
        )

        @Serializable
        data class Track(
            val al: Al,
            val ar: List<Artist>,
            val fee: Int,
            val id: Int,
            val mv: Int,
            val name: String,
        ) {
            @Serializable
            data class Al(
                val id: Int,
                val name: String,
            )

            fun toSong() = Song(id, name, Album(al.id, al.name), ar, fee, mv)
        }
    }
}