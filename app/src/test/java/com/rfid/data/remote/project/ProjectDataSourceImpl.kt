package com.rfid.data.remote.project

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ProjectDataSourceImpl (
    private val projectApi: ProjectApi
) : ProjectDataSource {
    override fun requestProjects(userId: String): Single<Projects> {
        return projectApi
            .requestProjects(UserId(userId))
            .subscribeOn(Schedulers.io())
    }

    override fun requestManufactures(): Single<Manufactures> {
        return projectApi
            .requestManufactures()
            .subscribeOn(Schedulers.io())
    }

}