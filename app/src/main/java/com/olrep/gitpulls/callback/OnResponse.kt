package com.olrep.gitpulls.callback

interface OnResponse<T> {
    fun onSuccess(result: T?)
    fun onError(error: Throwable)
}