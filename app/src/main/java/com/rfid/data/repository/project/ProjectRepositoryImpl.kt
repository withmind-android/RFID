package com.rfid.data.repository.project

import com.rfid.data.local.user.UserDataSource
import com.rfid.data.remote.login.LoginDataSource
import com.rfid.data.remote.project.Manufactures
import com.rfid.data.remote.project.ProjectDataSource
import com.rfid.data.remote.project.Projects
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Single

class ProjectRepositoryImpl constructor(
    private val userDataSource: UserDataSource,
    private val projectDataSource: ProjectDataSource,
    private val loginDataSource: LoginDataSource
) : ProjectRepository {

    override fun getProjects(): Single<Projects> {
        return userDataSource
            .getUser()
            .flatMap { userEntity ->
                projectDataSource
                    .requestProjects(userEntity.userId)
            }
            .retryWhen { error ->
                return@retryWhen error
                    .flatMapSingle {
                        return@flatMapSingle userDataSource
                            .getUser()
                            .flatMap { userEntity ->
                                loginDataSource
                                    .login(userEntity.userId, userEntity.userPw)
                            }
                            .flatMap { token ->
                                val newToken = token.headers()["x-auth-token"].toString()
                                SharedPreferencesPackage.setToken(newToken)
                                userDataSource
                                    .updateToken(newToken)
                                    .andThen(Single.just(Unit))
                            }
                    }
            }

    }

    override fun getManufactures(): Single<Manufactures> {
        return projectDataSource
            .requestManufactures()
            .retryWhen { error ->
                return@retryWhen error
                    .flatMapSingle {
                        return@flatMapSingle userDataSource
                            .getUser()
                            .flatMap { userEntity ->
                                loginDataSource
                                    .login(userEntity.userId, userEntity.userPw)
                            }
                            .flatMap { token ->
                                val newToken = token.headers()["x-auth-token"].toString()
                                SharedPreferencesPackage.setToken(newToken)
                                userDataSource
                                    .updateToken(newToken)
                                    .andThen(Single.just(Unit))
                            }
                    }
            }
    }
}