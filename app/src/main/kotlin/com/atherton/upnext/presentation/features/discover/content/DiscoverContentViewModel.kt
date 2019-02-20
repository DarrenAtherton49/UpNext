package com.atherton.upnext.presentation.features.discover.content

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

class DiscoverContentViewModel @Inject constructor(
    initialState: DiscoverContentState?,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
): UpNextViewModel<DiscoverContentAction, DiscoverContentState, DiscoverContentViewEffect>() {

    override val initialState = initialState ?: DiscoverContentState.Idle

    private val reducer: Reducer<DiscoverContentState, DiscoverContentChange> = { oldState, change ->
        when (change) {
            is DiscoverContentChange.Loading -> {
                when (oldState) {
                    is DiscoverContentState.Loading -> oldState.copy()
                    is DiscoverContentState.Content -> {
                        DiscoverContentState.Loading(results = oldState.results)
                    }
                    is DiscoverContentState.Error -> DiscoverContentState.Loading()
                    else -> DiscoverContentState.Loading()
                }
            }
            is DiscoverContentChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        DiscoverContentState.Content(
                            results = change.response.data.withDiscoverSearchImageUrls(change.config),
                            cached = change.response.cached,
                            viewMode = change.viewMode
                        )
                    }
                    is Response.Failure -> DiscoverContentState.Error(failure = change.response)
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        fun Observable<DiscoverContentAction.Load>.toResultChange(): Observable<DiscoverContentChange> {
            return this.switchMap {
                zip(getDiscoverMoviesTvUseCase.build(),
                    getConfigUseCase.build(),
                    getDiscoverViewModeUseCase.build(),
                    Function3<Response<List<SearchModel>>,
                        Config,
                        SearchModelViewMode,
                        DiscoverContentViewData> { searchModels, config, viewMode ->
                        DiscoverContentViewData(searchModels, config, viewMode)
                    })
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<DiscoverContentChange> { viewData ->
                        DiscoverContentChange.Result(
                            response = viewData.searchModels,
                            config = viewData.config,
                            viewMode = viewData.viewMode
                        )
                    }
                    .startWith(DiscoverContentChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<DiscoverContentAction.Load>()
            .distinctUntilChanged()
            .toResultChange()

        val viewModeToggleChange = actions.ofType<DiscoverContentAction.ViewModeToggleChanged>()
            .distinctUntilChanged()
            .map { DiscoverContentAction.Load }
            .toResultChange()

        val retryButtonChange = actions.ofType<DiscoverContentAction.RetryButtonClicked>()
            .map { DiscoverContentAction.Load }
            .toResultChange()

        val searchModelClickedViewEffect = actions.ofType<DiscoverContentAction.SearchModelClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { DiscoverContentViewEffect.ShowSearchModelDetailScreen(it.searchModel) }

        val stateChanges = merge(loadDataChange, viewModeToggleChange, retryButtonChange)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is DiscoverContentState.Idle }
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

sealed class DiscoverContentAction : BaseAction {
    object Load : DiscoverContentAction()
    data class ViewModeToggleChanged(val viewMode: SearchModelViewMode) : DiscoverContentAction()
    object RetryButtonClicked : DiscoverContentAction()
    data class SearchModelClicked(val searchModel: SearchModel) : DiscoverContentAction()
}

sealed class DiscoverContentChange {
    object Loading : DiscoverContentChange()
    data class Result(
        val response: Response<List<SearchModel>>,
        val config: Config,
        val viewMode: SearchModelViewMode
    ) : DiscoverContentChange()
}

sealed class DiscoverContentState : BaseState, Parcelable {

    @Parcelize
    object Idle : DiscoverContentState()

    @Parcelize
    data class Loading(val results: List<SearchModel> = emptyList()) : DiscoverContentState()

    @Parcelize
    data class Content(
        val results: List<SearchModel> = emptyList(),
        val cached: Boolean = false,
        val viewMode: SearchModelViewMode
    ) : DiscoverContentState()

    @Parcelize
    data class Error(val failure: Response.Failure) : DiscoverContentState()
}

sealed class DiscoverContentViewEffect : BaseViewEffect {
    data class ShowSearchModelDetailScreen(val searchModel: SearchModel) : DiscoverContentViewEffect()
}

//================================================================================
// Data model
//================================================================================

// this class is just used as the result of zipping the necessary Observables together
private data class DiscoverContentViewData(
    val searchModels: Response<List<SearchModel>>,
    val config: Config,
    val viewMode: SearchModelViewMode
)

//================================================================================
// Factory
//================================================================================

@PerView
class DiscoverContentViewModelFactory(
    private val initialState: DiscoverContentState?,
    private val getDiscoverViewModeUseCase: GetDiscoverViewModeUseCase,
    private val getDiscoverMoviesTvUseCase: GetDiscoverMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverContentViewModel(
            initialState,
            getDiscoverViewModeUseCase,
            getDiscoverMoviesTvUseCase,
            getConfigUseCase,
            schedulers
        ) as T
    }

    companion object {
        const val NAME = "DiscoverContentViewModelFactory"
    }
}
