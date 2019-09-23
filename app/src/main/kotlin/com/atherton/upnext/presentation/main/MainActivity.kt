package com.atherton.upnext.presentation.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.base.BaseActivity
import com.atherton.upnext.presentation.navigation.AndroidNavigator
import com.atherton.upnext.presentation.navigation.Navigator
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import com.atherton.upnext.util.extension.showLongSnackbar
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
    internal val navigator: Navigator by lazy { AndroidNavigator(navController, this) }

    private val topLevelDestinationIds = setOf(R.id.moviesFragment, R.id.showsFragment, R.id.discoverFragment)
    val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinationIds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNavigation()
        addBottomBarVisibilityListener()
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
            is MainViewEffect.Navigation.Settings.ShowLicensesScreen -> navigator.showLicensesScreen()
            is MainViewEffect.Navigation.ShowLicenseInBrowser -> navigator.showUrlInBrowser(viewEffect.url)
            is MainViewEffect.Navigation.ShowTvShowsScreen -> navigator.showTvShowsScreen(viewEffect.initialListId)
            is MainViewEffect.Navigation.ShowMoviesScreen -> navigator.showMoviesScreen(viewEffect.initialListId)
            is MainViewEffect.Message.ShowTvShowListCreatedMessage -> {
                mainCoordinatorLayout.showLongSnackbar(
                    text = viewEffect.message,
                    actionText = getString(R.string.new_list_created_action),
                    onClick = {
                        sharedViewModel.dispatch(MainAction.SeeTvShowListClicked(viewEffect.listId))
                    }
                )
            }
            is MainViewEffect.Message.ShowMovieListCreatedMessage -> {
                mainCoordinatorLayout.showLongSnackbar(
                    text = viewEffect.message,
                    actionText = getString(R.string.new_list_created_action),
                    onClick = {
                        sharedViewModel.dispatch(MainAction.SeeMovieListClicked(viewEffect.listId))
                    }
                )
            }
        }
    }

    private fun setupNavigation() {
        bottomNavigation.setupWithNavController(navController)

        // use the below line if we don't want fragment to be re-created when user re-selects the same button on bottom nav
        //bottomNavigation.setOnNavigationItemReselectedListener {  }
    }

    private fun addBottomBarVisibilityListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.showsFragment,
                R.id.moviesFragment,
                R.id.discoverFragment -> {
                    bottomNavigation.isVisible = true
                }
                else -> bottomNavigation.isVisible = false
            }
        }
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
