package com.atherton.upnext.presentation.features.movies.content

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.extensions.setTextOrHide
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie.*

class MovieViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(movieListItem: MovieListItem) {
        imageLoader.load(movieListItem.posterPath).apply(UpNextAppGlideModule.movieListRequestOptions).into(movieImageView)
        ratingTextView.setTextOrHide(movieListItem.voteAverage)

        titleAndReleaseDateTextView.setTextOrHide(movieListItem.titleAndReleaseDate)
        runtimeTextView.setTextOrHide(movieListItem.runtime)

        genresTextView.setTextOrHide(movieListItem.genresString)
    }

    fun clear() {
        imageLoader.clear(movieImageView)
    }
}
