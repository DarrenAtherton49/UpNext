package com.atherton.upnext.data.local

interface AppSettings {

    fun getDiscoverViewSetting(): DiscoverViewToggleSetting
    fun toggleDiscoverViewSetting()

    sealed class DiscoverViewToggleSetting {
        object Grid : DiscoverViewToggleSetting()
        object Carousel : DiscoverViewToggleSetting()
    }
}
