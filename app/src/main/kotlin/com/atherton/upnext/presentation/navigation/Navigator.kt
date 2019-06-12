package com.atherton.upnext.presentation.navigation

interface Navigator {

    fun showSearchScreen()
    fun showMovieDetailScreen(movieId: Long)
    fun showTvShowDetailScreen(tvShowId: Long)
    fun showPersonDetailScreen(personId: Long)
    fun playYoutubeVideo(videoKey: String)
    fun showSettingsScreen()
    fun showLicensesScreen()
    fun showUrlInBrowser(url: String)
}
