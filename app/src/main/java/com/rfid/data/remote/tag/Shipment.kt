package com.rfid.data.remote.tag

data class Shipment(
    val tags: MutableList<String>,
    val images: MutableList<String>
)