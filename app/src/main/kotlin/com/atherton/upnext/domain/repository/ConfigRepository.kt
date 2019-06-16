package com.atherton.upnext.domain.repository

import com.atherton.upnext.domain.model.Config

interface ConfigRepository {

    fun getConfig(): Config
    fun refreshConfig()
}
