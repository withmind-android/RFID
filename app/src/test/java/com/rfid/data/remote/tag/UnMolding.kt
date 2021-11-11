package com.rfid.data.remote.tag

data class UnMolding(
    val result: String,
    val strength: Int,
    val images: MutableList<Img>
)

data class PostUnMolding(
    var tag: String,
    var result: String,
    var strength: Int,
    var images: MutableList<String>,
    var deleted_images: MutableList<Int>
)