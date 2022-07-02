package com.example.xlisten

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.xlisten.json.Albums
import kotlinx.coroutines.launch

// 专辑榜单显示
@Composable
fun HotScreen() {
    val composableScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<Albums?>(null) }

    // 获取新专辑
    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<Albums>("album/new?area=ALL&limit=10").fold(
                success = { result = it },
                failure = { /* ... */ }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp)
    ) {
        Text(text = "最新专辑", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                result?.albums?.map {
                    album -> PopularAlbumCard(album)
                }
            }
        }

//        Spacer(modifier = Modifier.height(60.dp))
    }
}

// 热门专辑卡片
@Composable
fun PopularAlbumCard(album: Albums.Album) {
    Box(
        modifier = Modifier
            .height(240.dp)
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable {
                navController.navigate("/album/${album.id}")
            },
    ) {
        AsyncImage(
            model = album.picUrl,
            contentDescription = "专辑图片",
            contentScale = ContentScale.FillWidth,
            alpha = 0.8f,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = album.name, style = MaterialTheme.typography.h4, maxLines = 1, overflow = TextOverflow.Ellipsis)
                // 艺术家和专辑名
                Row {
                    for (artist in album.artists) {
                        // 用 "/" 分割多个艺术家
                        Text(
                            text = if (artist != album.artists.last()) artist.name + "/" else artist.name,
                            color = MaterialTheme.colors.onBackground.copy(0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(horizontal = 0.dp)
                                .clickable { navController.navigate("/artist/${artist.id}") }
                        )
                    }
                }
                album.description?.let { Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colors.onSurface.copy(0.4f)) }
            }
        }
    }
}
