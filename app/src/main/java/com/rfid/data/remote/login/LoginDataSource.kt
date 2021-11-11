package com.rfid.data.remote.login

import com.rfid.data.model.BaseDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Header

interface LoginDataSource {
    fun login(id: String, password: String): Single<retrofit2.Response<BaseDto>>
    fun requestValidate(token: String): Single<BaseDto>
}