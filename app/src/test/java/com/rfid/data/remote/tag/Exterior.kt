package com.rfid.data.remote.tag

data class Exterior(
    val result: String,
    val width: Int,
    val height: Int,
    val length: Int,
    val images: MutableList<Img>
)

data class PostExterior(
    var tag: String,
    var result: String,
    var width: Int,
    var height: Int,
    var length: Int,
    var images: MutableList<String>,
    var deleted_images: MutableList<Int>
)