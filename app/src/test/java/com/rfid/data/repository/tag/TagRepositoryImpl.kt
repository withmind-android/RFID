package com.rfid.data.repository.tag

import com.rfid.data.local.user.UserDataSource
import com.rfid.data.remote.login.LoginDataSource
import com.rfid.data.remote.shipment.Inbound
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.tag.*
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Single

class TagRepositoryImpl constructor(
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource,
    private val tagDataSource: TagDataSource
) : TagRepository {
    override fun scanTag(tags: MutableList<String>): Single<Tags> {
        return tagDataSource
            .requestTags(
                RequestTag(
                    SharedPreferencesPackage.getProject().id,
                    SharedPreferencesPackage.getManufacture().id,
                    tags
                )
            )
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

    override fun requestStandard(requestStandard: RequestStandard): Single<Standard> {
        return tagDataSource
            .requestStandard(requestStandard)
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

    override fun getMold(tag: String): Single<Mold> {
        return tagDataSource
            .getMold(tag)
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

    override fun postMold(postMold: PostMold): Single<ResponseDetect> {
        return tagDataSource
            .postMold(postMold)
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

    override fun getCovering(tag: String): Single<Covering> {
        return tagDataSource
            .getCovering(tag)
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

    override fun postCovering(postCovering: PostCovering): Single<ResponseDetect> {
        return tagDataSource
            .postCovering(postCovering)
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

    override fun getIronware(tag: String): Single<Ironware> {
        return tagDataSource
            .getIronware(tag)
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

    override fun postIronware(postIronware: PostIronware): Single<ResponseDetect> {
        return tagDataSource
            .postIronware(postIronware)
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

    override fun getConcrete(tag: String): Single<Concrete> {
        return tagDataSource
            .getConcrete(tag)
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

    override fun postConcrete(postConcrete: PostConcrete): Single<ResponseDetect> {
        return tagDataSource
            .postConcrete(postConcrete)
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

    override fun getUnMolding(tag: String): Single<UnMolding> {
        return tagDataSource
            .getUnMolding(tag)
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

    override fun postUnMolding(postUnMolding: PostUnMolding): Single<ResponseDetect> {
        return tagDataSource
            .postUnMolding(postUnMolding)
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

    override fun getExterior(tag: String): Single<Exterior> {
        return tagDataSource
            .getExterior(tag)
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

    override fun postExterior(postExterior: PostExterior): Single<ResponseDetect> {
        return tagDataSource
            .postExterior(postExterior)
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