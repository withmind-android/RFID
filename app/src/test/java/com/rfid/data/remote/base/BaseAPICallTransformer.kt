package com.rfid.data.remote.base

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleTransformer


fun Completable.transformCompletableToSingleDefault(): Single<BaseAPICallResult<Unit>> =
    toSingleDefault(Unit)
        .compose(wrappingSingleAPIResultData())

fun <T> Single<T>.wrappingAPICallResult(): Single<BaseAPICallResult<T>>
        = compose(wrappingSingleAPIResultData())

fun <T> wrappingSingleAPIResultData() = SingleTransformer<T, BaseAPICallResult<T>> { single ->
    single
        .map { data -> BaseAPICallResult(result = data) }
        .onErrorReturn { BaseAPICallResult(throwable = it) }
}