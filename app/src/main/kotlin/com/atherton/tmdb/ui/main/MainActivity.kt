package com.atherton.tmdb.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.atherton.tmdb.R
import com.atherton.tmdb.util.base.BaseActivity
import com.atherton.tmdb.util.extensions.getAppComponent
import com.atherton.tmdb.util.extensions.getViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_main

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(vmFactory, MainViewModel::class.java)

        observeViewModel()

        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        setupNavigation(host.navController)
    }

    private fun observeViewModel() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun setupNavigation(navController: NavController) {
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // toolbar
        setupActionBarWithNavController(navController)

        // bottom navigation
        bottomNavigation.setupWithNavController(navController)
    }

    // Have the NavHelper look for an action or destination matching the menu
    // item id and navigate there if found.
    // Otherwise, bubble up to the parent.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
                item,
                findNavController(R.id.navHostFragment)
        ) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun initInjection() {
        DaggerMainComponent.builder()
                .appComponent(getAppComponent())
                .build()
                .inject(this)
    }
}
