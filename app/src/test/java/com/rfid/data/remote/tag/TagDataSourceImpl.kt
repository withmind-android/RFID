package com.rfid.data.remote.tag

import com.rfid.data.remote.shipment.Inbound
import com.rfid.data.remote.shipment.Shipment
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TagDataSourceImpl (private val tagApi: TagApi) : TagDataSource {
    override fun requestTags(requestTag: RequestTag): Single<Tags> {
        return tagApi
            .requestTags(requestTag)
            .subscribeOn(Schedulers.io())
    }

    override fun requestStandard(requestStandard: RequestStandard): Single<Standard> {
        return tagApi
            .requestStandard(requestStandard)
            .subscribeOn(Schedulers.io())
    }

    override fun getMold(tag: String): Single<Mold> {
        return tagApi
            .getMold(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postMold(postMold: PostMold): Single<ResponseDetect> {
        return tagApi
            .postMold(postMold)
            .subscribeOn(Schedulers.io())
    }

    override fun getCovering(tag: String): Single<Covering> {
        return tagApi
            .getCovering(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postCovering(postCovering: PostCovering): Single<ResponseDetect> {
        return tagApi
            .postCovering(postCovering)
            .subscribeOn(Schedulers.io())
    }

    override fun getIronware(tag: String): Single<Ironware> {
        return tagApi
            .getIronware(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postIronware(postIronware: PostIronware): Single<ResponseDetect> {
        return tagApi
            .postIronware(postIronware)
            .subscribeOn(Schedulers.io())
    }

    override fun getConcrete(tag: String): Single<Concrete> {
        return tagApi
            .getConcrete(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postConcrete(postConcrete: PostConcrete): Single<ResponseDetect> {
        return tagApi
            .postConcrete(postConcrete)
            .subscribeOn(Schedulers.io())
    }

    override fun getUnMolding(tag: String): Single<UnMolding> {
        return tagApi
            .getUnMolding(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postUnMolding(postUnMolding: PostUnMolding): Single<ResponseDetect> {
        return tagApi
            .postUnMolding(postUnMolding)
            .subscribeOn(Schedulers.io())
    }

    override fun getExterior(tag: String): Single<Exterior> {
        return tagApi
            .getExterior(tag)
            .subscribeOn(Schedulers.io())
    }

    override fun postExterior(postExterior: PostExterior): Single<ResponseDetect> {
        return tagApi
            .postExterior(postExterior)
            .subscribeOn(Schedulers.io())
    }
}