package com.atherton.upnext.presentation.features.content

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.domain.usecase.GetConfigUseCase
import com.atherton.upnext.domain.usecase.GetMovieDetailUseCase
import com.atherton.upnext.domain.usecase.GetTvShowDetailUseCase
import com.atherton.upnext.presentation.common.detail.ModelDetailSection
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
import io.reactivex.rxkotlin.zipWith
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import javax.inject.Inject

class ContentDetailViewModel @Inject constructor(
    initialState: ContentDetailState?,
    private val getTvShowDetailUseCase: GetTvShowDetailUseCase,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
): UpNextViewModel<ContentDetailAction, ContentDetailState, ContentDetailViewEffect>() {

    override val initialState = initialState ?: ContentDetailState.Idle

    private val reducer: Reducer<ContentDetailState, ContentDetailChange> = { oldState, change ->
        when (change) {
            is ContentDetailChange.Loading -> {
                when (oldState) {
                    is ContentDetailState.Idle -> ContentDetailState.Loading(null)
                    is ContentDetailState.Loading -> oldState.copy()
                    is ContentDetailState.Content -> {
                        ContentDetailState.Loading(watchable = oldState.watchable, detailSections = oldState.detailSections)
                    }
                    is ContentDetailState.Error -> ContentDetailState.Loading(null)
                }
            }
            is ContentDetailChange.Result -> {
                when (change.response) {
                    is Response.Success -> {
                        val watchableWithImageUrls = change.response.data.withContentDetailImageUrls(change.config)
                        ContentDetailState.Content(
                            watchable = watchableWithImageUrls,
                            detailSections = buildContentDetailSections(watchableWithImageUrls, appStringProvider),
                            cached = change.response.cached
                        )
                    }
                    is Response.Failure -> ContentDetailState.Error(failure = change.response)
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
                    is ContentType.TvShow -> getTvShowDetailUseCase.invoke(action.contentId)
                    is ContentType.Movie -> getMovieDetailUseCase.invoke(action.contentId)
                }
                contentObservable.zipWith(getConfigUseCase.invoke())
                    .subscribeOn(schedulers.io)
                    .map<ContentDetailChange> { ContentDetailChange.Result(it.first, it.second) }
                    .startWith(ContentDetailChange.Loading)
            }
        }

        val loadDataChange = actions.ofType<ContentDetailAction.Load>()
            .toResultChange()

        val retryButtonChange = actions.ofType<ContentDetailAction.RetryButtonClicked>()
            .map { ContentDetailAction.Load(it.contentId, it.contentType) }
            .preventMultipleClicks()
            .toResultChange()

        val seasonClickedViewEffect = actions.ofType<ContentDetailAction.SeasonClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.season.id?.let { id ->
                    ContentDetailViewEffect.ShowSeasonDetailScreen(id)
                }
            }

        val castMemberClickedViewEffect = actions.ofType<ContentDetailAction.CastMemberClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.castMember.id?.let { id ->
                    ContentDetailViewEffect.ShowPersonDetailScreen(id)
                }
            }

        val crewMemberClickedViewEffect = actions.ofType<ContentDetailAction.CrewMemberClicked>()
            .preventMultipleClicks()
            .subscribeOn(schedulers.io)
            .map {
                it.crewMember.id?.let { id ->
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

        val stateChanges = merge(loadDataChange, retryButtonChange)

        val viewEffectChanges = mergeArray(
            seasonClickedViewEffect,
            castMemberClickedViewEffect,
            crewMemberClickedViewEffect,
            videoClickedViewEffect,
            recommendedContentClickedViewEffect
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
    data class Load(val contentId: Int, val contentType: ContentType) : ContentDetailAction()
    data class RetryButtonClicked(val contentId: Int, val contentType: ContentType) : ContentDetailAction()
    data class SeasonClicked(val season: Season) : ContentDetailAction()
    data class CastMemberClicked(val castMember: CastMember) : ContentDetailAction()
    data class CrewMemberClicked(val crewMember: CrewMember) : ContentDetailAction()
    data class YouTubeVideoClicked(val video: Video) : ContentDetailAction()
    data class RecommendedContentClicked(val watchable: Watchable) : ContentDetailAction()
}

sealed class ContentDetailChange {
    object Loading : ContentDetailChange()
    data class Result(val response: Response<Watchable>, val config: Config) : ContentDetailChange()
}

sealed class ContentDetailState : BaseState, Parcelable {

    @Parcelize
    object Idle : ContentDetailState()

    @Parcelize
    data class Loading(
        val watchable: Watchable?,
        val detailSections: List<ModelDetailSection> = emptyList()
    ) : ContentDetailState()

    @Parcelize
    data class Content(
        val watchable: Watchable,
        val detailSections: List<ModelDetailSection> = emptyList(),
        val cached: Boolean = false
    ) : ContentDetailState()

    @Parcelize
    data class Error(val failure: Response.Failure) : ContentDetailState()
}

sealed class ContentDetailViewEffect : BaseViewEffect {
    data class ShowTvShowDetailScreen(val tvShowId: Int) : ContentDetailViewEffect()
    data class ShowMovieDetailScreen(val movieID: Int) : ContentDetailViewEffect()
    data class ShowPersonDetailScreen(val personId: Int) : ContentDetailViewEffect()
    data class PlayYoutubeVideo(val videoKey: String) : ContentDetailViewEffect()
    data class ShowSeasonDetailScreen(val seasonId: Int) : ContentDetailViewEffect()
}

//================================================================================
// Screen-specific view data/functions
//================================================================================

sealed class ContentType : Parcelable {
    @Parcelize object TvShow : ContentType()
    @Parcelize object Movie : ContentType()
}

//================================================================================
// Factory
//================================================================================

@PerView
class ContentDetailViewModelFactory(
    private val initialState: ContentDetailState?,
    private val getTvShowDetailUseCase: GetTvShowDetailUseCase,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getConfigUseCase: GetConfigUseCase,
    private val appStringProvider: AppStringProvider,
    private val schedulers: RxSchedulers
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T  {
        return ContentDetailViewModel(
            initialState,
            getTvShowDetailUseCase,
            getMovieDetailUseCase,
            getConfigUseCase,
            appStringProvider,
            schedulers) as T
    }

    companion object {
        const val NAME = "ContentDetailViewModelFactory"
    }
}