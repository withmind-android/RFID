package com.rfid.data.local.token

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TokenDataSourceImpl(
    private val tokenDao: TokenDao
) : TokenDataSource {
    override fun getTokens(): Single<TokenEntity> {
        return tokenDao
            .getToken()
            .subscribeOn(Schedulers.io())
    }

    override fun saveTokens(tokenEntity: TokenEntity): Completable {
        return tokenDao
            .saveToken(tokenEntity)
            .subscribeOn(Schedulers.io())
    }

    override fun clearCache(): Completable {
        return tokenDao
            .clearCache()
            .subscribeOn(Schedulers.io())
    }

    override fun getAccessToken(): Single<String> {
        return tokenDao
            .getAccessToken()
            .subscribeOn(Schedulers.io())
    }
}