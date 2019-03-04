package com.atherton.upnext.presentation.common.detail

import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.presentation.util.AppStringProvider
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


internal fun buildMovieDetailSections(movie: Movie, appStringProvider: AppStringProvider): List<ModelDetailSection> {
    val sectionList: MutableList<ModelDetailSection> = ArrayList()

    val runtime: String? = movie.detail?.runtime?.let { appStringProvider.getRuntimeString(it) }
    //todo Also factor in user region when calling API - think there are different 'releases' for each region
    //val releaseDate: String? = formatFullDateForDetailScreen(movie.releaseDate)
    val releaseDate: String? = formatYearForDetailScreen(movie.releaseDate)
    sectionList.add(
        ModelDetailSection.RuntimeRelease(
            runtime = runtime,
            releaseDate = releaseDate,
            showDivider = runtime != null && releaseDate != null
        )
    )

    movie.overview?.let { sectionList.add(ModelDetailSection.Overview(it)) }
    movie.detail?.let {

        //todo add ratings
        //todo add seasons

        if (it.genres != null && it.genres.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Genres(it.genres.sortedBy { genre -> genre.name }))
        }
        if (it.videos != null && it.videos.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Videos(appStringProvider.getVideosHeader(), it.videos))
        }

        //todo add photos

        if (it.cast != null && it.cast.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Cast(appStringProvider.getCastHeader(), it.cast))
        }
        if (it.crew != null && it.crew.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Crew(appStringProvider.getCrewHeader(), it.crew))
        }
        if (it.similar != null && it.similar.isNotEmpty()) {
            sectionList.add(ModelDetailSection.SimilarItems(appStringProvider.getSimilarMoviesHeader(), it.similar))
        }

        //todo add reviews
        //todo add comments
        //todo add external links
    }
    return sectionList
}

private fun formatFullDateForDetailScreen(dateString: String?): String? {
    return if (dateString != null) {
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        try {
            val date: Date = oldFormat.parse(dateString)
            newFormat.format(date)
        } catch (exception: ParseException) {
            dateString
        }
    } else dateString
}

private fun formatYearForDetailScreen(dateString: String?): String? {
    return if (dateString != null && dateString.length > 4) {
        dateString.take(4)
    } else dateString
}
