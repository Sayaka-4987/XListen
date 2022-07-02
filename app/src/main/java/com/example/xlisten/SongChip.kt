package com.example.xlisten

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.xlisten.json.Song

@Composable
fun SongChip(song: Song) {
    Row(
        modifier = Modifier
            .width(LocalConfiguration.current.screenWidthDp.dp)
            .height(80.dp)
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .clickable {
                    player.startMusic(song.id)
                }
        ) {
            // 歌名
            Text(
                text = song.name,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            // 艺术家和专辑名
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = song.artist.joinToString(
                        separator = "/",
                        transform = {ar -> ar.name}
                    ) + " · " + song.album.name,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1
                )
            }
        }
        if (song.mv != 0) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play_video),
                contentDescription = "播放MV视频",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { navController.navigate("/video/${song.mv}") }
            )
        }
    }
}
