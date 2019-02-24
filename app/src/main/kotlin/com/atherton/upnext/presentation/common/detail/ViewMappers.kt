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
    val releaseDate: String? = formatDateForDetailScreen(movie.releaseDate)
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
        //todo add trailers
        //todo add photos



        if (it.genres != null && it.genres.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Genres(it.genres.sortedBy { genre -> genre.name }))
        }
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

private fun formatDateForDetailScreen(dateString: String?): String? {
    return if (dateString != null) {
        val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            val date: Date = oldFormat.parse(dateString)
            newFormat.format(date)
        } catch (exception: ParseException) {
            dateString
        }
    } else dateString
}
