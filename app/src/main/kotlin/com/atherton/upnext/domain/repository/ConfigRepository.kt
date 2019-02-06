package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import io.reactivex.Single

interface ConfigRepository {

    fun getConfig(): Single<Response<Config>>
}