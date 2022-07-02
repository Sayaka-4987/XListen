package com.example.xlisten

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.xlisten.database.favourite
import com.example.xlisten.json.PlayerDetail
import com.example.xlisten.json.PlayerData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

// 有三个方法可供调用：
//
// - 立即播放一首歌
// player.startMusic(歌曲ID)
//
// - 加入到播放列表
// player.addToPlaylist(歌曲ID)
//
// - 重置播放列表并播放其第一首
// player.setPlaylist(播放列表)
//
// 其中，播放列表为ArrayDeque或LinkedList

lateinit var player : Player

class Player(context: Context) : ViewModel() {
    enum class State {
        Empty,
        Preparing,
        Playing,
        Pausing,
    }

    enum class Mode {
        ListCycle,
        SingleCycle,
        ListSingle,
    }

    var exoPlayer by mutableStateOf(
        ExoPlayer.Builder(context).build().apply {
            addListener(Listener())
            prepare()
        }
    )

    var state by mutableStateOf(State.Empty)
    var mode by mutableStateOf(Mode.ListCycle)
    var playerDetail by mutableStateOf<PlayerDetail?>(null)

    private var history : Deque<Int> = ArrayDeque()
    private var playlist : Deque<Int> = ArrayDeque()

    fun startMusic(id: Int) {
        playlist.addFirst(id)
        start(id)
    }

    fun addToPlaylist(id: Int) {
        playlist.addLast(id)
        if (state == State.Empty) {
            start(playlist.first)
        }
    }

    fun setPlaylist(ids: Deque<Int>) {
        stop()
        history = ArrayDeque()
        playlist = ids
        mode = Mode.ListCycle
        playlist.peekFirst()?.let { start(it) }
    }

    private fun start(id: Int) {
        state = State.Preparing

        viewModelScope.launch {
            var loadedSong = false
            var loadedDetail = false

            RPC.get<PlayerData>("song/url?id=$id").fold(
                success = {
                    exoPlayer.setMediaItem(MediaItem.fromUri(it.data[0].url))
                    exoPlayer.prepare()
                    loadedSong = true
                    if (loadedSong && loadedDetail && state == State.Preparing) {
                        exoPlayer.play()
                        state = State.Playing
                    }
                },
                failure = { Log.d("getPlayerData", it.toString()) }
            )

            RPC.get<PlayerDetail>("song/detail?ids=$id").fold(
                success = {
                    playerDetail = it
                    loadedDetail = true
                    if (loadedSong && loadedDetail && state == State.Preparing) {
                        exoPlayer.play()
                        state = State.Playing
                    }
                },
                failure = { Log.d("getPlayerDetail", it.toString()) }
            )
        }
    }

    fun pause() {
        exoPlayer.pause()
        state = State.Pausing
    }

    fun play() {
        exoPlayer.play()
        state = State.Playing
    }

    fun stop() {
        exoPlayer.stop()
        state = State.Empty
        playerDetail = null
    }

    fun hasNext() = playlist.size > 1
    fun hasBack() = history.isNotEmpty()

    fun next(mode: Player.Mode = this.mode) {
        val music = playlist.pollFirst()!!
        when (mode) {
            Mode.ListCycle -> {
                history.add(music)
                playlist.peekFirst()?.let { start(it) } ?: run {
                    playlist = history
                    history = ArrayDeque()
                    playlist.peekFirst()?.let { start(it) }
                }
            }
            Mode.ListSingle -> {
                history.add(music)
                playlist.peekFirst()?.let { start(it) } ?: run { stop() }
            }
            Mode.SingleCycle -> {
                startMusic(music)
            }
        }
    }

    fun back() = startMusic(history.pollLast()!!)

    inner class Listener : com.google.android.exoplayer2.Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                com.google.android.exoplayer2.Player.STATE_ENDED -> {
                    if (playlist != null && playlist.isNotEmpty()) {
                        next()
                    }
                }
            }
        }
    }
}

