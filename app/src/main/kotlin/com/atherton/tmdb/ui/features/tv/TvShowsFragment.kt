package com.atherton.tmdb.ui.features.tv


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atherton.tmdb.R
import com.atherton.tmdb.ui.main.MainViewModel
import com.atherton.tmdb.util.base.BaseFragment
import com.atherton.tmdb.util.extensions.getActivityViewModel
import com.atherton.tmdb.util.extensions.getAppComponent
import javax.inject.Inject

class TvShowsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_tv_shows

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
        DaggerTvShowsComponent.builder()
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = TvShowsFragment()
    }
}
