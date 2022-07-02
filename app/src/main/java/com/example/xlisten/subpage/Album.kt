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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.xlisten.*
import com.example.xlisten.json.*
import kotlinx.coroutines.launch
import java.util.*

// 介绍专辑信息
@Composable
fun AlbumScreen(backStackEntry: NavBackStackEntry) {
    val composableScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<AlbumContent?>(null) }
    val albumId = backStackEntry.arguments?.getInt("album_id")

    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<AlbumContent>("album?id=$albumId").fold(
                success = { result = it },
                failure = { /* ... */ }
            )
        }
    }

    Scaffold(bottomBar = { Column { MiniMusicPlayer(player = player) } }) {
        Column(
            modifier = Modifier.padding(all = 20.dp).fillMaxSize()
        ) {
            // 专辑图片和标题
            result?.let { AlbumHeading(albumContent = it) }
            Spacer(modifier = Modifier.height(20.dp))
            // 歌曲列表
            result?.let { AlbumSongList(it.songs) }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// 显示专辑封面图片和名称信息
@Composable
fun AlbumHeading(albumContent: AlbumContent) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 专辑图片加载
            AsyncImage(
                model = albumContent.album.picUrl,
                contentDescription = "专辑封面图片",
                modifier = Modifier
                    .size(128.dp)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .align(Alignment.CenterVertically)

            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                // 专辑名
                Text(text = albumContent.album.name, style = MaterialTheme.typography.h5)
                Spacer(modifier = Modifier.height(10.dp))
                // 专辑所有歌曲送入播放列表
                Button(
                    onClick = {
                        player.setPlaylist(albumContent.songs.map{ it.id }.toCollection(ArrayDeque()))
                    }
                ) {
                    Icon(
                        painter = painterResource(id = com.google.android.exoplayer2.R.drawable.exo_controls_play),
                        modifier = Modifier.size(20.dp),
                        contentDescription = "全部播放"
                    )
                    Text(text = " 播放全部")
                }
            }
        }
    }
}

@Composable
fun AlbumSongList(songs: List<SongWithPic>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(songs) { SongChip(it.toSong()) }
    }
}