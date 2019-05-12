package com.atherton.upnext.data.local

interface AppSettings {

    fun getGridViewModeSetting(): GridViewModeSetting
    fun toggleGridViewModeSetting()

    sealed class GridViewModeSetting {
        object Grid : GridViewModeSetting()
        object List : GridViewModeSetting()
    }
}
