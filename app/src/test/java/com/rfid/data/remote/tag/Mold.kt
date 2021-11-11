package com.rfid.data.remote.tag

data class Mold(
    var result: String,
    var width: Int,
    var height: Int,
    var length: Int,
    var images: MutableList<Img>
)

data class PostMold(
    var tag: String,
    var result: String,
    var width: Int,
    var height: Int,
    var length: Int,
    var images: MutableList<String>,
    var deleted_images: MutableList<Int>
)