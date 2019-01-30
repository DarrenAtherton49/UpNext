package com.atherton.tmdb.ui.features.movies


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atherton.tmdb.R
import com.atherton.tmdb.ui.main.MainViewModel
import com.atherton.tmdb.util.base.BaseFragment
import com.atherton.tmdb.util.extensions.getActivityViewModel
import com.atherton.tmdb.util.extensions.getAppComponent
import javax.inject.Inject

class MoviesFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_movies

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var activityViewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //viewModel = getViewModel(vmFactory, MoviesViewModel::class.java) //todo
        activityViewModel = getActivityViewModel(vmFactory, MainViewModel::class.java)

        observeViewModels()
    }

    private fun observeViewModels() {

    }

    override fun initInjection() {
        DaggerMoviesComponent.builder()
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = MoviesFragment()
    }
}
