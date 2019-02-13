package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.DiscoverViewMode
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.usecase.*
import com.atherton.upnext.presentation.features.discover.withDiscoverSearchImageUrls
import com.atherton.upnext.util.base.BaseViewEffect
import com.atherton.upnext.util.base.UpNextViewModel
import com.atherton.upnext.util.extensions.preventMultipleClicks
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
    initialState: DiscoverState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val featuredMoviesTvUseCase: GetFeaturedMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers,
    private val titleProvider: DiscoverStringProvider
): UpNextViewModel<DiscoverAction, DiscoverState, DiscoverViewEffect>() {

    override val initialState = initialState ?: DiscoverState.Idle

    private val reducer: Reducer<DiscoverState, DiscoverChange> = { oldState, change ->
        when (change) {
            is DiscoverChange.Loading -> {
                when (oldState) {
                    is DiscoverState.Loading -> oldState.copy(results = oldState.results)
                    is DiscoverState.Content -> oldState.copy(results = oldState.results)
                    else -> DiscoverState.Loading(results = emptyList())
                }
            }
            is DiscoverChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverState.Content(
                            results = change.response.data.toDiscoverCarouselSections(titleProvider, change.config)
                        )
                    }
                    is Response.Failure -> DiscoverState.Error(failure = change.response)
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<DiscoverAction.Load>.toResultChange(): Observable<DiscoverChange> {
            return this.switchMap {
                featuredMoviesTvUseCase.build().zipWith(getConfigUseCase.build())
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<DiscoverChange> { dataAndConfigPair ->
                        DiscoverChange.Result(dataAndConfigPair.first, dataAndConfigPair.second)
                    }
                    .startWith(DiscoverChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<DiscoverAction.Load>()
            .distinctUntilChanged()
            .toResultChange()

        val retryButtonChange = actions.ofType<DiscoverAction.RetryButtonClicked>()
            .map { DiscoverAction.Load }
            .toResultChange()

        val stateChanges = merge(loadDataChange, retryButtonChange)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is DiscoverState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        // handles the initial loading of the view mode menu action icon
        val loadViewModeViewEffect = actions.ofType<DiscoverAction.Load>()
            .switchMap {
                getDiscoverViewModeUseCase.build()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverViewEffect.ToggleViewMode(it) }
            }

        // handles user clicking the view mode toggle menu action
        val viewModeToggleViewEffect = actions.ofType<DiscoverAction.ViewModeToggleActionClicked>()
            .preventMultipleClicks()
            .switchMap {
                toggleDiscoverViewModeUseCase.build()
                    .flatMap { getDiscoverViewModeUseCase.build() }
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map { DiscoverViewEffect.ToggleViewMode(it) }
            }

        val searchActionClickedViewEffect = actions.ofType<DiscoverAction.SearchActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { DiscoverViewEffect.ShowSearchResultsScreen }

        val searchModelClickedViewEffect = actions.ofType<DiscoverAction.SearchModelClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { DiscoverViewEffect.ShowSearchModelDetailScreen(it.searchModel) }

        val viewEffectChanges = merge(
            loadViewModeViewEffect,
            viewModeToggleViewEffect,
            searchActionClickedViewEffect,
            searchModelClickedViewEffect
        )

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::onNext, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverAction : BaseAction {
    object Load : DiscoverAction()
    object RetryButtonClicked : DiscoverAction()
    object ViewModeToggleActionClicked : DiscoverAction()
    object SearchActionClicked : DiscoverAction()
    data class SearchModelClicked(val searchModel: SearchModel) : DiscoverAction()
}

sealed class DiscoverChange {
    object Loading : DiscoverChange()
    data class Result(
        val response: Response<DiscoverFeaturedResponse>,
        val config: Config
    ) : DiscoverChange()
}

sealed class DiscoverState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverState()

    @Parcelize
    data class Loading(val results: List<DiscoverCarouselSection> = emptyList()) : DiscoverState()

    @Parcelize
    data class Content(val results: List<DiscoverCarouselSection> = emptyList()) : DiscoverState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverState()
}

sealed class DiscoverViewEffect : BaseViewEffect {
    data class ToggleViewMode(val viewMode: DiscoverViewMode) : DiscoverViewEffect()
    data class ShowSearchModelDetailScreen(val searchModel: SearchModel) : DiscoverViewEffect()
    object ShowSearchResultsScreen : DiscoverViewEffect()
}

//================================================================================
// View-Specific Mappers
//================================================================================

private fun DiscoverFeaturedResponse.toDiscoverCarouselSections(
    titleProvider: DiscoverStringProvider,
    config: Config
): List<DiscoverCarouselSection> {
    val discoverSections: MutableList<DiscoverCarouselSection> = ArrayList()
    return discoverSections.apply {
        popularTvMovies?.let {
            add(DiscoverCarouselSection(titleProvider.invoke(DiscoverTitle.Popular), it.withDiscoverSearchImageUrls(config)))
        }
        nowPlayingMovies?.let {
            add(DiscoverCarouselSection(titleProvider.invoke(DiscoverTitle.NowPlaying), it.withDiscoverSearchImageUrls(config)))
        }
        topRatedTvMovies?.let {
            add(DiscoverCarouselSection(titleProvider.invoke(DiscoverTitle.TopRated), it.withDiscoverSearchImageUrls(config)))
        }
    }.toList()
}

//================================================================================
// View Items
//================================================================================

@Parcelize
data class DiscoverCarouselSection(val title: String, val data: List<SearchModel>) : Parcelable

sealed class DiscoverTitle {
    object Popular : DiscoverTitle()
    object NowPlaying : DiscoverTitle()
    object TopRated : DiscoverTitle()
}

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverViewModelFactory(
    private val initialState: DiscoverState?,
    private val toggleDiscoverViewModeUseCase: ToggleDiscoverViewModeUseCase,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val featuredMoviesTvUseCase: GetFeaturedMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers,
    private val stringProvider: DiscoverStringProvider
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverViewModel(
            initialState,
            toggleDiscoverViewModeUseCase,
            getDiscoverViewModeUseCase,
            featuredMoviesTvUseCase,
            getConfigUseCase,
            schedulers,
            stringProvider
        ) as T
    }

    companion object {
        const val NAME = "DiscoverViewModelFactory"
    }
}

typealias DiscoverStringProvider = (DiscoverTitle) -> String
