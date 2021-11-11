package com.rfid.data.remote.project

data class Manufactures(
    val data: MutableList<Manufacture>
)

data class Manufacture(
    val id: String,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}