package com.rfid.data.local.user

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class UserDataSourceImpl(
    private val userDao: UserDao
) : UserDataSource {
    override fun getUser(): Single<UserEntity> {
        return userDao
            .getUser()
            .subscribeOn(Schedulers.io())
    }
    override fun saveUser(userEntity: UserEntity): Completable {
        return userDao
            .saveUser(userEntity)
            .subscribeOn(Schedulers.io())
    }
    override fun deleteUser(): Completable {
        return userDao
            .deleteUser()
            .subscribeOn(Schedulers.io())
    }
    override fun getToken(): Single<String> {
        return userDao
            .getToken()
            .subscribeOn(Schedulers.io())
    }
    override fun updateToken(token: String): Completable {
        return userDao
            .updateToken(token)
            .subscribeOn(Schedulers.io())
    }
}