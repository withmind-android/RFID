package com.rfid.data.local.token

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface TokenDataSource {
    fun getTokens(): Single<TokenEntity>
    fun saveTokens(tokenEntity: TokenEntity): Completable
    fun clearCache(): Completable
    fun getAccessToken(): Single<String>
}