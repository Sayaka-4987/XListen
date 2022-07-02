package com.example.xlisten

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.xlisten.json.Album
import com.example.xlisten.json.Artist
import com.example.xlisten.json.SearchResult
import com.example.xlisten.json.Song
import kotlinx.coroutines.launch

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxSize(),
    ) {
        Text(text = "搜索歌曲", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        // 搜索输入框
        SearchBar()
    }
}

// 搜索文本框
@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("")}
    val composableScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(true)}
    var res by remember{ mutableStateOf<SearchResult?>(null)}

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField (
            value = searchText,
            onValueChange = {searchText = it},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { state = true },
            enabled = state,
            label = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                    contentDescription = "搜索图标"
                )
            },
            placeholder = { Text("请输入歌曲名称") },
            trailingIcon = {
                if (searchText.isNotEmpty()){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_clear_24),
                        contentDescription = "清空搜索框输入",
                        modifier = Modifier.clickable { searchText = ""; state = false}
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                composableScope.launch {
                    RPC.get<SearchResult>("search?keywords=$searchText").fold(
                        success = { res = it },
                        failure = { /* ... */ }
                    )
                }
            }),
            singleLine = true,
            shape = CircleShape
        )
        res?.let { SearchInfo(searchResult = it) }
    }
}

@Composable
fun SearchInfo (searchResult: SearchResult) {
    LazyColumn {
        itemsIndexed(searchResult.result.songs) { _, item ->
            SongChip(Song(
                item.id,
                item.name,
                Album(
                    item.album.id,
                    item.album.name
                ),
                item.artists.map { Artist(it.id, it.name) },
                item.fee,
                item.mvid
            ))
        }
    }
}