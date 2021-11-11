package com.rfid.data.remote.login

import com.rfid.data.model.BaseDto
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginDataSourceImpl (
    private val loginApi: LoginApi
) : LoginDataSource {
    override fun login(id: String, password: String): Single<retrofit2.Response<BaseDto>> {
        return loginApi
            .login(LoginInfo(id, password))
            .subscribeOn(Schedulers.io())
    }

    override fun requestValidate(token: String): Single<BaseDto> {
        return loginApi
            .requestValidate(token)
            .subscribeOn(Schedulers.io())
    }
}