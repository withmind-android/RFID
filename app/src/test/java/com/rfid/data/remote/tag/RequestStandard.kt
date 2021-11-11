package com.rfid.data.remote.tag

data class RequestStandard(
    val project_id: String,
    val manufacture_id: String,
    val tag: String
)