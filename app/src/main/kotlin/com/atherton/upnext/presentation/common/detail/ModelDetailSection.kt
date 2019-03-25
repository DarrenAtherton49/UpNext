package com.atherton.upnext.presentation.common.detail

import android.os.Parcelable
import com.atherton.upnext.domain.model.*
import kotlinx.android.parcel.Parcelize

sealed class ModelDetailSection(val viewType: Int, val hasScrollingChildAdapter: Boolean) : Parcelable {

    @Parcelize data class InfoPanel(
        val releaseDate: String?,
        val runtime: String?,
        val voteAverage: String?,
        val showFirstDivider: Boolean,
        val showSecondDivider: Boolean
    ) : ModelDetailSection(RUNTIME_RELEASE, false)

    @Parcelize data class Overview(val overview: String) : ModelDetailSection(OVERVIEW, false)
    @Parcelize data class Genres(val genres: List<Genre>) : ModelDetailSection(GENRES, false)
    @Parcelize data class Ratings(val ratings: List<String>) : ModelDetailSection(RATINGS, false)
    @Parcelize data class Seasons(
        val sectionTitle: String,
        val seasons: List<Season>
    ) : ModelDetailSection(SEASONS, false)

    @Parcelize data class Cast(
        val sectionTitle: String,
        val cast: List<CastMember>
    ) : ModelDetailSection(CAST, true)

    @Parcelize data class Crew(
        val sectionTitle: String,
        val crew: List<CrewMember>
    ) : ModelDetailSection(CREW, true)

    @Parcelize data class Videos(
        val sectionTitle: String,
        val videos: List<Video>
    ) : ModelDetailSection(VIDEOS, true)

    @Parcelize data class Photos(val photos: List<String>) : ModelDetailSection(PHOTOS, true)
    @Parcelize data class Reviews(val reviews: List<String>) : ModelDetailSection(REVIEWS, false)
    @Parcelize data class Comments(val comments: List<String>) : ModelDetailSection(COMMENTS, false)

    @Parcelize data class RecommendedItems(
        val sectionTitle: String,
        val recommendedItems: List<Watchable>
    ) : ModelDetailSection(RECOMMENDED_ITEMS, true)

    @Parcelize data class ExternalLinks(val links: List<String>) : ModelDetailSection(EXTERNAL_LINKS, false)

    companion object ViewType {
        const val RUNTIME_RELEASE = 0
        const val OVERVIEW = 1
        const val GENRES = 2
        const val RATINGS = 3
        const val SEASONS = 4
        const val CAST = 5
        const val CREW = 6
        const val VIDEOS = 7
        const val PHOTOS = 8
        const val REVIEWS = 9
        const val COMMENTS = 10
        const val RECOMMENDED_ITEMS = 11
        const val EXTERNAL_LINKS = 12
    }
}
