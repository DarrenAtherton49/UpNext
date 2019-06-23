package com.atherton.upnext.presentation.features.movies.content

import android.os.Parcelable
import com.atherton.upnext.domain.model.Genre
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieListItem(
    val genres: List<Genre>?,
    val movieId: Long,
    val posterPath: String?,
    val runtime: String?,
    val titleAndReleaseDate: String?,
    val voteAverage: String?
    //val watchlistButtonText: String
) : Parcelable
