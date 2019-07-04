package com.atherton.upnext.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import com.atherton.upnext.presentation.common.addtolists.AddToListsContentType
import com.atherton.upnext.presentation.common.addtolists.AddToListsDialogFragment
import com.atherton.upnext.presentation.features.content.ContentDetailContentType
import com.atherton.upnext.presentation.features.content.ContentDetailFragmentDirections

class AndroidNavigator(private val navController: NavController, val context: Context) : Navigator {

    override fun showSearchScreen() {
        navController.navigate(com.atherton.upnext.R.id.actionSharedGoToSearch)
    }

    override fun showMovieDetailScreen(movieId: Long) {
        val action = ContentDetailFragmentDirections.actionSharedGoToContentDetail(
            contentId = movieId,
            contentType = ContentDetailContentType.Movie
        )
        navController.navigate(action)
    }

    override fun showTvShowDetailScreen(tvShowId: Long) {
        val action = ContentDetailFragmentDirections.actionSharedGoToContentDetail(
            contentId = tvShowId,
            contentType = ContentDetailContentType.TvShow
        )
        navController.navigate(action)
    }

    override fun showPersonDetailScreen(personId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // fall back on web intent if Youtube app is not installed
    override fun playYoutubeVideo(videoKey: String) {
        try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoKey"))
            context.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$videoKey"))
            context.startActivity(webIntent)
        }
    }

    override fun showSettingsScreen() {
        navController.navigate(com.atherton.upnext.R.id.actionSharedGoToSettings)
    }

    override fun showLicensesScreen() {
        navController.navigate(com.atherton.upnext.R.id.actionSharedGoToLicenses)
    }

    override fun showUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    override fun showAddToListsMenu(
        contentId: Long,
        contentType: AddToListsContentType,
        childFragmentManager: FragmentManager
    ) {
        val fragment = AddToListsDialogFragment.newInstance(contentId = contentId, contentType = contentType)
        fragment.show(childFragmentManager, AddToListsDialogFragment.FRAGMENT_TAG)
    }
}
