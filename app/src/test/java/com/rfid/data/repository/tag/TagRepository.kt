package com.rfid.data.repository.tag

import com.rfid.data.remote.shipment.Inbound
import com.rfid.data.remote.shipment.Shipment
import com.rfid.data.remote.tag.*
import io.reactivex.rxjava3.core.Single

interface TagRepository {
    fun scanTag(tags: MutableList<String>): Single<Tags>
    fun requestStandard(requestStandard: RequestStandard): Single<Standard>

    fun getMold(tag: String): Single<Mold>
    fun postMold(postMold: PostMold): Single<ResponseDetect>

    fun getCovering(tag: String): Single<Covering>
    fun postCovering(postCovering: PostCovering): Single<ResponseDetect>

    fun getIronware(tag: String): Single<Ironware>
    fun postIronware(postIronware: PostIronware): Single<ResponseDetect>

    fun getConcrete(tag: String): Single<Concrete>
    fun postConcrete(postConcrete: PostConcrete): Single<ResponseDetect>

    fun getUnMolding(tag: String): Single<UnMolding>
    fun postUnMolding(postUnMolding: PostUnMolding): Single<ResponseDetect>

    fun getExterior(tag: String): Single<Exterior>
    fun postExterior(postExterior: PostExterior): Single<ResponseDetect>
}