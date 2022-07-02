package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val code: Int,
    val `data`: List<Data>
) {
    @Serializable
    data class Data(
        val br: Int,
        val code: Int,
        val encodeType: String,
        val fee: Int,
        val freeTrialInfo: FreeTrialInfo?,
        val id: Int,
        val level: String,
        val md5: String,
        val size: Int,
        val type: String,
        val url: String,
    ) {
        @Serializable
        data class FreeTrialInfo(
            val end: Int,
            val start: Int
        )
    }
}