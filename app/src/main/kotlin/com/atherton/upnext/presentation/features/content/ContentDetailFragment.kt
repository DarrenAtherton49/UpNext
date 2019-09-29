package com.atherton.upnext.presentation.features.content


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.presentation.base.BaseFragment
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.presentation.features.content.adapter.ModelDetailAdapter
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.presentation.util.glide.GlideApp
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.presentation.util.toolbar.ToolbarOptions
import com.atherton.upnext.util.extension.getActivityViewModel
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import kotlinx.android.synthetic.main.detail_screen_appbar.*
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_content_detail.*
import javax.inject.Inject
import javax.inject.Named

class ContentDetailFragment : BaseFragment<ContentDetailAction, ContentDetailState, ContentDetailViewEffect, ContentDetailViewModel>() {

    override val layoutResId: Int = R.layout.fragment_content_detail
    override val stateBundleKey: String = "bundle_key_content_detail_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(ContentDetailViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: ContentDetailViewModel by lazy { getViewModel<ContentDetailViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = null,
        menuResId = R.menu.menu_content_detail
    )

    @Inject lateinit var imageLoader: ImageLoader

    private val recyclerViewAdapter: ModelDetailAdapter by lazy {
        ModelDetailAdapter(
            imageLoader = imageLoader,
            glideRequests = GlideApp.with(this),
            childRecyclerItemSpacingPx = resources.getDimensionPixelSize(R.dimen.content_detail_child_items_spacing),
            onSeasonClickListener = { season -> viewModel.dispatch(ContentDetailAction.SeasonClicked(season)) },
            onCastMemberClickListener = { castMember -> viewModel.dispatch(ContentDetailAction.CastMemberClicked(castMember)) },
            onCrewMemberClickListener = { crewMember -> viewModel.dispatch(ContentDetailAction.CrewMemberClicked(crewMember)) },
            onVideoClickListener = { video -> viewModel.dispatch(ContentDetailAction.YouTubeVideoClicked(video)) },
            onRecommendedItemClickListener = { movie -> viewModel.dispatch(ContentDetailAction.RecommendedContentClicked(movie)) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        arguments?.let {
            val args = ContentDetailFragmentArgs.fromBundle(it)
            val contentId: Long = args.contentId
            val contentType: ContentType = args.contentType

            initClickListeners(contentId, contentType)

            if (savedInstanceState == null) {
                viewModel.dispatch(ContentDetailAction.Load(contentId, contentType))
            }
        }
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            R.id.action_settings -> {
                sharedViewModel.dispatch(MainAction.SettingsActionClicked)
                true
            }
            else -> false
        }
    }

    override fun renderState(state: ContentDetailState) {
        when (state) {
            is ContentDetailState.Loading -> {
                errorLayout.isVisible = false
                progressBar.isVisible = true

                if (state.watchable != null && state.detailSections != null) {
                    // show a loading state with cached data
                    recyclerView.isVisible = true
                    posterImageView.isVisible = true
                    watchlistButton.isVisible = true
                    addToListButton.isVisible = true
                    renderContent(state.watchable, state.detailSections)
                } else {
                    recyclerView.isVisible = false
                    posterImageView.isVisible = false
                    watchlistButton.isVisible = false
                    addToListButton.isVisible = false
                }
            }
            is ContentDetailState.Content -> {
                progressBar.isVisible = false
                errorLayout.isVisible = false
                posterImageView.isVisible = true
                recyclerView.isVisible = true
                watchlistButton.isVisible = true
                addToListButton.isVisible = true
                renderContent(state.watchable, state.detailSections)
            }
            is ContentDetailState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                posterImageView.isVisible = false
                watchlistButton.isVisible = false
                addToListButton.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.message
                retryButton.isVisible = state.canRetry
            }
        }
    }

    private fun renderContent(watchable: Watchable, detailSections: List<ModelDetailSection>) {
        renderContentImages(watchable.backdropPath, watchable.posterPath)
        titleTextView.text = watchable.title
        recyclerViewAdapter.submitData(detailSections)

        watchlistButton.text = if (watchable.state.inWatchlist) {
            getString(R.string.content_detail_remove_from_watchlist)
        } else {
            getString(R.string.content_detail_add_to_watchlist)
        }
    }

    private fun renderContentImages(backdropPath: String?, posterPath: String?) {
        imageLoader.load(
            with = GlideApp.with(this),
            url = backdropPath,
            requestOptions = ImageLoader.modelDetailBackdropRequestOptions,
            into = backdropImageView
        )

        imageLoader.load(
            with = GlideApp.with(this),
            url = posterPath,
            requestOptions = ImageLoader.searchModelPosterRequestOptions,
            into = backdropImageView
        )
    }

    override fun processViewEffects(viewEffect: ContentDetailViewEffect) {
        when (viewEffect) {
            is ContentDetailViewEffect.ShowTvShowDetailScreen -> {
                sharedViewModel.dispatch(MainAction.TvShowClicked(viewEffect.tvShowId))
            }
            is ContentDetailViewEffect.ShowMovieDetailScreen -> {
                sharedViewModel.dispatch(MainAction.MovieClicked(viewEffect.movieId))
            }
            is ContentDetailViewEffect.ShowPersonDetailScreen -> {
                sharedViewModel.dispatch(MainAction.PersonClicked(viewEffect.personId))
            }
            is ContentDetailViewEffect.PlayYoutubeVideo -> {
                sharedViewModel.dispatch(MainAction.YouTubeVideoClicked(viewEffect.videoKey))
            }
            is ContentDetailViewEffect.ShowAddToListMenu -> {
                navigator.showAddToListsMenu(
                    contentId = viewEffect.contentId,
                    contentType = viewEffect.contentType,
                    fragmentManager = childFragmentManager
                )
            }
            is ContentDetailViewEffect.ShowSettingsScreen -> {
                sharedViewModel.dispatch(MainAction.SettingsActionClicked)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initClickListeners(contentId: Long, contentType: ContentType) {
        retryButton.setOnClickListener {
            viewModel.dispatch(ContentDetailAction.RetryButtonClicked(contentId, contentType))
        }

        watchlistButton.setOnClickListener {
            viewModel.dispatch(ContentDetailAction.WatchlistButtonClicked(contentId, contentType))
        }

        addToListButton.setOnClickListener {
            viewModel.dispatch(ContentDetailAction.AddToListButtonClicked(contentId, contentType))
        }
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            //todo item decorations?
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    override fun initInjection(initialState: ContentDetailState?) {
        DaggerContentDetailComponent.builder()
            .contentDetailModule(ContentDetailModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
