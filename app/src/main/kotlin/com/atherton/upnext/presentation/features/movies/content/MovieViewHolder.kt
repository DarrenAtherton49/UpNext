package com.atherton.upnext.presentation.features.movies.content

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.util.extension.setTextOrHide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie.*

class MovieViewHolder(
    override val containerView: View,
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(movieListItem: MovieListItem) {

        imageLoader.loadMovieListItem(
            with = glideRequests,
            url = movieListItem.posterPath,
            into = movieImageView,
            onSuccess = { colorCombination ->
                movieCardView.setCardBackgroundColor(colorCombination.backgroundColor)
            }
        )

        ratingTextView.setTextOrHide(movieListItem.voteAverage)

        imageLoader.load(with = glideRequests, drawableResId = movieListItem.watchlistButtonResId, into = watchlistButton)
        imageLoader.load(with = glideRequests, drawableResId = movieListItem.watchedButtonResId, into = watchedButton)
        imageLoader.load(with = glideRequests, drawableResId = movieListItem.addToListButtonResId, into = addToListButton)

        releaseDateTextView.setTextOrHide(movieListItem.releaseDate)
        titleTextView.setTextOrHide(movieListItem.title)
        genresTextView.setTextOrHide(movieListItem.genresString)
        runtimeTextView.setTextOrHide(movieListItem.runtime)
    }

    fun clear() {
        imageLoader.clear(with = glideRequests, imageView = movieImageView)
    }
}
