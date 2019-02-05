package com.atherton.upnext.presentation.features.shows


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import javax.inject.Inject
import javax.inject.Named

class ShowsFragment : BaseFragment<ShowsAction, ShowsState, ShowsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_shows
    override val stateBundleKey: String = "bundle_key_shows_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(ShowsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)

    }
    override val viewModel: ShowsViewModel by lazy {
        getViewModel<ShowsViewModel>(vmFactory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //todo dispatch action
    }

    override fun renderState(state: ShowsState) {

    }

    override fun initInjection(initialState: ShowsState?) {
        DaggerShowsComponent.builder()
            .showsModule(ShowsModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = ShowsFragment()
    }
}
