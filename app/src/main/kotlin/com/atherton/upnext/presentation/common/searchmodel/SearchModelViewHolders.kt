package com.atherton.upnext.presentation.common.searchmodel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.TvShow
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
        searchModelRatingTextView.text = tvShow.voteAverage.toString()
    }
}

class MovieModelGridViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)
        searchModelRatingTextView.text = movie.voteAverage.toString()
    }
}

class PersonModelGridViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelGridViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
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
