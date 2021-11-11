package com.rfid.data.repository.login

import com.rfid.data.local.user.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface LoginRepository {
    fun login(id: String, password: String, isAuto: Boolean): Completable
    fun getUser(): Single<UserEntity>
}