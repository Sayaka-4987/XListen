package com.example.xlisten

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.xlisten.database.Cache
import com.example.xlisten.database.Favourite
import com.example.xlisten.database.cache
import com.example.xlisten.database.favourite
import com.example.xlisten.subpage.AlbumScreen
import com.example.xlisten.subpage.ArtistScreen
import com.example.xlisten.subpage.PlayList
import com.example.xlisten.subpage.Video
import com.example.xlisten.ui.theme.XListenTheme

@SuppressLint("StaticFieldLeak")
lateinit var navController: NavHostController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 初始化播放器
            player = Player(LocalContext.current)
            // 初始化收藏夹
            favourite = Favourite(LocalContext.current)
            // 初始化缓存
            cache = Cache(LocalContext.current, null)
            // 改变状态栏颜色为黑色
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            XListenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    App()
                }
            }
        }
    }

    override fun onDestroy() {
        favourite.close()
        super.onDestroy()
    }
}

@Composable
fun App() {
    navController = rememberNavController()
    NavHost(navController = navController, startDestination = "/") {
        // 主页4个栏目
        composable("/") { Navigation() }
        // 播放器
        composable("/player") { MusicPlayer(player) }
        // 歌手介绍
        composable("/artist/{artist_id}",
            arguments = listOf(navArgument("artist_id") {
                type = NavType.IntType
            })) { backStackEntry -> ArtistScreen(backStackEntry)
        }
        // 专辑介绍
        composable("/album/{album_id}",
            arguments = listOf(navArgument("album_id") {
                type = NavType.IntType
            })) { backStackEntry -> AlbumScreen(backStackEntry)
        }
        // 视频播放
        composable("/video/{mv_id}",
            arguments = listOf(navArgument("mv_id") {
                type = NavType.IntType
            })) { backStackEntry -> Video(backStackEntry)
        }
        // 歌单详细信息
        composable("/playlist/{list_id}",
            arguments = listOf(navArgument("list_id") {
                type = NavType.LongType
            })) { backStackEntry -> PlayList(backStackEntry)
        }
    }
}

object Nav {
    const val HOME = 0
    const val HOT = 1
    const val SEARCH = 2
    const val FAVOURITE = 3
    var current = HOME
    val list = listOf(
        Pair("主页", Icons.Filled.Home),
        Pair("热榜", Icons.Filled.ThumbUp),
        Pair("搜索", Icons.Filled.Search),
        Pair("收藏", Icons.Filled.Favorite),
    )
}

@Composable
fun Navigation() {
    var selectedItem by remember { mutableStateOf(Nav.current) }

    // bottomBar 槽
    Scaffold(
        bottomBar = {
            Column {
                MiniMusicPlayer(player = player)
                BottomNavigation {
                    Nav.list.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            label = { Text(item.first) },
                            icon = { Icon(item.second, contentDescription = "主页图标") },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                Nav.current = index
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            when (selectedItem) {
                Nav.HOME -> HomeScreen()
                Nav.HOT -> HotScreen()
                Nav.SEARCH -> SearchScreen()
                Nav.FAVOURITE -> FavouriteScreen()
            }
        }
    }
}
