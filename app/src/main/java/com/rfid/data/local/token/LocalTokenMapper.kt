package com.rfid.data.local.token

import com.rfid.data.remote.login.Token

object LocalTokenMapper {
    fun mappingRemoteDataToLocal(token: Token): TokenEntity {
        return TokenEntity(
            accessToken = token.access,
            userId = "userid"
        )
    }
}