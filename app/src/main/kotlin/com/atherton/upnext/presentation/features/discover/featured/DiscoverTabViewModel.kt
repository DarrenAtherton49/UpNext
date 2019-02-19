package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverMoviesTvUseCase
import com.atherton.upnext.domain.usecase.GetDiscoverViewModeUseCase
import com.atherton.upnext.presentation.common.withDiscoverSearchImageUrls
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
import io.reactivex.Single.zip
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class DiscoverTabViewModel @Inject constructor(
    initialState: DiscoverTabState?,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverTabAction, DiscoverTabState, DiscoverTabViewEffect>() {

    override val initialState = initialState ?: DiscoverTabState.Idle

    private val reducer: Reducer<DiscoverTabState, DiscoverTabChange> = { oldState, change ->
        when (change) {
            is DiscoverTabChange.Loading -> {
                when (oldState) {
                    is DiscoverTabState.Loading -> oldState.copy()
                    is DiscoverTabState.Content -> {
                        DiscoverTabState.Loading(results = oldState.results)
                    }
                    is DiscoverTabState.Error -> DiscoverTabState.Loading()
                    else -> DiscoverTabState.Loading()
                }
            }
            is DiscoverTabChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverTabState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            viewMode = change.viewMode
                        )
                    }
                    is Response.Failure -> DiscoverTabState.Error(failure = change.response)
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<DiscoverTabAction.Load>.toResultChange(): Observable<DiscoverTabChange> {
            return this.switchMap {
                zip(getDiscoverMoviesTvUseCase.build(),
                    getConfigUseCase.build(),
                    getDiscoverViewModeUseCase.build(),
                    Function3<Response<List<SearchModel>>,
                        Config,
                        SearchModelViewMode,
                        DiscoverTabViewData> { searchModels, config, viewMode ->
                        DiscoverTabViewData(searchModels, config, viewMode)
                    })
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<DiscoverTabChange> { viewData ->
                        DiscoverTabChange.Result(
                            response = viewData.searchModels,
                            config = viewData.config,
                            viewMode = viewData.viewMode
                        )
                    }
                    .startWith(DiscoverTabChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<DiscoverTabAction.Load>()
            .distinctUntilChanged()
            .toResultChange()

        val viewModeToggleChange = actions.ofType<DiscoverTabAction.ViewModeToggleChanged>()
            .map { DiscoverTabAction.Load }
            .toResultChange()

        val retryButtonChange = actions.ofType<DiscoverTabAction.RetryButtonClicked>()
            .map { DiscoverTabAction.Load }
            .toResultChange()

        val searchModelClickedViewEffect = actions.ofType<DiscoverTabAction.SearchModelClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { DiscoverTabViewEffect.ShowSearchModelDetailScreen(it.searchModel) }

        val stateChanges = merge(loadDataChange, viewModeToggleChange, retryButtonChange)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is DiscoverTabState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)

        disposables += searchModelClickedViewEffect
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverTabAction : BaseAction {
    object Load : DiscoverTabAction()
    object ViewModeToggleChanged : DiscoverTabAction()
    object RetryButtonClicked : DiscoverTabAction()
    data class SearchModelClicked(val searchModel: SearchModel) : DiscoverTabAction()
}

sealed class DiscoverTabChange {
    object Loading : DiscoverTabChange()
    data class Result(
        val response: Response<List<SearchModel>>,
        val config: Config,
        val viewMode: SearchModelViewMode
    ) : DiscoverTabChange()
}

sealed class DiscoverTabState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverTabState()

    @Parcelize
    data class Loading(val results: List<SearchModel> = emptyList()) : DiscoverTabState()

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        val viewMode: SearchModelViewMode
    ) : DiscoverTabState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverTabState()
}

sealed class DiscoverTabViewEffect : BaseViewEffect {
    data class ShowSearchModelDetailScreen(val searchModel: SearchModel) : DiscoverTabViewEffect()
}

//================================================================================
// Data model
//================================================================================

// this class is just used as the result of zipping the necessary Observables together
data class DiscoverTabViewData(
    val searchModels: Response<List<SearchModel>>,
    val config: Config,
    val viewMode: SearchModelViewMode
)

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverTabViewModelFactory(
    private val initialState: DiscoverTabState?,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverTabViewModel(
            initialState,
            getDiscoverViewModeUseCase,
            getDiscoverMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverTabViewModelFactory"
    }
}
