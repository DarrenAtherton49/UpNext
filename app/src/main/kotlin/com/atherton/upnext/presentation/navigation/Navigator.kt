package com.atherton.upnext.presentation.navigation

import androidx.fragment.app.FragmentManager
import com.atherton.upnext.presentation.common.ContentType

interface Navigator {

    fun showSearchScreen()
    fun showMovieDetailScreen(movieId: Long)
    fun showTvShowDetailScreen(tvShowId: Long)
    fun showPersonDetailScreen(personId: Long)
    fun playYoutubeVideo(videoKey: String)
    fun showSettingsScreen()
    fun showLicensesScreen()
    fun showUrlInBrowser(url: String)
    fun showAddToListsMenu(contentId: Long, contentType: ContentType, childFragmentManager: FragmentManager)
}
