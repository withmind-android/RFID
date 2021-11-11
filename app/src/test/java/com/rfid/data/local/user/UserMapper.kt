package com.rfid.data.local.user

import com.rfid.data.remote.login.Token

object UserMapper {
    fun mappingRemoteDataToLocal(userId: String, userPw: String, token: String, isAuto: Boolean): UserEntity {
        return UserEntity(userId, userPw, token, isAuto)
    }
}