package com.atherton.upnext.presentation.common.searchmodel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.presentation.features.content.formatVoteAverage
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_search_model_grid.*

sealed class SearchModelGridViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(title: String?, imageUrl: String?) {
        searchModelTitleTextView.text = title
        imageLoader.load(imageUrl).apply(UpNextAppGlideModule.searchModelGridRequestOptions).into(searchModelImageView)
    }

    fun clear() {
        imageLoader.clear(searchModelImageView)
    }
}

class TvShowModelGridViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(tvShow: TvShow) {
        super.bind(tvShow.name, tvShow.posterPath)

        val voteAverage = formatVoteAverage(tvShow.voteAverage)
        if (voteAverage != null) {
            searchModelRatingTextView.text = voteAverage
            searchModelRatingTextView.isVisible = true
        } else {
            searchModelRatingTextView.isVisible = false
        }
    }
}

class MovieModelGridViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)

        val voteAverage = formatVoteAverage(movie.voteAverage)
        if (voteAverage != null) {
            searchModelRatingTextView.text = voteAverage
            searchModelRatingTextView.isVisible = true
        } else {
            searchModelRatingTextView.isVisible = false
        }
    }
}

class PersonModelGridViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
        searchModelRatingTextView.isVisible = false
    }
}

sealed class SearchModelListViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(title: String?, imageUrl: String?) {
        searchModelTitleTextView.text = title
        imageLoader.load(imageUrl).apply(UpNextAppGlideModule.searchModelGridRequestOptions).into(searchModelImageView)
    }

    fun clear() {
        imageLoader.clear(searchModelImageView)
    }
}

class TvShowModelListViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(tvShow: TvShow) {
        super.bind(tvShow.name, tvShow.posterPath)
    }
}

class MovieModelListViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)
    }
}

class PersonModelListViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
    }
}
