package com.atherton.upnext.data.repository

import com.atherton.upnext.data.db.dao.TvShowDao
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_AIRING_TODAY
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_ON_THE_AIR
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_POPULAR
import com.atherton.upnext.data.db.dao.TvShowDao.Companion.PLAYLIST_TOP_RATED
import com.atherton.upnext.data.db.model.tv.RoomTvShow
import com.atherton.upnext.data.db.model.tv.RoomTvShowAllData
import com.atherton.upnext.data.mapper.*
import com.atherton.upnext.data.network.model.NetworkResponse
import com.atherton.upnext.data.network.model.TmdbTvShow
import com.atherton.upnext.data.network.service.TmdbTvShowService
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.domain.model.LceResponse
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.domain.repository.TvShowRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CachingTvShowRepository @Inject constructor(
    private val tvShowDao: TvShowDao,
    private val tvShowService: TmdbTvShowService
) : TvShowRepository {

    //todo modify this to check if show is in database and if it is still valid (time based?)
    //todo modify this call to add a 'forceRefresh' parameter (e.g. in case of pull-to-refresh)
    override fun getTvShow(id: Long): Observable<LceResponse<TvShow>> {
        return tvShowDao.getTvShowListForIdSingle(id)
            .toObservable()
            .flatMap { tvShowList ->
                if (tvShowList.isNotEmpty() && tvShowList[0].isModelComplete) {
                    val tvShow: TvShow? = getTvShowFromDatabase(id)
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
                            networkResponse.toDomainLceResponse(data = getTvShowFromDatabase(id))
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

    override fun getTvShowLists(): Observable<LceResponse<List<ContentList>>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggleTvShowWatchlistStatus(tvShowId: Long): Observable<LceResponse<TvShow>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggleTvShowListStatus(tvShowId: Long, listId: Long): Observable<LceResponse<TvShow>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    private fun getTvShowFromDatabase(id: Long): TvShow? {
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
