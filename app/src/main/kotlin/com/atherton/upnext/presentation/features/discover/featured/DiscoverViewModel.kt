package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.Config
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.usecase.DiscoverFeaturedResponse
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetFeaturedMoviesTvUseCase
import com.atherton.upnext.presentation.features.discover.withDiscoverSearchImageUrls
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
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
    private val featuredMoviesTvUseCase: GetFeaturedMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers,
    private val titleProvider: DiscoverStringProvider
): BaseViewModel<DiscoverAction, DiscoverState>() {

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
                            results = change.response.data.toDiscoverSections(titleProvider, change.config)
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

        val allChanges = merge(loadDataChange, retryButtonChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .filter { it !is DiscoverState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class DiscoverAction : BaseAction {
    object Load : DiscoverAction()
    object RetryButtonClicked : DiscoverAction()
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
    data class Loading(
        val results: List<DiscoverSection> = emptyList()
    ) : DiscoverState()

    @Parcelize
    data class Content(
        val results: List<DiscoverSection> = emptyList()
    ) : DiscoverState()

    @Parcelize
    data class Error(
        val failure: Response.Failure
    ) : DiscoverState()
}

//================================================================================
// View-Specific Mappers
//================================================================================

private fun DiscoverFeaturedResponse.toDiscoverSections(
    titleProvider: DiscoverStringProvider,
    config: Config
): List<DiscoverSection> {
    val discoverSections: MutableList<DiscoverSection> = ArrayList()
    return discoverSections.apply {
        popularTvMovies?.let {
            add(DiscoverSection(titleProvider.invoke(DiscoverTitle.Popular), it.withDiscoverSearchImageUrls(config)))
        }
        nowPlayingMovies?.let {
            add(DiscoverSection(titleProvider.invoke(DiscoverTitle.NowPlaying), it.withDiscoverSearchImageUrls(config)))
        }
        topRatedTvMovies?.let {
            add(DiscoverSection(titleProvider.invoke(DiscoverTitle.TopRated), it.withDiscoverSearchImageUrls(config)))
        }
    }.toList()
}

//================================================================================
// View Items
//================================================================================

@Parcelize
data class DiscoverSection(val title: String, val data: List<SearchModel>) : Parcelable

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
    private val featuredMoviesTvUseCase: GetFeaturedMoviesTvUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val schedulers: RxSchedulers,
    private val stringProvider: DiscoverStringProvider
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoverViewModel(
            initialState,
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
