package com.rfid.data.remote

import android.util.Log
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDao
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.login.LoginApi
import com.rfid.data.remote.project.ProjectApi
import com.rfid.data.remote.shipment.ShipmentApi
import com.rfid.data.remote.tag.TagApi
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 서버 주소
    private const val TEST_URL = "https://run.mocky.io/v3/6ba57aa7-7f07-4c64-a2fa-74023f4c2811/"
    private const val BASE_URL = "http://roit.smartosc.co.kr:8889/"

    private val okHttpClient: OkHttpClient by lazy {
        return@lazy OkHttpClient
            .Builder()
            .addInterceptor(Interceptor.invoke {
                val originalRequest = it.request()
//                val single = DatabaseClient.userDao().getToken()
//                val singleString = UserDataSourceImpl(DatabaseClient.userDao()).getToken()
//                val s = arrayOfNulls<String>(1)
//                singleString.subscribe({ result ->
//                    s[0] = result
//                    Log.e("TAG", "singleString set: ${s[0]}")
//                }, {})
//                Log.e("TAG", "singleString get: ${s[0]}")
                val newRequest = originalRequest.newBuilder()
                    .addHeader("x-auth-token", SharedPreferencesPackage.getToken())
//                    .addHeader("x-auth-token", s[0].toString())
                    .build()
                it.proceed(newRequest)
            })
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val buildRetrofitClient: Retrofit by lazy {
        return@lazy Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    val loginAPI: LoginApi by lazy {
        return@lazy buildRetrofitClient.create(LoginApi::class.java)
    }
    val projectAPI: ProjectApi by lazy {
        return@lazy buildRetrofitClient.create(ProjectApi::class.java)
    }
    val tagAPI: TagApi by lazy {
        return@lazy buildRetrofitClient.create(TagApi::class.java)
    }

    val shipmentAPI: ShipmentApi by lazy {
        return@lazy buildRetrofitClient.create(ShipmentApi::class.java)
    }
}