package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.domain.usecase.LoadPopularUseCase
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(
    initialState: SearchResultsState?,
    private val loadPopularUseCase: LoadPopularUseCase,
    private val schedulers: RxSchedulers
): BaseViewModel<SearchResultsAction, SearchResultsState>() {

    override val initialState = initialState ?: SearchResultsState()

    private val reducer: Reducer<SearchResultsState, SearchResultsChange> = { state, change ->
        when (change) {
            is SearchResultsChange.Loading -> state.copy( // loading state (could be with or without previous/cached results)
                isIdle = false,
                isLoading = true,
                results = state.results,
                cached = state.cached,
                failure = null
            )
            is SearchResultsChange.Result -> {
                when (change.response) {
                    is Response.Success -> { // success state (could be fresh or cached data)
                        state.copy(
                            isIdle = false,
                            isLoading = false,
                            results = change.response.data,
                            cached = change.response.cached,
                            failure = null
                        )
                    }
                    is Response.Failure -> { // error state (could be with or without previous/cached results)
                        state.copy(
                            isIdle = false,
                            isLoading = false,
                            results = emptyList(),
                            cached = state.cached,
                            failure = change.response
                        )
                    }
                }
            }
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val loadPopularResultsChange = actions.ofType<SearchResultsAction.LoadPopular>()
            .switchMap {
                loadPopularUseCase.build()
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<SearchResultsChange> { SearchResultsChange.Result(it) }
                    .defaultIfEmpty(SearchResultsChange.Result(Response.Failure.AppError.NoResourcesFound))
                    .startWith(SearchResultsChange.Loading)
            }

        disposables += loadPopularResultsChange
            .scan(initialState, reducer)
            .filter { !it.isIdle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class SearchResultsAction : BaseAction {
    object LoadPopular : SearchResultsAction()
    object ResultClicked : SearchResultsAction()
}

sealed class SearchResultsChange {
    object Loading : SearchResultsChange()
    data class Result(val response: Response<List<SearchModel>>) : SearchResultsChange()
}

@Parcelize
data class SearchResultsState(
    @Transient val isIdle: Boolean = true,
    @Transient val isLoading: Boolean = false,
    val results: List<SearchModel> = emptyList(),
    val cached: Boolean = false,
    val failure: Response.Failure? = null
) : BaseState, Parcelable


//================================================================================
// Factory
//================================================================================

@PerView
class SearchResultsViewModelFactory(
    private val initialState: SearchResultsState?,
    private val loadPopularUseCase: LoadPopularUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultsViewModel(initialState, loadPopularUseCase, schedulers) as T
    }

    companion object {
        const val NAME = "SearchResultsViewModelFactory"
    }
}
