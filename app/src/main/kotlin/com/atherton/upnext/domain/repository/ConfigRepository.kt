package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Config
import io.reactivex.Observable

interface ConfigRepository {

    fun getConfig(): Observable<Config>
}