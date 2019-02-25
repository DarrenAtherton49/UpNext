package com.atherton.upnext.presentation.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.atherton.upnext.presentation.features.movies.detail.MovieDetailFragmentDirections


class AndroidNavigator(private val navController: NavController, val context: Context) : Navigator {

    override fun showSearchScreen() {
        navController.navigate(com.atherton.upnext.R.id.actionSharedGoToSearch)
    }

    override fun showMovieDetailScreen(movieId: Int) {
        val action = MovieDetailFragmentDirections.actionSharedGoToMovieDetail(movieId)
        navController.navigate(action)
    }

    override fun showTvShowDetailScreen(tvShowId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showPersonDetailScreen(personId: Int) {
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
}
