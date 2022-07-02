package com.example.xlisten.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FavDB(context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DB_NAME, factory, DB_VER) {
    companion object {
        private const val DB_NAME = "fav"
        private const val DB_VER = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table fav (" +
                "id integer primary key not null," +
                "value text not null," +
                "time datetime default current_timestamp not null" +
            ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("drop table if exists $DB_NAME")
        onCreate(db)
    }

    fun addRaw(id: Int, value: String) = this
        .writableDatabase
        .insert(
            DB_NAME,
            null,
            ContentValues().apply {
                put("id", id)
                put("value", value)
            }
        )

    fun del(id: Int) = this
        .writableDatabase
        .delete(DB_NAME, "id=?", arrayOf(id.toString()))

    @SuppressLint("Recycle")
    fun has(id: Int) : Boolean {
        val cursor = this
            .readableDatabase
            .rawQuery("select * from $DB_NAME where id=$id", null)
        val result = cursor.moveToFirst()
        cursor.close()
        return result
    }

    @SuppressLint("Recycle", "Range")
    fun allRaw() : List<String> {
        val items = ArrayList<String>()
        this.readableDatabase
            .rawQuery("select value from $DB_NAME order by time desc", null)
            .apply {
                if (moveToFirst()) {
                    do {
                        items.add(getString(getColumnIndex("value")))
                    } while (moveToNext())
                }
                close()
            }
        return items
    }
}