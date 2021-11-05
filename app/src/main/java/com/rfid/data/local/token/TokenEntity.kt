package com.rfid.data.local.token

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class TokenEntity(
    val userId: String,
    val accessToken: String,
) {
    @PrimaryKey
    var id: Int = 0
}