package com.rfid.data.remote.tag

data class Concrete(
    val result: String,
    val slump: Int,
    val air: Int,
    val chloride: Int,
    val slump_standard: Int,
    val air_standard: Int,
    val chloride_standard: Int,
    val images: MutableList<Img>
)

data class PostConcrete(
    var tag: String,
    var result: String,
    var slump: Int,
    var air: Int,
    var chloride: Int,
    var slump_standard: Int,
    var air_standard: Int,
    var chloride_standard: Int,
    var images: MutableList<String>,
    var deleted_images: MutableList<Int>
)