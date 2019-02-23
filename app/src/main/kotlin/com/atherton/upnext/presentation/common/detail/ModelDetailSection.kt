package com.atherton.upnext.presentation.common.detail

import android.os.Parcelable
import com.atherton.upnext.domain.model.Genre
import kotlinx.android.parcel.Parcelize

sealed class ModelDetailSection(val viewType: Int, val hasScrollingChildAdapter: Boolean) : Parcelable {
    @Parcelize data class RuntimeRelease(
        val runtime: String?,
        val releaseDate: String?,
        val showDivider: Boolean
    ) : ModelDetailSection(RUNTIME_RELEASE, false)
    @Parcelize data class Overview(val overview: String) : ModelDetailSection(OVERVIEW, false)
    @Parcelize data class Genres(val genres: List<Genre>) : ModelDetailSection(GENRES, false)
    @Parcelize data class Ratings(val ratings: List<String>) : ModelDetailSection(RATINGS, false)
    @Parcelize data class Seasons(val seasons: List<String>) : ModelDetailSection(SEASONS, false)
    @Parcelize data class Cast(val cast: List<String>) : ModelDetailSection(CAST, true)
    @Parcelize data class Crew(val crew: List<String>) : ModelDetailSection(CREW, true)
    @Parcelize data class Trailers(val trailers: List<String>) : ModelDetailSection(TRAILERS, true)
    @Parcelize data class Photos(val photos: List<String>) : ModelDetailSection(PHOTOS, true)
    @Parcelize data class Reviews(val reviews: List<String>) : ModelDetailSection(REVIEWS, false)
    @Parcelize data class Comments(val comments: List<String>) : ModelDetailSection(COMMENTS, false)
    @Parcelize data class SimilarItems(val similarItems: List<String>) : ModelDetailSection(SIMILAR_ITEMS, true)
    @Parcelize data class ExternalLinks(val links: List<String>) : ModelDetailSection(EXTERNAL_LINKS, false)

    companion object ViewType {
        const val RUNTIME_RELEASE = 0
        const val OVERVIEW = 1
        const val GENRES = 2
        const val RATINGS = 3
        const val SEASONS = 4
        const val CAST = 5
        const val CREW = 6
        const val TRAILERS = 7
        const val PHOTOS = 8
        const val REVIEWS = 9
        const val COMMENTS = 10
        const val SIMILAR_ITEMS = 11
        const val EXTERNAL_LINKS = 12
    }
}
