package com.rfid.data.remote.base

class BaseAPICallResult<T>(
    val result: T? = null,
    val throwable: Throwable? = null
)