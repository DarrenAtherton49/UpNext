package com.atherton.upnext.presentation.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.navigation.AndroidNavigator
import com.atherton.upnext.presentation.navigation.Navigator
import com.atherton.upnext.util.base.BaseActivity
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity<MainAction, MainState, MainViewEffect, MainViewModel>() {

    override val layoutResId: Int = R.layout.activity_main
    override val stateBundleKey: String = "bundle_key_main_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getViewModel<MainViewModel>(vmFactory) }
    val navController: NavController by lazy { findNavController(R.id.navHostFragment) }
    private val navigator: Navigator by lazy { AndroidNavigator(navController, this) }

    private val topLevelDestinationIds = setOf(R.id.moviesFragment, R.id.showsFragment, R.id.discoverFragment)
    val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinationIds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNavigation()
    }

    override fun renderState(state: MainState) {}

    override fun processViewEffects(viewEffect: MainViewEffect) {
        when (viewEffect) {
            is MainViewEffect.Navigation.ShowSearchScreen -> navigator.showSearchScreen()
            is MainViewEffect.Navigation.ShowMovieDetailScreen -> navigator.showMovieDetailScreen(viewEffect.movieId)
            is MainViewEffect.Navigation.ShowTvDetailScreen -> navigator.showTvShowDetailScreen(viewEffect.tvShowId)
            is MainViewEffect.Navigation.ShowPersonDetailScreen -> navigator.showPersonDetailScreen(viewEffect.personId)
            is MainViewEffect.Navigation.PlayYoutubeVideo -> navigator.playYoutubeVideo(viewEffect.videoKey)
            is MainViewEffect.Navigation.ShowSettingsScreen -> navigator.showSettingsScreen()
        }
    }

    private fun setupNavigation() {
        // bottom navigation
        bottomNavigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun initInjection(initialState: MainState?) {
        DaggerMainComponent.builder()
            .mainModule(MainModule(initialState))
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
