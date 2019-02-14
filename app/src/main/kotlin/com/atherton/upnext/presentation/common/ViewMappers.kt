package com.atherton.upnext.presentation.common

import com.atherton.upnext.domain.model.*

// generate image urls for the discover and search results screens using the base url, size and path
internal fun List<SearchModel>.withDiscoverSearchImageUrls(config: Config): List<SearchModel> {

    //todo write function to generate path based on device screen size?
    fun buildPosterPath(posterPath: String?, config: Config): String? =
        posterPath?.let { "${config.secureBaseUrl}${config.posterSizes[2]}$posterPath" }

    //todo write function to generate path based on device screen size?
    fun buildProfilePath(profilePath: String?, config: Config): String? =
        profilePath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$profilePath" }

    //todo write function to generate path based on device screen size?
    fun buildBackdropPath(backdropPath: String?, config: Config): String? =
        backdropPath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$backdropPath" }

    return this.map {
        when (it) {
            is TvShow -> {
                it.copy(
                    backdropPath = buildBackdropPath(it.backdropPath, config),
                    posterPath = buildPosterPath(it.posterPath, config)
                )
            }
            is Movie -> {
                it.copy(
                    backdropPath = buildBackdropPath(it.backdropPath, config),
                    posterPath = buildPosterPath(it.posterPath, config)
                )
            }
            is Person -> it.copy(profilePath = buildProfilePath(it.profilePath, config))
        }
    }
}
