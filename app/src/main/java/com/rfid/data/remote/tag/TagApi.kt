package com.rfid.data.remote.tag

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface TagApi {
    // tag 조회
    @POST("tag")
    fun requestTags(@Body requestTag: RequestTag): Single<Tags>

    // standard
    @POST("tag_info")
    fun requestStandard(@Body requestStandard: RequestStandard): Single<Standard>

    // 몰드
    @GET("mold/{tag}")
    fun getMold(@Path("tag") tag: String): Single<Mold>

    @POST("mold")
    fun postMold(@Body postMold: PostMold): Single<ResponseDetect>

    // 피복두께
    @GET("covering/{tag}")
    fun getCovering(@Path("tag") tag: String): Single<Covering>

    @POST("covering")
    fun postCovering(@Body postCovering: PostCovering): Single<ResponseDetect>

    // 매입철물
    @GET("ironware/{tag}")
    fun getIronware(@Path("tag") tag: String): Single<Ironware>

    @POST("ironware")
    fun postIronware(@Body postIronware: PostIronware): Single<ResponseDetect>

    // 콘크리트
    @GET("concreat/{tag}")
    fun getConcrete(@Path("tag") tag: String): Single<Concrete>

    @POST("concreat")
    fun postConcrete(@Body postConcrete: PostConcrete): Single<ResponseDetect>

    // 탈형강도
    @GET("unmolding/{tag}")
    fun getUnMolding(@Path("tag") tag: String): Single<UnMolding>

    @POST("unmolding")
    fun postUnMolding(@Body postUnMolding: PostUnMolding): Single<ResponseDetect>

    // 외관검사
    @GET("exterior/{tag}")
    fun getExterior(@Path("tag") tag: String): Single<Exterior>

    @POST("exterior")
    fun postExterior(@Body postExterior: PostExterior): Single<ResponseDetect>
}