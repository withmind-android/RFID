package com.rfid.data.remote.project

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface ProjectApi {
    // 프로젝트 조회
    @POST("project")
    fun requestProjects(@Body userId: UserId): Single<Projects>

    // 제조사 조회
    @GET("manufacture")
    fun requestManufactures(): Single<Manufactures>
}