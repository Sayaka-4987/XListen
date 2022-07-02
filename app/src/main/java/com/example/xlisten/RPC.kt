package com.example.xlisten

import com.example.xlisten.database.Cache
import com.example.xlisten.database.cache
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object RPC {
    const val HOST = "https://netease-cloud-music-api-nine-sepia.vercel.app/"

    val Json = Json { ignoreUnknownKeys = true }

    // Example:
    // --------
    // data class Search(...)
    //
    // @Composable
    // fun Foo() {
    //     val composableScope = rememberCoroutineScope()
    //
    //     var result by remember { mutableStateOf<Search?>(null) }
    //     val keywords = "November Rain"
    //
    //     // 用于加载组件后获取数据
    //     LaunchedEffect(Unit) {
    //         composableScope.launch {
    //             RPC.get<Search>("search?keywords=$keywords").fold(
    //                 success = { result = it },
    //                 failure = { /* ... */ }
    //             )
    //         }
    //     }
    //
    //     // 用于点击按钮后获取数据
    //     Button(onClick = {
    //         composableScope.launch {
    //             RPC.get<Search>("search?keywords=$keywords").fold(
    //                 success = { result = it },
    //                 failure = { /* ... */ }
    //             )
    //         }
    //     }) { /* ... */}
    // }

    suspend inline fun <reified T> get(uri: String) : Result<T, FuelError> {
        val (value, status) = cache.get(uri)
        if (status == Cache.Status.Ok) {
            return Result.of { Json.decodeFromString(value) }
        }
        val (_, _, result) = Fuel
            .get("$HOST$uri&realIP=115.183.147.29")
            .awaitStringResponseResult()
        return result.map {
            when (status) {
                Cache.Status.NotFound -> cache.add(uri, it)
                Cache.Status.Expired -> cache.update(uri, it)
                else -> {}
            }
            Json.decodeFromString(it)
        }
    }
}