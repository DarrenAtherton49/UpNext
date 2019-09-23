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
    fun showTvShowsScreen(initialListId: Long?)
    fun showMoviesScreen(initialListId: Long?)
    fun showAddToListsMenu(contentId: Long, contentType: ContentType, fragmentManager: FragmentManager)
    fun showNewListScreen(contentId: Long, contentType: ContentType, fragmentManager: FragmentManager)
}
