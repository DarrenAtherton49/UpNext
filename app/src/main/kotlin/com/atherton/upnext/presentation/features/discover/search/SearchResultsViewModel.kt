package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.domain.usecase.SearchMultiUseCase
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(
    initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val schedulers: RxSchedulers
): BaseViewModel<SearchResultsAction, SearchResultsState>() {

    override val initialState = initialState ?: SearchResultsState()

    private val reducer: Reducer<SearchResultsState, SearchResultsChange> = { state, change ->
        when (change) {
            is SearchResultsChange.Loading -> state.copy( // loading state (could be with or without previous/cached results)
                isIdle = false,
                isLoading = true,
                query = state.query,
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
                            query = change.query,
                            results = change.response.data,
                            cached = change.response.cached,
                            failure = null
                        )
                    }
                    is Response.Failure -> { // error state (could be with or without previous/cached results)
                        state.copy(
                            isIdle = false,
                            isLoading = false,
                            query = change.query,
                            results = emptyList(),
                            cached = false,
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
        val textSearchedChange = actions.ofType<SearchResultsAction.SearchTextChanged>()
            .debounce { action ->
                // only debounce if query contains text, otherwise show popular straight away
                val milliseconds: Long = if (action.query.isBlank()) 0 else 250
                Observable.just(action).debounce(milliseconds, TimeUnit.MILLISECONDS)
            }
            .distinctUntilChanged()
            .switchMap { action ->
                searchMultiUseCase.build(action.query)
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<SearchResultsChange> { SearchResultsChange.Result(action.query, it) }
                    .defaultIfEmpty(SearchResultsChange.Result(action.query, Response.Failure.AppError.NoResourcesFound))
                    .startWith(SearchResultsChange.Loading)
            }

        disposables += textSearchedChange
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
    data class SearchTextChanged(val query: String) : SearchResultsAction()
    object ResultClicked : SearchResultsAction()
}

sealed class SearchResultsChange {
    object Loading : SearchResultsChange()
    data class Result(val query: String, val response: Response<List<SearchModel>>) : SearchResultsChange()
}

@Parcelize
data class SearchResultsState(
    @Transient val isIdle: Boolean = true,
    @Transient val isLoading: Boolean = false,
    val query: String = "",
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
    private val searchMultiUseCase: SearchMultiUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultsViewModel(initialState, searchMultiUseCase, schedulers) as T
    }

    companion object {
        const val NAME = "SearchResultsViewModelFactory"
    }
}
