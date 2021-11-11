package com.rfid.ui.rfiddemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rfid.data.remote.tag.Tags
import com.rfid.data.repository.tag.TagRepository
import com.rfid.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class RFIDDemoViewModel (
    private val repository: TagRepository
) : BaseViewModel() {

    private val _tags = MutableLiveData<Tags>()
    val tags: LiveData<Tags> = _tags
//    private val _manufactures = MutableLiveData<Manufactures>()
//    val manufactures: LiveData<Manufactures> = _manufactures

    init {

    }

    fun scanTag(tags: MutableList<String>) {
        compositeDisposable.add(
            repository
                .scanTag(tags)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _tags.value = it
                },{
                    it.printStackTrace()
                })
        )
    }
}