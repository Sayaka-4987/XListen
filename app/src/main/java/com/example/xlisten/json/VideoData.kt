package com.example.xlisten.json

import kotlinx.serialization.Serializable

@Serializable
data class VideoData(
    val code: Int,
    val `data`: Data
) {
    @Serializable
    data class Data(
        val code: Int,
        val expi: Int,
        val fee: Int,
        val id: Int,
        val md5: String,
        val msg: String,
        val mvFee: Int,
        val r: Int,
        val size: Int,
        val st: Int,
        val url: String
    )
}