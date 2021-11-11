package com.rfid.adapter.item

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File


@Parcelize
data class PictureItem(
    var id: Int?,
    var img: String,
    var file: File?,
    var isVisibleDeleteBtn: Boolean
) : Parcelable
