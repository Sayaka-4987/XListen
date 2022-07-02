package com.example.xlisten

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.xlisten.json.PlayLists
import com.example.xlisten.json.PlayLists.Playlist
import com.example.xlisten.json.developerPlayLists
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


// TODO: 回头全局变量放一起, 这是歌单类型
enum class ListType {
    Top, HighQuality
}

// 主页界面
@Composable
fun HomeScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(all = 20.dp)) {

        Text(text = "猜您喜欢", style = MaterialTheme.typography.h5)
        Advertisement()

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "最热歌单推荐", style = MaterialTheme.typography.h5)
        RecommendedUserList(ListType.Top)

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "用户歌单推荐", style = MaterialTheme.typography.h5)
        RecommendedUserList(ListType.HighQuality)

        Spacer(modifier = Modifier.height(60.dp))
    }
}

// 开发者歌单展示
@Composable
fun AdCard(list: Playlist) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(120.dp)
            .clickable {
                navController.navigate("/playlist/${list.id}")
            },
    ) {
        AsyncImage(
            model = list.coverImgUrl,
            contentDescription = "歌单封面图片",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.lighting(multiply = Color.White.copy(0.2f), add = Color.Black),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Text(
            text = list.name,
            maxLines = 1,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(all = 15.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

// 广告栏
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Advertisement() {
    val pagerState = rememberPagerState(initialPage = 0)

    // 自动滚动
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.pageCount > 0) {
            delay(5000)
            // 无限循环
            pagerState.scrollToPage(
                page = if (pagerState.currentPage != 2) pagerState.currentPage + 1 else 0,
                pageOffset = 0.01f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier.fillMaxWidth(),
        ) {
            page -> AdCard(list = developerPlayLists[page])
        }
    }
}

// 用户歌单横向滚动列表
@Composable
fun RecommendedUserList(type: ListType) {
    val composableScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<PlayLists?>(null) }

    // 获取最热用户歌单
    LaunchedEffect(Unit) {
        composableScope.launch {
            if (type == ListType.Top) {
                RPC.get<PlayLists?>("/Top/playlist?limit=10").fold(
                    success = { result = it },
                    failure = { /* ... */ }
                )
            } else if (type == ListType.HighQuality) {
                RPC.get<PlayLists?>("/Top/playlist/HighQuality?limit=10").fold(
                    success = { result = it },
                    failure = { /* ... */ }
                )
            }
        }
    }

    LazyRow(modifier = Modifier.fillMaxWidth()) {
        item {
            result?.playlists?.map { list -> PopularListCard(list = list) }
        }
    }
}

// 推荐歌单卡片
@Composable
fun PopularListCard(list: Playlist) {
    Column(
        modifier = Modifier
            .padding(end = 10.dp)
            .width(220.dp)
            .clickable {
                navController.navigate("/playlist/${list.id}")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = list.coverImgUrl,
            contentDescription = "歌单封面图片",
            modifier = Modifier
                .padding(vertical = 10.dp)
                .size(220.dp)
                .clip(shape = MaterialTheme.shapes.medium)
        )
        Text(text = list.name, maxLines = 2, modifier = Modifier.padding(start = 10.dp))
    }
}

