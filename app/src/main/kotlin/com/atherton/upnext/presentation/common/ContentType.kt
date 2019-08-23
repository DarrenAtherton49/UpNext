package com.atherton.upnext.presentation.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class ContentType : Parcelable {
    @Parcelize object TvShow : ContentType()
    @Parcelize object Movie : ContentType()
}
