package com.atherton.upnext.util.extensions

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.preventMultipleClicks(): Observable<T> {
    return this.throttleFirst(300, TimeUnit.MILLISECONDS)
}
