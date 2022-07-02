package com.example.xlisten

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.xlisten.database.favourite
import com.google.android.exoplayer2.R
import kotlinx.coroutines.launch
import java.util.ArrayDeque

@Composable
fun FavouriteScreen() {
    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxSize()
    ) {
        FavouriteHeading()
        Spacer(modifier = Modifier.height(20.dp))
        CollectionList()
    }
}

// 收藏界面标题提示语
@Composable
fun FavouriteHeading() {
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .clip(shape = MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colors.surface)
                .size(128.dp)
                .align(Alignment.CenterVertically)) {
                Icon(
                    painter = painterResource(id = com.example.xlisten.R.drawable.ic_favorite),
                    contentDescription = "收藏图标提示",
                    modifier = Modifier.size(96.dp).align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = "My favourite",
                    style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(10.dp))
                // 收藏的所有歌曲送入播放列表
                Button(
                    onClick = {
                        favourite.all().map { it.id }.toCollection(ArrayDeque()).let { player.setPlaylist(it) }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.exo_controls_play),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.onSurface.copy(0.6f),
                        contentDescription = "全部播放"
                    )
                    Text(text = " 播放全部")
                }
            }
        }
    }
}

// 收藏夹中的歌曲展示
@Composable
fun CollectionList() {
    val coroutineScope = rememberCoroutineScope()
    var fav by remember { mutableStateOf(favourite.all()) }

    LazyColumn {
        items(fav) {
            val state = rememberScrollState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .horizontalScroll(state),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SongChip(it)

                IconButton(onClick = {
                    favourite.del(it.id)
                    fav = favourite.all()
                    coroutineScope.launch { state.scrollTo(0) }
                }) {
                    Icon(
                        painter = painterResource(com.example.xlisten.R.drawable.ic_baseline_delete_forever_24),
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = "删除按钮",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(width = 36.dp, height = 30.dp)
                    )
                }
            }
        }
    }
}