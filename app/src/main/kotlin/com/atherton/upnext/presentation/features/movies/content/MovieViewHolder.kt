package com.atherton.upnext.presentation.features.movies.content

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.extensions.setTextOrHide
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import com.google.android.material.chip.Chip
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

        if (movieListItem.genres != null && movieListItem.genres.isNotEmpty()) {
            genreChipHorizontalScrollView.isVisible = true
            movieListItem.genres.forEach { genre ->
                genre.name?.let { name ->
                    val chip = Chip(containerView.context).apply {
                        text = name
                        isClickable = false
                        isCheckable = false
                    }
                    genreChipGroup.addView(chip as View)
                }
            }
        } else {
            genreChipHorizontalScrollView.isVisible = false
        }
    }

    fun clear() {
        imageLoader.clear(movieImageView)
    }
}
