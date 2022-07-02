package com.example.xlisten.subpage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage
import com.example.xlisten.*
import com.example.xlisten.R
import com.example.xlisten.json.*
import kotlinx.coroutines.launch

// 艺术家详情页面
@Composable
fun ArtistScreen (backStackEntry: NavBackStackEntry) {
    var res by remember{ mutableStateOf<ArtistDetail?>(null)}

    val artistId = backStackEntry.arguments?.getInt("artist_id")
    val composableScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<ArtistDetail>("artist/detail?id=$artistId").fold(
                success = { res = it },
                failure = { /* ... */ }
            )
        }
    }

    Scaffold(bottomBar = { Column { MiniMusicPlayer(player = player) } }) {
        Column(modifier = Modifier.padding(all = 20.dp).fillMaxSize()) {
            res?.data?.artist?.let {
                ArtistTopArea(it.name, it.cover)
                ChoseBar(res)
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun ArtistTopArea(name: String, pic: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 艺术家名
        Text(text = name, style = MaterialTheme.typography.h5)
        // 艺术家图片加载
        AsyncImage(
            model = pic,
            contentDescription = "艺术家照片",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 30.dp)
                .fillMaxWidth(1f)
                .clip(shape = MaterialTheme.shapes.medium)
        )
    }
}


@Composable
fun ChoseBar(detail: ArtistDetail?) {
    val items = listOf("百科", "歌曲", "专辑")
    var selected by remember { mutableStateOf(0) }

    Column {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            itemsIndexed(items) { index, item ->
                Column(
                    Modifier
                        .width(50.dp)
                        .clickable { selected = index }
                ) {
                    Text(
                        text = item,
                        color = if (selected == index) MaterialTheme.colors.primary
                                else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    if (selected == index) {
                        Box(
                            Modifier
                                .padding(top = 3.dp)
                                .width(50.dp)
                                .height(1.dp)
                                .background(MaterialTheme.colors.primary)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        // 下方选择展示
        when (selected) {
            0 -> ArtistInformation(detail)
            1 -> detail?.let { ArtistSongList(it.data.artist.id) }
            2 -> detail?.let { ArtistAlbumList(it.data.artist.id) }
        }
    }
}

// 显示艺术家介绍信息
@Composable
fun ArtistInformation(detail: ArtistDetail?) {
    Column(modifier = Modifier.fillMaxSize())  {
        Text(
            text = "简介",
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(modifier = Modifier
            .height(10.dp)
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            detail?.let {
                Text(
                    text = it.data.artist.briefDesc,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

// 显示艺术家名下歌曲列表
@Composable
fun ArtistSongList(id: Int) {
    var singerTopSongs by remember{ mutableStateOf<ArtistTopSongs?>(null)}
    val composableScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<ArtistTopSongs>("artist/Top/song?id=$id").fold(
                success = { singerTopSongs = it },
                failure = { /* ... */ }
            )
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        singerTopSongs?.let {
            items(singerTopSongs!!.songs) { SongChip(it.toSong()) }
        }
    }
}

// 显示艺术家名下专辑列表
@Composable
fun ArtistAlbumList(id: Int) {
    var artistAlbums by remember { mutableStateOf<ArtistHotAlbums?>(null) }
    val composableScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        composableScope.launch {
            RPC.get<ArtistHotAlbums>("artist/album?id=${id}&limit=30").fold(
                success = { artistAlbums = it },
                failure = { /* ... */ }
            )
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        artistAlbums?.let {
            items(it.hotAlbums) {
                albumItem -> AlbumChip(album = albumItem)
            }
        }
    }
}

// 专辑卡片
@Composable
fun AlbumChip(album: ArtistHotAlbums.HotAlbum) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { navController.navigate("/album/" + album.id) }
    ) {
        AsyncImage(
            //网络图片加载
            model = album.picUrl,
            contentDescription = "专辑封面图片",
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
                .size(50.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = album.name,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = album.subType + "·" + album.company,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
                    style = MaterialTheme.typography.body2
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24),
            contentDescription = "点击进入专辑",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        )
    }
}