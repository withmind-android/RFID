package com.rfid.ui.shipment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.tag.ResponseDetect
import com.rfid.data.repository.shipment.ShipmentRepository
import com.rfid.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class ShipmentViewModel(
    private val repository: ShipmentRepository
) : BaseViewModel() {
    private val _response = MutableLiveData<ResponseDetect>()
    val response: LiveData<ResponseDetect> = _response

    init {

    }

    fun postShipment(shipment: Shipment) {
        compositeDisposable.add(
            repository
                .postShipment(shipment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }
}