package com.rfid.data.remote.base

interface BaseCallback<T> {
    fun onSuccess(data: T)
    fun onFail(throwable: Throwable)
}