package com.atherton.upnext.presentation.navigation

import androidx.navigation.NavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.features.movies.detail.MovieDetailFragmentDirections

class AndroidNavigator(private val navController: NavController) : Navigator {

    override fun showSearchScreen() {
        navController.navigate(R.id.actionSharedGoToSearch)
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
}
