package com.rfid.data.remote.tag

import com.google.gson.annotations.SerializedName

data class RequestTag(
    @SerializedName("project_id")
    val projectId: String,
    @SerializedName("manufacture_id")
    val manufactureId: String,
    val tags: List<String>
)