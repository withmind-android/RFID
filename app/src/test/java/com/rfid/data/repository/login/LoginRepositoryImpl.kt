package com.rfid.data.repository.login

import android.util.Log
import com.rfid.data.local.user.UserDataSource
import com.rfid.data.local.user.UserEntity
import com.rfid.data.remote.login.LoginDataSource
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class LoginRepositoryImpl constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource
) : LoginRepository {

    override fun login(id: String, password: String, isAuto: Boolean): Completable {
        return loginDataSource
            .login(id, password)
            .flatMapCompletable {
                val token = it.headers()["x-auth-token"].toString()
                Log.e(TAG, "login: $token")
                if (token == "null") {
                    return@flatMapCompletable error("")
                } else {
                    SharedPreferencesPackage.setToken(token)
                    userDataSource
                        .deleteUser()
                        .andThen(
                            userDataSource.saveUser(
                                UserEntity(id, password, token, isAuto)
                            )
                        )
                }
            }
    }

    override fun getUser(): Single<UserEntity> {
        return userDataSource
            .getUser()
    }

    companion object {
        private const val TAG = "LoginRepositoryImpl"
    }
}