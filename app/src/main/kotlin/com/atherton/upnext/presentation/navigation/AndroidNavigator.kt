package com.atherton.upnext.presentation.navigation

import androidx.navigation.NavController
import com.atherton.upnext.R

class AndroidNavigator(private val navController: NavController) : Navigator {

    override fun showSearchScreen() {
        navController.navigate(R.id.actionSharedGoToSearch)
    }
}
