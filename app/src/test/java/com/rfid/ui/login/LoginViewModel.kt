package com.rfid.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rfid.data.local.user.UserEntity
import com.rfid.data.remote.base.transformCompletableToSingleDefault
import com.rfid.data.repository.login.LoginRepository
import com.rfid.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class LoginViewModel(
    private val repository: LoginRepository
) : BaseViewModel() {
    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> = _loginState

    private val _userInfo = MutableLiveData<UserEntity>()
    val userInfo: LiveData<UserEntity> = _userInfo

    init {
        compositeDisposable.add(
            repository
                .getUser()
                .doOnSubscribe { showProgress() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { hideProgress() }
                .subscribe({
                    _userInfo.value = it
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun startLogin(id: String, password: String, isAuto: Boolean) {
        compositeDisposable.add(
            repository
                .login(id, password, isAuto)
                .doOnSubscribe { showProgress() }
                .transformCompletableToSingleDefault()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { hideProgress() }
                .subscribe({
                    _loginState.value = it.throwable == null
                    Log.e(TAG, "result: ${it.result}")
                    Log.e(TAG, "throwable: ${it.throwable}")
                }, {
                    Log.e(TAG, "startLogin: fail")
                    _loginState.value = false
                    it.printStackTrace()
                })
        )
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}