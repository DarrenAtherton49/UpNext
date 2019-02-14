package com.atherton.upnext.presentation.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.util.glide.GlideRequests
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_search_model_grid.*

sealed class SearchModelViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    private val requestOptions: RequestOptions by lazy {
        RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(20))
            .error(R.drawable.ic_broken_image_white_24dp)
    }

    fun bind(title: String?, imageUrl: String?) {
        searchModelTitleTextView.text = title
        imageLoader.load(imageUrl).apply(requestOptions).into(searchModelImageView)
    }

    fun clear() {
        imageLoader.clear(searchModelImageView)
    }
}

class TvShowModelViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelViewHolder(containerView, imageLoader) {

    fun bind(tvShow: TvShow) {
        super.bind(tvShow.name, tvShow.posterPath)
    }
}

class MovieModelViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)
    }
}

class PersonModelViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchModelViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
    }
}
