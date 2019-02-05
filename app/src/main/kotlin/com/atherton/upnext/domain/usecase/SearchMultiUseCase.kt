package com.atherton.upnext.domain.usecase

import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.data.repository.movies.MoviesRepository
import com.atherton.upnext.data.repository.search.SearchRepository
import io.reactivex.Single
import javax.inject.Inject

class SearchMultiUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val moviesRepository: MoviesRepository
) {

    fun build(query: String): Single<Response<List<SearchModel>>> {
        return if (query.isBlank()) { // a list of popular items
            Single.just(Response.Success(listOf(
                Person(false, 1, listOf(), "popularGuy", 1f, "")), true)
            )
        } else {
            searchRepository.searchMulti(query)
        }
    }
}
