package com.atherton.upnext.presentation.features.movies.content

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieListItem(
    val genresString: String?,
    val movieId: Long,
    val posterPath: String?,
    val releaseDate: String?,
    val runtime: String?,
    val title: String?,
    val voteAverage: String?
    //val watchlistButtonText: String
) : Parcelable
