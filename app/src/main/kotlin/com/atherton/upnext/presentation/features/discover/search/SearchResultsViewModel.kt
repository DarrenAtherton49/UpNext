package com.atherton.upnext.presentation.features.discover.search

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.SearchMultiUseCase
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import io.reactivex.Observable.just
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor(
    initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val getConfigUseCase: GetConfigUseCase,
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
                            results = change.response.data.withImageUrls(change.config),
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
                just(action).debounce(milliseconds, TimeUnit.MILLISECONDS)
            }
            .distinctUntilChanged()
            .switchMap { action ->
                searchMultiUseCase.build(action.query).zipWith(getConfigUseCase.build())
                    .subscribeOn(schedulers.io)
                    .toObservable()
                    .map<SearchResultsChange> { SearchResultsChange.Result(action.query, it.first, it.second) }
                    .defaultIfEmpty(
                        SearchResultsChange.Result(
                            query = action.query,
                            response = Response.Failure.AppError.NoResourcesFound,
                            config = null
                        )
                    )
                    .startWith(SearchResultsChange.Loading)
            }

        //todo add search model clicked change

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
    data class ResultClicked(val searchModel: SearchModel) : SearchResultsAction()
}

sealed class SearchResultsChange {
    object Loading : SearchResultsChange()
    data class Result(
        val query: String,
        val response: Response<List<SearchModel>>,
        val config: Config?
    ) : SearchResultsChange()
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
// View-Specific Mappers
//================================================================================

// generate image urls for this screen using the base url, size and path
private fun List<SearchModel>.withImageUrls(config: Config?): List<SearchModel> {

    //todo write function to generate path based on device screen size?
    fun buildPosterPath(posterPath: String?, config: Config): String? =
        posterPath?.let { "${config.secureBaseUrl}${config.posterSizes[2]}$posterPath" }

    //todo write function to generate path based on device screen size?
    fun buildProfilePath(profilePath: String?, config: Config): String? =
        profilePath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$profilePath" }

    //todo write function to generate path based on device screen size?
    fun buildBackdropPath(backdropPath: String?, config: Config): String? =
        backdropPath?.let { "${config.secureBaseUrl}${config.profileSizes[1]}$backdropPath" }

    return if (config != null) {
        this.map {
            when (it) {
                is TvShow -> {
                    it.copy(
                        backdropPath = buildBackdropPath(it.backdropPath, config),
                        posterPath = buildPosterPath(it.posterPath, config)
                    )
                }
                is Movie -> {
                    it.copy(
                        backdropPath = buildBackdropPath(it.backdropPath, config),
                        posterPath = buildPosterPath(it.posterPath, config)
                    )
                }
                is Person -> it.copy(profilePath = buildProfilePath(it.profilePath, config))
            }
        }
    } else this
}

//================================================================================
// Factory
//================================================================================

@PerView
class SearchResultsViewModelFactory(
    private val initialState: SearchResultsState?,
    private val searchMultiUseCase: SearchMultiUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchResultsViewModel(initialState, searchMultiUseCase, getConfigUseCase, schedulers) as T
    }

    companion object {
        const val NAME = "SearchResultsViewModelFactory"
    }
}
