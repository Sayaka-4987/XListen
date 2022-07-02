package com.example.xlisten.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Cache(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DB_NAME, factory, DB_VER) {
    companion object {
        private const val DB_NAME = "cache"
        private const val DB_VER = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table cache (" +
                    "url text primary key not null," +
                    "value text not null," +
                    "time bigint not null" +
            ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("drop table if exists $DB_NAME")
        onCreate(db)
    }

    fun add(url: String, value: String) = this
        .writableDatabase
        .insert(
            DB_NAME,
            null,
            ContentValues().apply {
                put("url", url)
                put("value", value)
                put("time", System.currentTimeMillis())
            }
        )

    fun update(url: String, value: String) = this
        .writableDatabase
        .update(
            DB_NAME,
            ContentValues().apply {
                put("value", value)
                put("time", System.currentTimeMillis())
            },
            "url=?",
            arrayOf(url)
        )

    enum class Status {
        Ok,
        NotFound,
        Expired
    }

    @SuppressLint("Recycle", "Range")
    fun get(url: String) : Pair<String, Status> {
        val cursor = this
            .readableDatabase
            .rawQuery("select * from $DB_NAME where url='$url'", null)
        val first = cursor.moveToFirst()
        return if (first) {
            val value = cursor.getString(cursor.getColumnIndex("value"))
            val time = cursor.getLong(cursor.getColumnIndex("time"))
            cursor.close()
            if (System.currentTimeMillis() - time > 4 * 60 * 60 * 1000) {
                Pair("", Status.Expired)
            } else {
                Pair(value, Status.Ok)
            }
        } else {
            cursor.close()
            Pair("", Status.NotFound)
        }
    }
}