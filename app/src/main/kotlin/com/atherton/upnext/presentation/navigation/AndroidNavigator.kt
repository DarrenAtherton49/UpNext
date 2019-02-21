package com.atherton.upnext.presentation.navigation

import androidx.navigation.NavController
import com.atherton.upnext.R

class AndroidNavigator(private val navController: NavController) : Navigator {

    override fun showSearchScreen() {
        navController.navigate(R.id.actionSharedGoToSearch)
    }

    override fun showMovieDetailScreen() {
        navController.navigate(R.id.actionSharedGoToMovieDetail)
    }

    override fun showTvShowDetailScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showPersonDetailScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
