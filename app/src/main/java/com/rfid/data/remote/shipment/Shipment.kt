package com.rfid.data.remote.shipment

data class Shipment(
    val tags: MutableList<String>,
    val images: MutableList<String>
)