package com.atherton.upnext.presentation.navigation

interface Navigator {

    fun showSearchScreen()
    fun showMovieDetailScreen(movieId: Int)
    fun showTvShowDetailScreen(tvShowId: Int)
    fun showPersonDetailScreen(personId: Int)
    fun showSettingsScreen()
    fun playYoutubeVideo(videoKey: String)
}
