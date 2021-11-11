package com.rfid.data.remote.shipment

import com.rfid.data.remote.tag.ResponseDetect
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body

interface ShipmentDataSource {
    fun postShipment(@Body shipment: Shipment): Single<ResponseDetect>
    fun postInbound(@Body inbound: Inbound): Single<ResponseDetect>
}