// 底端栏的迷你播放器
@Composable
fun MiniMusicPlayer(player: Player) {
    if (player.state == Player.State.Playing || player.state == Player.State.Pausing) {
        Button(onClick = { navController.navigate("/player") }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "正在播放：${player.playerDetail?.songs?.get(0)?.name}",
                    modifier = Modifier.padding(10.dp, 0.dp),
                    maxLines = 1,
                    overflow = Ellipsis
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    when (player.state) {
                        // 根据播放状态决定展示播放/暂停按钮
                        Player.State.Playing -> {
                            IconButton(onClick = { player.pause() }) {
                                Icon(
                                    painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_icon_pause),
                                    contentDescription = "暂停按钮",
                                )
                            }
                        }
                        Player.State.Pausing -> {
                            IconButton(onClick = { player.play() }) {
                                Icon(
                                    painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_controls_play),
                                    contentDescription = "播放按钮",
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

// 完整界面的播放器
@Composable
fun MusicPlayer(player: Player) {
    val composableScope = rememberCoroutineScope()
    var position by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        composableScope.launch {
            while (true) {
                delay(100)
                if (player.state == Player.State.Playing) {
                    position = player.exoPlayer.currentPosition
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (player.playerDetail != null) {
            val detail = player.playerDetail!!.songs[0]
            // 歌名
            Text(
                text = detail.name,
                modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp),
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = Ellipsis
            )
            // 艺术家和专辑名
            Row {
                for (ar in detail.ar) {
                    // 用 "/" 分割多个艺术家
                    Text(
                        text = if (ar != detail.ar.last()) ar.name + "/" else ar.name,
                        color = MaterialTheme.colors.onBackground.copy(0.6f),
                        maxLines = 1,
                        overflow = Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .clickable { navController.navigate("/artist/${ar.id}") }
                    )
                }
            }

            AsyncImage(
                model = detail.al.picUrl,
                contentDescription = "专辑封面图片",
                modifier = Modifier
                    .padding(vertical = 80.dp)
                    .fillMaxWidth(1f)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .clickable { navController.navigate("/album/${detail.al.id}") }
            )

            // 播放控制按钮
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = position.toFloat(),
                        onValueChange = {
                            position = it.toLong()
                            player.exoPlayer.seekTo(it.toLong())
                        },
                        valueRange = 0f..player.exoPlayer.contentDuration.toFloat()
                    )
                    Row(
                        Modifier.fillMaxWidth()
                    ) {
                        Time(position / 1000)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Time(player.exoPlayer.contentDuration / 1000)
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        // 当前播放列表的播放方式
                        when (player.mode) {
                            Player.Mode.ListCycle -> {
                                IconButton(onClick = { player.mode = Player.Mode.SingleCycle }) {
                                    Icon(
                                        painter = painterResource(
                                            id = com.google.android.exoplayer2.ui.R.drawable.exo_controls_repeat_all
                                        ),
                                        contentDescription = "列表循环",
                                    )
                                }
                            }
                            Player.Mode.SingleCycle -> {
                                IconButton(onClick = { player.mode = Player.Mode.ListSingle }) {
                                    Icon(
                                        painter = painterResource(
                                            id = com.google.android.exoplayer2.ui.R.drawable.exo_controls_repeat_one
                                        ),
                                        contentDescription = "单曲循环",
                                    )
                                }
                            }
                            Player.Mode.ListSingle -> {
                                IconButton(onClick = { player.mode = Player.Mode.ListCycle }) {
                                    Icon(
                                        painter = painterResource(
                                            id = R.drawable.ic_single_list
                                        ),
                                        contentDescription = "顺序播放",
                                    )
                                }
                            }
                        }

                        // 播放控制按钮组
                        IconButton(onClick = { if (player.hasBack()) { player.back() } } ) {
                            Icon(
                                painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_ic_skip_previous),
                                contentDescription = "上一首按钮",
                            )
                        }
                        when(player.state) {
                            Player.State.Playing -> {
                                IconButton(onClick = { player.pause() }) {
                                    Icon(
                                        painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_icon_pause),
                                        contentDescription = "暂停按钮",
                                    )
                                }
                            }
                            Player.State.Pausing -> {
                                IconButton(onClick = { player.play() }) {
                                    Icon(
                                        painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_controls_play),
                                        contentDescription = "播放按钮",
                                    )
                                }
                            }
                            Player.State.Preparing -> {
                                IconButton(onClick = { /* */ }) {
                                    Icon(
                                        painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_controls_play),
                                        contentDescription = "播放按钮",
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { player.next(Player.Mode.ListCycle) } ) {
                            Icon(
                                painter = painterResource(id = com.google.android.exoplayer2.ui.R.drawable.exo_ic_skip_next),
                                contentDescription = "下一首按钮",
                            )
                        }

                        var fav by remember { mutableStateOf(favourite.has(detail.id)) }

                        if (fav) {
                            IconButton(onClick = {
                                favourite.del(detail.id)
                                fav = false
                            } ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_favorite_border_24),
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = "取消收藏"
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                favourite.add(detail.toSong())
                                fav = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_favorite_border_24),
                                    tint = MaterialTheme.colors.onPrimary,
                                    contentDescription = "添加到收藏列表"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Time(t: Long) = Text(if (t < 3600) {
        fill(t / 60) + ":" + fill(t % 60)
    } else {
        fill(t / 3600) + ":" + fill((t % 3600) / 60) + ":" + fill(t % 60)
    },
    style = MaterialTheme.typography.overline,
    color = MaterialTheme.colors.onBackground.copy(0.6f)
)

fun fill(l: Long) : String {
    val s = l.toString()
    return if (s.length < 2) { "0$s" } else { s }
}