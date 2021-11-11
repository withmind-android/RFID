package com.rfid.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterItem(
    val id: String,
    val num: String,
    val kind: String
) : Parcelable
