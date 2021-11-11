package com.rfid.data.repository.shipment

import com.rfid.data.remote.shipment.Inbound
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.tag.ResponseDetect
import com.rfid.data.remote.tag.Tags
import io.reactivex.rxjava3.core.Single

interface ShipmentRepository {
    fun postShipment(shipment: Shipment): Single<ResponseDetect>
    fun postInbound(inbound: Inbound): Single<ResponseDetect>
}