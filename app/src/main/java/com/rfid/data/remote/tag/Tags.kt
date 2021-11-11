package com.rfid.data.remote.tag

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Tags(
    val data: MutableList<Tag>
)

@Parcelize
data class Tag(
    var tag: String,
    val serial: String,
    val type: String
) : Parcelable
