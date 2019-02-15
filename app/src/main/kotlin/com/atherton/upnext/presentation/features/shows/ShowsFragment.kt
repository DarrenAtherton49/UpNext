package com.atherton.upnext.presentation.features.shows


import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import com.atherton.upnext.util.viewpager.FragmentViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_shows.*
import javax.inject.Inject
import javax.inject.Named

class ShowsFragment : BaseFragment<ShowsAction, ShowsState, ShowsViewEffect, ShowsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_shows
    override val stateBundleKey: String = "bundle_key_shows_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(ShowsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: ShowsViewModel by lazy { getViewModel<ShowsViewModel>(vmFactory) }
    private val viewPagerAdapter: FragmentViewPagerAdapter by lazy { FragmentViewPagerAdapter(childFragmentManager) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()

        addShowButton.setOnClickListener {
            sharedViewModel.dispatch(MainAction.AddShowButtonClicked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu != null && inflater != null) {
            inflater.inflate(R.menu.menu_shows, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun renderState(state: ShowsState) {

    }

    override fun processViewEffects(viewEffect: ShowsViewEffect) {

    }

    private fun initViewPager() {
        //todo
        viewPagerAdapter.clear()
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_up_next), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_watchlist), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_finished), )
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initInjection(initialState: ShowsState?) {
        DaggerShowsComponent.builder()
            .showsModule(ShowsModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
