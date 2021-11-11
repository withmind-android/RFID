package com.rfid.data.remote.login

import com.rfid.data.model.BaseDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface LoginApi {
    // 로그인
    @POST("login")
    fun login(@Body login: LoginInfo): Single<retrofit2.Response<BaseDto>>

    // 토큰검증
    @POST("validate_token")
    fun requestValidate(@Header("x-auth-token") token: String): Single<BaseDto>
}