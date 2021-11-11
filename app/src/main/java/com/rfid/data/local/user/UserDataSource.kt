package com.rfid.data.local.user

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface UserDataSource {
    fun getUser(): Single<UserEntity>
    fun saveUser(userEntity : UserEntity): Completable
    fun deleteUser(): Completable
    fun getToken(): Single<String>
    fun updateToken(token: String): Completable
}