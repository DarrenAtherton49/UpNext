package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.ListDao
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_ID_TV_WATCHED
import com.atherton.upnext.data.db.dao.ListDao.Companion.LIST_ID_TV_WATCHLIST
import com.atherton.upnext.data.db.dao.TvShowDao
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_AIRING_TODAY
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_ON_THE_AIR
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_POPULAR
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_TOP_RATED
import com.atherton.upnext.data.db.model.list.RoomTvShowList
import com.atherton.upnext.data.db.model.tv.RoomTvShow
import com.atherton.upnext.data.db.model.tv.RoomTvShowAllData
import com.atherton.upnext.data.mapper.*
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbTvShow
import com.atherton.upnext.data.network.service.TmdbTvShowService
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.ContentListStatus
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingTvShowRepository @Inject constructor(
    private val tvShowDao: TvShowDao,
    private val listDao: ListDao,
    private val tvShowService: TmdbTvShowService
) : TvShowRepository {

    //todo modify this to check if show is in database and if it is still valid (time based?)
    //todo modify this call to add a 'forceRefresh' parameter (e.g. in case of pull-to-refresh)
    override fun getTvShow(id: Long): Observable<LceResponse<TvShow>> {
        return tvShowDao.getTvShowListForIdObservable(id)
            .distinctUntilChanged()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty() && tvShowList[0].isModelComplete) {
                    val tvShow: TvShow? = getFullShowFromDatabase(id)
                    Observable.fromCallable {
                        if (tvShow != null) {
                            LceResponse.Content(data = tvShow) // fetch the full tv show and all relations
                        } else {
                            throw IllegalStateException("TV Show should be in database - check query")
                        }
                    }
                } else {
                    tvShowService.getTvShowDetails(id)
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkTvShow: TmdbTvShow = networkResponse.body
                                saveFullTvShowToDatabase(networkTvShow)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getFullShowFromDatabase(id))
                        }
                }
            }
    }

    //todo modify this to check if shows are are still valid (time based?)
    override fun getPopular(): Observable<LceResponse<List<TvShow>>> {
        return tvShowDao.getTvShowsForPlaylistSingle(PLAYLIST_POPULAR)
            .toObservable()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = tvShowList.map { it.toDomainTvShow() })
                    }
                } else {
                    tvShowService.getPopular()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkPopularTvShows: List<TmdbTvShow> = networkResponse.body.results
                                saveTvShowsForPlaylist(networkPopularTvShows, PLAYLIST_POPULAR)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getTvShowsForPlaylist(PLAYLIST_POPULAR))
                        }
                }
            }
    }

    //todo modify this to check if shows are are still valid (time based?)
    override fun getTopRated(): Observable<LceResponse<List<TvShow>>> {
        return tvShowDao.getTvShowsForPlaylistSingle(PLAYLIST_TOP_RATED)
            .toObservable()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = tvShowList.map { it.toDomainTvShow() })
                    }
                } else {
                    tvShowService.getTopRated()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkTopRatedTvShows: List<TmdbTvShow> = networkResponse.body.results
                                saveTvShowsForPlaylist(networkTopRatedTvShows, PLAYLIST_TOP_RATED)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getTvShowsForPlaylist(PLAYLIST_TOP_RATED))
                        }
                }
            }
    }

    //todo modify this to check if shows are are still valid (time based?)
    override fun getAiringToday(): Observable<LceResponse<List<TvShow>>> {
        return tvShowDao.getTvShowsForPlaylistSingle(PLAYLIST_AIRING_TODAY)
            .toObservable()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = tvShowList.map { it.toDomainTvShow() })
                    }
                } else {
                    tvShowService.getAiringToday()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkAiringTodayTvShows: List<TmdbTvShow> = networkResponse.body.results
                                saveTvShowsForPlaylist(networkAiringTodayTvShows, PLAYLIST_AIRING_TODAY)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getTvShowsForPlaylist(PLAYLIST_AIRING_TODAY))
                        }
                }
            }
    }

    //todo modify this to check if shows are are still valid (time based?)
    override fun getOnTheAir(): Observable<LceResponse<List<TvShow>>> {
        return tvShowDao.getTvShowsForPlaylistSingle(PLAYLIST_ON_THE_AIR)
            .toObservable()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty()) {
                    Observable.fromCallable {
                        LceResponse.Content(data = tvShowList.map { it.toDomainTvShow() })
                    }
                } else {
                    tvShowService.getOnTheAir()
                        .toObservable()
                        .doOnNext { networkResponse ->
                            if (networkResponse is NetworkResponse.Success) {
                                val networkOnTheAirTvShows: List<TmdbTvShow> = networkResponse.body.results
                                saveTvShowsForPlaylist(networkOnTheAirTvShows, PLAYLIST_ON_THE_AIR)
                            }
                        }
                        .map { networkResponse ->
                            networkResponse.toDomainLceResponse(data = getTvShowsForPlaylist(PLAYLIST_ON_THE_AIR))
                        }
                }
            }
    }

    override fun getTvShowListsForTvShow(showId: Long): Observable<LceResponse<List<ContentListStatus>>> {
        return listDao.getTvShowListsObservable()
            .map { allLists ->
                val listsForShow: List<RoomTvShowList> = listDao.getListsForTvShow(showId = showId)
                if (allLists.isNotEmpty()) {
                    val data = allLists.map { roomTvShowList ->
                        val listContainsMovie: Boolean = listsForShow.contains(roomTvShowList)
                        roomTvShowList.toDomainContentListStatus(showId, listContainsMovie)
                    }
                    LceResponse.Content(data = data)
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun getTvShowLists(): Observable<LceResponse<List<ContentList>>> {
        return listDao.getTvShowListsObservable()
            .map { showLists ->
                if (showLists.isNotEmpty()) {
                    LceResponse.Content(data = showLists.toDomainTvShowLists())
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun getTvShowsForList(listId: Long): Observable<LceResponse<List<TvShow>>> {
        return listDao.getTvShowsForListObservable(listId)
            .distinctUntilChanged()
            .map { showDataList ->
                val domainsShows = showDataList.toDomainShows()
                if (domainsShows.isNotEmpty()) {
                    LceResponse.Content(data = domainsShows)
                } else {
                    LceResponse.Content(data = emptyList())
                }
            }
    }

    override fun toggleTvShowWatchlistStatus(tvShowId: Long): Observable<LceResponse<TvShow>> {
        return toggleTvShowListStatus(tvShowId, LIST_ID_TV_WATCHLIST)
    }

    override fun toggleTvShowWatchedStatus(tvShowId: Long): Observable<LceResponse<TvShow>> {
        return toggleTvShowListStatus(tvShowId, LIST_ID_TV_WATCHED)
    }

    override fun toggleTvShowListStatus(tvShowId: Long, listId: Long): Observable<LceResponse<TvShow>> {
        return tvShowDao.getTvShowForIdSingle(tvShowId)
            .toObservable()
            .flatMap { showList ->
                if (showList.isNotEmpty()) {
                    when (listId) {
                        LIST_ID_TV_WATCHLIST -> { // toggle watchlist state on show object too
                            val dbShow: RoomTvShow = showList[0]
                            val newWatchlistState: Boolean = !dbShow.state.inWatchlist
                            val updatedShow = dbShow.copy(state = dbShow.state.copy(inWatchlist = newWatchlistState))
                            tvShowDao.updateShowAndToggleListStatus(updatedShow, listId)
                        }
                        LIST_ID_TV_WATCHED -> { // toggle watched state on show object too
                            val dbShow: RoomTvShow = showList[0]
                            val newWatchedState: Boolean = !dbShow.state.isWatched
                            val updatedShow = dbShow.copy(state = dbShow.state.copy(isWatched = newWatchedState))
                            tvShowDao.updateShowAndToggleListStatus(updatedShow, listId)

                            //todo need to update every episode where show id == tvShowId to set the episode to watched

                        }
                        //todo move this to the top of the function so we don't have to fetch movie when we don't need it here
                        else -> tvShowDao.toggleTvShowListStatus(tvShowId, listId)
                    }

                    // return full show
                    val domainShow: TvShow? = getFullShowFromDatabase(tvShowId)
                    if (domainShow != null) {
                        Observable.fromCallable { LceResponse.Content(domainShow) }
                    } else {
                        throw IllegalStateException("Show should be in database before toggling watchlist status.")
                    }
                }  else {
                    val errorMessage = "Cannot toggle show list status for list id $listId if " +
                        "movie is not in database. If trying to add a movie on search screen to " +
                        "a list, we must first convert search results to be saved in the movie " +
                        "table instead of just it's own table."
                    throw IllegalStateException(errorMessage)
                }
            }
    }

    override fun createTvShowList(tvShowId: Long?, listTitle: String): Observable<LceResponse<Long>> {
        return listDao.getHighestTvShowListOrderSingle()
            .flatMapObservable { currentHighestOrder ->
                val tvShowList = RoomTvShowList(name = listTitle, sortOrder = currentHighestOrder + 1)
                val listId: Long = listDao.insertTvShowList(tvShowList)

                if (tvShowId != null) {
                    tvShowDao.toggleTvShowListStatus(tvShowId, listId)
                }

                Observable.fromCallable { LceResponse.Content(listId) }
            }
    }

    private fun saveFullTvShowToDatabase(tvShow: TmdbTvShow) {
        val tvShowId: Long = tvShow.id.toLong()
        tvShowDao.insertFullTvShowData(
            tvShow = tvShow.toRoomTvShow(true),
            genres = tvShow.genres?.toRoomTvShowGenres(tvShowId),
            productionCompanies = tvShow.productionCompanies?.toRoomTvProductionCompanies(tvShowId),
            castMembers = tvShow.credits?.cast?.toRoomTvShowCast(tvShowId),
            crewMembers = tvShow.credits?.crew?.toRoomTvShowCrew(tvShowId),
            createdBy = tvShow.createdBy?.toRoomTvShowCreatedBy(tvShowId),
            networks = tvShow.networks?.toRoomTvNetworks(tvShowId),
            seasons = tvShow.seasons?.toRoomTvSeasons(tvShowId),
            recommendations = tvShow.recommendations?.results?.toRoomTvShows(false),
            videos = tvShow.videos?.results?.toRoomTvShowVideos(tvShowId)
        )
    }

    private fun getFullShowFromDatabase(id: Long): TvShow? {
        val dbTvShowData: RoomTvShowAllData? = tvShowDao.getFullTvShowForId(id)
        return if (dbTvShowData != null) {
            val tvShow: RoomTvShow? = dbTvShowData.tvShow
            if (tvShow != null) {
                val recommendations: List<RoomTvShow> = tvShowDao.getRecommendationsForTvShow(tvShow.id)
                return dbTvShowData.toDomainTvShow(recommendations)
            } else null
        } else null
    }

    private fun getTvShowsForPlaylist(playlistName: String): List<TvShow> {
        val dbTvShowsData: List<RoomTvShow> = tvShowDao.getTvShowsForPlaylist(playlistName)
        return dbTvShowsData.map { it.toDomainTvShow() }
    }

    private fun saveTvShowsForPlaylist(networkTvShows: List<TmdbTvShow>, playlistName: String) {
        val dbTvShows = networkTvShows.toRoomTvShows(false)
        tvShowDao.insertAllTvShowsForPlaylist(dbTvShows, playlistName)
    }
}
