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
    sectionList.add(ModelDetailSection.RuntimeRelease(runtime, releaseDate, showDivider = runtime != null && releaseDate != null))

    movie.overview?.let { sectionList.add(ModelDetailSection.Overview(it)) }
    movie.detail?.genres?.let { genres ->
        if (genres.isNotEmpty()) {
            sectionList.add(ModelDetailSection.Genres(genres.sortedBy { it.name }))
        }
    }
    //todo add ratings
    //todo add seasons
    //todo add trailers
    //todo add photos
    //todo add cast
    //todo add crew
    //todo add reviews
    //todo add comments
    //todo add similar items
    //todo add external links

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
