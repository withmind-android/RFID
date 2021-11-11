package com.rfid.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserEntity")
data class UserEntity(
    val userId: String,
    val userPw: String,
    val token: String,
    val isAuto: Boolean
) {
    @PrimaryKey
    var id: Int = 0
}