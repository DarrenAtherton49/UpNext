package com.atherton.upnext.data.cache

import io.reactivex.Single

class Cacher<T> {

    //todo implement caching, fallback, stale/filtering logic
    fun buildSingle(networkRequest: () -> Single<T>): Single<T> {
        return networkRequest.invoke()
    }
}