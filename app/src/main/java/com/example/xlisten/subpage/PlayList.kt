package com.example.xlisten.subpage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.xlisten.MiniMusicPlayer
import com.example.xlisten.RPC
import com.example.xlisten.SongChip
import com.example.xlisten.json.PlayListDetail
import com.example.xlisten.player
import com.google.android.exoplayer2.R
import kotlinx.coroutines.launch
import java.util.*

// 歌单详细展示
@Composable
fun PlayList(backStackEntry: NavBackStackEntry) {
    val composableScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<PlayListDetail?>(null) }
    val listId = backStackEntry.arguments?.getLong("list_id")

    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<PlayListDetail>("playlist/detail?id=$listId").fold(
                success = { result = it },
                failure = { /* ... */ }
            )
        }
    }

    Scaffold(bottomBar = { Column { MiniMusicPlayer(player = player) } }) {
        Column(
            modifier = Modifier
                .padding(all = 20.dp)
                .fillMaxSize()
        ) {
            // 歌单图片和标题
            result?.let { it -> PlayListHeading(list = it.playlist) }
            Spacer(modifier = Modifier.height(20.dp))
            // 歌曲列表
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                result?.playlist?.let { it1 ->
                    items(it1.tracks) {
                        SongChip(song = it.toSong())
                    }
                }
            }
        }
    }
}

// 显示歌单的封面图片和名称信息
@Composable
fun PlayListHeading(list: PlayListDetail.Playlist) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 歌单图片加载
            AsyncImage(
                model = list.coverImgUrl,
                contentDescription = "歌单封面图片",
                modifier = Modifier
                    .size(128.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                // 歌单名
                Text(text = list.name, style = MaterialTheme.typography.h6, maxLines = 2, overflow = Ellipsis)
                // 歌单制作者
                Text(text = "by " + list.creator.nickname, color = MaterialTheme.colors.onBackground.copy(0.6f), maxLines = 1, overflow = Ellipsis)
                // 歌单描述
                list.description?.let { Text(text = it, color = MaterialTheme.colors.onBackground.copy(0.4f), maxLines = 1, overflow = Ellipsis) }

                Spacer(modifier = Modifier.height(10.dp))

                // 歌单所有歌曲送入播放列表
                Button(
                    onClick = { player.setPlaylist(list.tracks.map{ it.id }.toCollection(ArrayDeque())) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.exo_controls_play),
                        modifier = Modifier.size(20.dp),
                        contentDescription = "全部播放"
                    )
                    Text(text = " 播放全部")
                }
            }
        }
    }
}