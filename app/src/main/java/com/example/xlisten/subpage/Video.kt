package com.example.xlisten.subpage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavBackStackEntry
import com.example.xlisten.Player
import com.example.xlisten.RPC
import com.example.xlisten.json.VideoData
import com.example.xlisten.player
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.launch

// 播放视频
@Composable
fun Video(backStackEntry: NavBackStackEntry) {
    val id = backStackEntry.arguments?.getInt("mv_id")
    val composableScope = rememberCoroutineScope()
    val shouldContinue = player.state == Player.State.Playing

    val context = LocalContext.current
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        if (shouldContinue) {
            player.pause()
        }

        composableScope.launch {
            RPC.get<VideoData>("mv/url?id=$id").fold(
                success = {
                    exoPlayer.setMediaItem(MediaItem.fromUri(it.data.url))
                    exoPlayer.prepare()
                },
                failure = { Log.d("getVideoData", it.toString()) }
            )
        }

        onDispose {
            exoPlayer.release()
            if (shouldContinue) {
                player.play()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView({
            StyledPlayerView(it).apply { player = exoPlayer }
        })
    }
}