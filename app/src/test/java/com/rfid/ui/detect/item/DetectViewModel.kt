package com.rfid.ui.detect.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rfid.data.remote.tag.*
import com.rfid.data.repository.tag.TagRepository
import com.rfid.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class DetectViewModel(
    private val repository: TagRepository,
    private val requestStandard: RequestStandard
) : BaseViewModel() {
    private val _standard = MutableLiveData<Standard>()
    val standard: LiveData<Standard> = _standard

    private val _mold = MutableLiveData<Mold>()
    val mold: LiveData<Mold> = _mold
    private val _cover = MutableLiveData<Covering>()
    val cover: LiveData<Covering> = _cover
    private val _ironware = MutableLiveData<Ironware>()
    val ironware: LiveData<Ironware> = _ironware
    private val _concrete = MutableLiveData<Concrete>()
    val concrete: LiveData<Concrete> = _concrete
    private val _unMolding = MutableLiveData<UnMolding>()
    val unMolding: LiveData<UnMolding> = _unMolding
    private val _exterior = MutableLiveData<Exterior>()
    val exterior: LiveData<Exterior> = _exterior

    private val _response = MutableLiveData<ResponseDetect>()
    val response: LiveData<ResponseDetect> = _response

    init {
        compositeDisposable.add(
            repository
                .requestStandard(requestStandard)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _standard.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 몰드검사 get, post
    fun getMold(tag: String) {
        compositeDisposable.add(
            repository
                .getMold(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mold ->
                    _mold.value = mold
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postMold(postMold: PostMold) {
        compositeDisposable.add(
            repository
                .postMold(postMold)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 피복두께 get, post
    fun getCovering(tag: String) {
        compositeDisposable.add(
            repository
                .getCovering(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ covering ->
                    _cover.value = covering
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postCovering(postCovering: PostCovering) {
        compositeDisposable.add(
            repository
                .postCovering(postCovering)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 매입철물
    fun getIronware(tag: String) {
        compositeDisposable.add(
            repository
                .getIronware(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ ironware ->
                    _ironware.value = ironware
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postIronware(postIronware: PostIronware) {
        compositeDisposable.add(
            repository
                .postIronware(postIronware)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 콘크리트
    fun getConcrete(tag: String) {
        compositeDisposable.add(
            repository
                .getConcrete(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ concrete ->
                    _concrete.value = concrete
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postConcrete(postConcrete: PostConcrete) {
        compositeDisposable.add(
            repository
                .postConcrete(postConcrete)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 탈형강도
    fun getUnMolding(tag: String) {
        compositeDisposable.add(
            repository
                .getUnMolding(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ unMolding ->
                    _unMolding.value = unMolding
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postUnMolding(postUnMolding: PostUnMolding) {
        compositeDisposable.add(
            repository
                .postUnMolding(postUnMolding)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    // 외관검사
    fun getExterior(tag: String) {
        compositeDisposable.add(
            repository
                .getExterior(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ exterior ->
                    _exterior.value = exterior
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun postExterior(postExterior: PostExterior) {
        compositeDisposable.add(
            repository
                .postExterior(postExterior)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }
}