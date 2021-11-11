package com.rfid.data.remote.project

import io.reactivex.rxjava3.core.Single
import retrofit2.http.Query

interface ProjectDataSource {
    fun requestProjects(userId: String): Single<Projects>
    fun requestManufactures(): Single<Manufactures>
}