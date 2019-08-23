package com.atherton.upnext.presentation.features.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.repository.MovieRepository
import com.atherton.upnext.domain.repository.TvShowRepository
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.presentation.util.AppStringProvider
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
import io.reactivex.Observable.mergeArray
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class ContentDetailViewModel @Inject constructor(
    initialState: ContentDetailState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : UpNextViewModel<ContentDetailAction, ContentDetailState, ContentDetailViewEffect>() {

    override val initialState = initialState ?: ContentDetailState.Idle

    private val reducer: Reducer<ContentDetailState, ContentDetailChange> = { oldState, change ->
        when (change) {
            is ContentDetailChange.Loading -> {
                when (oldState) {
                    is ContentDetailState.Idle -> ContentDetailState.Loading(null, null)
                    is ContentDetailState.Loading -> oldState.copy()
                    is ContentDetailState.Content -> {
                        ContentDetailState.Loading(watchable = oldState.watchable, detailSections = oldState.detailSections)
                    }
                    is ContentDetailState.Error -> ContentDetailState.Loading(null, null)
                }
            }
            is ContentDetailChange.Result -> {
                when (change.response) {
                    is LceResponse.Loading -> {
                        val watchableWithImageUrls = change.response.data?.withContentDetailImageUrls(change.config)
                        val detailSections = watchableWithImageUrls?.let {
                            buildContentDetailSections(it, appStringProvider)
                        }
                        ContentDetailState.Loading(
                            watchable = watchableWithImageUrls,
                            detailSections = detailSections
                        )
                    }
                    is LceResponse.Content -> {
                        val watchableWithImageUrls = change.response.data.withContentDetailImageUrls(change.config)
                        ContentDetailState.Content(
                            watchable = watchableWithImageUrls,
                            detailSections = buildContentDetailSections(watchableWithImageUrls, appStringProvider)
                        )
                    }
                    is LceResponse.Error -> {
                        ContentDetailState.Error(
                            message = appStringProvider.generateErrorMessage(change.response),
                            canRetry = change.response is LceResponse.Error.NetworkError
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
        fun Observable<ContentDetailAction.Load>.toResultChange(): Observable<ContentDetailChange> {
            return this.switchMap { action ->
                val contentObservable = when (action.contentType) {
                    is ContentType.TvShow -> {
                        tvShowRepository.getTvShow(action.contentId)
                            .map<LceResponse<Watchable>> { it }
                    }
                    is ContentType.Movie -> {
                        movieRepository.getMovie(action.contentId)
                            .map<LceResponse<Watchable>> { it }
                    }
                }
                contentObservable
                    .subscribeOn(schedulers.io)
                    .map<ContentDetailChange> { watchableResponse ->
                        ContentDetailChange.Result(watchableResponse, configRepository.getConfig())
                    }
                    .startWith(ContentDetailChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<ContentDetailAction.Load>()
            .toResultChange()

        val retryButtonChange = actions.ofType<ContentDetailAction.RetryButtonClicked>()
            .map { ContentDetailAction.Load(it.contentId, it.contentType) }
            .preventMultipleClicks()
            .toResultChange()

        val watchlistButtonChange = actions.ofType<ContentDetailAction.WatchlistButtonClicked>()
            .preventMultipleClicks()
            .switchMap { action ->
                val watchlistObservable = when (action.contentType) {
                    is ContentType.TvShow -> {
                        tvShowRepository.toggleTvShowWatchlistStatus(action.contentId)
                            .map<LceResponse<Watchable>> { it }
                    }
                    is ContentType.Movie -> {
                        movieRepository.toggleMovieWatchlistStatus(action.contentId)
                            .map<LceResponse<Watchable>> { it }
                    }
                }
                watchlistObservable
                    .subscribeOn(schedulers.io)
                    .map<ContentDetailChange> { watchableResponse ->
                        ContentDetailChange.Result(watchableResponse, configRepository.getConfig())
                    }
                    .startWith(ContentDetailChange.Loading)
            }

        val seasonClickedViewEffect = actions.ofType<ContentDetailAction.SeasonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.season.id.let { id ->
                    ContentDetailViewEffect.ShowSeasonDetailScreen(id)
                }
            }

        val castMemberClickedViewEffect = actions.ofType<ContentDetailAction.CastMemberClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.castMember.id.let { id ->
                    ContentDetailViewEffect.ShowPersonDetailScreen(id)
                }
            }

        val crewMemberClickedViewEffect = actions.ofType<ContentDetailAction.CrewMemberClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.crewMember.id.let { id ->
                    ContentDetailViewEffect.ShowPersonDetailScreen(id)
                }
            }

        val videoClickedViewEffect = actions.ofType<ContentDetailAction.YouTubeVideoClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { ContentDetailViewEffect.PlayYoutubeVideo(it.video.key) }

        val recommendedContentClickedViewEffect = actions.ofType<ContentDetailAction.RecommendedContentClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                when (it.watchable) {
                    is TvShow -> ContentDetailViewEffect.ShowTvShowDetailScreen(it.watchable.id)
                    is Movie -> ContentDetailViewEffect.ShowMovieDetailScreen(it.watchable.id)
                }
            }

        val addToListViewEffect = actions.ofType<ContentDetailAction.AddToListButtonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { action -> ContentDetailViewEffect.ShowAddToListMenu(action.contentId, action.contentType) }

        val settingsActionClickedViewEffect = actions.ofType<ContentDetailAction.SettingsActionClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map { ContentDetailViewEffect.ShowSettingsScreen }

        val stateChanges = merge(
            loadDataChange,
            retryButtonChange,
            watchlistButtonChange
        )

        val viewEffectChanges = mergeArray(
            seasonClickedViewEffect,
            castMemberClickedViewEffect,
            crewMemberClickedViewEffect,
            videoClickedViewEffect,
            recommendedContentClickedViewEffect,
            addToListViewEffect,
            settingsActionClickedViewEffect
        )

        disposables += viewEffectChanges
            .observeOn(schedulers.main)
            .subscribe(viewEffects::accept, Timber::e)

        disposables += stateChanges
            .scan(initialState, reducer)
            .filter { it !is ContentDetailState.Idle }
            .distinctUntilChanged()
            .observeOn(schedulers.main)
            .subscribe(state::setValue, Timber::e)
    }
}

//================================================================================
// MVI
//================================================================================

sealed class ContentDetailAction : BaseAction {
    data class Load(val contentId: Long, val contentType: ContentType) : ContentDetailAction()
    data class RetryButtonClicked(val contentId: Long, val contentType: ContentType) : ContentDetailAction()
    data class WatchlistButtonClicked(val contentId: Long, val contentType: ContentType) : ContentDetailAction()
    data class SeasonClicked(val season: TvSeason) : ContentDetailAction()
    data class CastMemberClicked(val castMember: CastMember) : ContentDetailAction()
    data class CrewMemberClicked(val crewMember: CrewMember) : ContentDetailAction()
    data class YouTubeVideoClicked(val video: Video) : ContentDetailAction()
    data class RecommendedContentClicked(val watchable: Watchable) : ContentDetailAction()
    data class AddToListButtonClicked(val contentId: Long, val contentType: ContentType) : ContentDetailAction()
    object SettingsActionClicked : ContentDetailAction()
}

sealed class ContentDetailChange {
    object Loading : ContentDetailChange()
    data class Result(val response: LceResponse<Watchable>, val config: Config) : ContentDetailChange()
}

sealed class ContentDetailState : BaseState, Parcelable {

    @Parcelize
    object Idle : ContentDetailState()

    @Parcelize
    data class Loading(
        val watchable: Watchable?,
        val detailSections: List<ModelDetailSection>?
    ) : ContentDetailState()

    @Parcelize
    data class Content(
        val watchable: Watchable,
        val detailSections: List<ModelDetailSection>
    ) : ContentDetailState()

    @Parcelize
    data class Error(val message: String, val canRetry: Boolean) : ContentDetailState()
}

sealed class ContentDetailViewEffect : BaseViewEffect {
    data class ShowTvShowDetailScreen(val tvShowId: Long) : ContentDetailViewEffect()
    data class ShowMovieDetailScreen(val movieId: Long) : ContentDetailViewEffect()
    data class ShowPersonDetailScreen(val personId: Long) : ContentDetailViewEffect()
    data class PlayYoutubeVideo(val videoKey: String) : ContentDetailViewEffect()
    data class ShowSeasonDetailScreen(val seasonId: Long) : ContentDetailViewEffect()
    data class ShowAddToListMenu(val contentId: Long, val contentType: ContentType) : ContentDetailViewEffect()
    object ShowSettingsScreen : ContentDetailViewEffect()
}

//================================================================================
// Factory
//================================================================================

@PerView
class ContentDetailViewModelFactory(
    private val initialState: ContentDetailState?,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val configRepository: ConfigRepository,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
        return ContentDetailViewModel(
            initialState,
            movieRepository,
            tvShowRepository,
            configRepository,
            appStringProvider,
            schedulers) as T
    }

    companion object {
        const val NAME = "ContentDetailViewModelFactory"
    }
}
