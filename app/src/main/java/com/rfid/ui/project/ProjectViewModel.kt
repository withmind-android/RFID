package com.rfid.ui.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rfid.data.remote.project.Manufactures
import com.rfid.data.remote.project.Project
import com.rfid.data.remote.project.Projects
import com.rfid.data.repository.project.ProjectRepository
import com.rfid.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class ProjectViewModel(
    private val repository: ProjectRepository
) : BaseViewModel() {

    private val _projects = MutableLiveData<Projects>()
    val projects: LiveData<Projects> = _projects
    private val _manufactures = MutableLiveData<Manufactures>()
    val manufactures: LiveData<Manufactures> = _manufactures

    init {
        compositeDisposable.add(
            repository
                .getProjects()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _projects.value = it
                }, {
                    it.printStackTrace()
                })
        )
        compositeDisposable.add(
            repository
                .getManufactures()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _manufactures.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }
}