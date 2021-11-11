package com.rfid.data.remote.tag

data class Covering(
    val result: String,
    val thickness1: Int,
    val thickness2: Int,
    val images: MutableList<Img>
)

data class PostCovering(
    var tag: String,
    var result: String,
    var thickness1: Int,
    var thickness2: Int,
    var images: MutableList<String>,
    var deleted_images: MutableList<Int>
)