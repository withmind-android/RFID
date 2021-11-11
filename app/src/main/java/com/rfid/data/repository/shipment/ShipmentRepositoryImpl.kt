package com.rfid.data.repository.shipment

import com.rfid.data.local.user.UserDataSource
import com.rfid.data.remote.login.LoginDataSource
import com.rfid.data.remote.shipment.Inbound
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.shipment.ShipmentDataSource
import com.rfid.data.remote.tag.RequestTag
import com.rfid.data.remote.tag.ResponseDetect
import com.rfid.data.remote.tag.TagDataSource
import com.rfid.data.remote.tag.Tags
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Single

class ShipmentRepositoryImpl (
    private val userDataSource: UserDataSource,
    private val loginDataSource: LoginDataSource,
    private val shipmentDataSource: ShipmentDataSource
) : ShipmentRepository {
    override fun postShipment(shipment: Shipment): Single<ResponseDetect> {
        return shipmentDataSource
            .postShipment(shipment)
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

    override fun postInbound(inbound: Inbound): Single<ResponseDetect> {
        return shipmentDataSource
            .postInbound(inbound)
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