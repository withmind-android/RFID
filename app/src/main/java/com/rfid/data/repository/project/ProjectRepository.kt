package com.rfid.data.repository.project

import com.rfid.data.remote.project.Manufactures
import com.rfid.data.remote.project.Projects
import io.reactivex.rxjava3.core.Single

interface ProjectRepository {
    fun getProjects(): Single<Projects>
    fun getManufactures(): Single<Manufactures>
}