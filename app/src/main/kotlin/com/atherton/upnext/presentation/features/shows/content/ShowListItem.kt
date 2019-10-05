package com.atherton.upnext.presentation.features.shows.content

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShowListItem(
    @DrawableRes val addToListButtonResId: Int,
    val genresString: String?,
    val showId: Long,
    val posterPath: String?,
    val releaseDate: String?,
    val runtime: String?,
    val title: String?,
    val voteAverage: String?,
    @DrawableRes val watchedButtonResId: Int,
    @DrawableRes val watchlistButtonResId: Int
) : Parcelable
