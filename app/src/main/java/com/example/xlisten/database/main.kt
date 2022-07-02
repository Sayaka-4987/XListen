package com.example.xlisten.database

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.xlisten.json.Song
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

lateinit var favourite: Favourite
lateinit var cache: Cache

class Favourite(context: Context) : ViewModel() {
    private val db = FavDB(context, null)
    private var cache: List<Song>? = null
    private fun update() = db.allRaw().map<String, Song> { Json.decodeFromString(it) }.also { cache = it }

    fun all() = cache ?: update()
    fun add(item: Song) = db.addRaw(item.id, Json.encodeToString(item)).also { update() }
    fun del(id: Int) = db.del(id).also { update() }
    fun has(id: Int) = db.has(id)

    fun close() = db.close()
}
