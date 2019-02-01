package com.atherton.upnext.presentation.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.atherton.upnext.R
import com.atherton.upnext.util.base.BaseActivity
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_main

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    private val topLevelDestinationIds = setOf(R.id.moviesFragment, R.id.showsFragment, R.id.discoverFragment)
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinationIds)
    }
    private val navController: NavController by lazy {
        findNavController(R.id.navHostFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(vmFactory, MainViewModel::class.java)

        observeViewModel()
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    private fun observeViewModel() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun setupNavigation() {
        // toolbar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // bottom navigation
        bottomNavigation.setupWithNavController(navController)
    }

    // Have the NavHelper look for an action or destination matching the menu
    // item id and navigate there if found.
    // Otherwise, bubble up to the parent.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
                item,
                navController
        ) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun initInjection() {
        DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .build()
                .inject(this)
    }
}
