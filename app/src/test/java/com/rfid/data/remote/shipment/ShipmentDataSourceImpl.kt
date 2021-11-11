package com.rfid.data.remote.shipment

import com.rfid.data.remote.tag.ResponseDetect
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ShipmentDataSourceImpl (
    private val shipmentApi: ShipmentApi
) : ShipmentDataSource {
    override fun postShipment(shipment: Shipment): Single<ResponseDetect> {
        return shipmentApi
            .postShipment(shipment)
            .subscribeOn(Schedulers.io())
    }

    override fun postInbound(inbound: Inbound): Single<ResponseDetect> {
        return shipmentApi
            .postInbound(inbound)
            .subscribeOn(Schedulers.io())
    }
}