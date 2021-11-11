package com.rfid.adapter.item

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubjectItem(
    var field: String = "",
    var value: Int = 0,
    var standard: Int = 0,
    var unit: String = ""
) : Parcelable
