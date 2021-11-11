package com.rfid.data.remote.shipment

import com.rfid.data.remote.tag.ResponseDetect
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ShipmentApi {
    // 출하등록
    @POST("shipment")
    fun postShipment(@Body shipment: Shipment): Single<ResponseDetect>

    // 반입등록
    @POST("inbound")
    fun postInbound(@Body inbound: Inbound): Single<ResponseDetect>
}