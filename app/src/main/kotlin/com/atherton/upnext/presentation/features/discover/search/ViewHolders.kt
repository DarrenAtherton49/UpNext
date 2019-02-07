package com.atherton.upnext.presentation.features.discover.search

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
import kotlinx.android.synthetic.main.item_search_result.*



sealed class SearchResultViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    //todo bind placeholder and error
    private val requestOptions: RequestOptions by lazy {
        RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(20))
            .placeholder(R.drawable.ic_image_white_24dp)
            .error(R.drawable.ic_broken_image_white_24dp)
    }

    fun bind(title: String?, imageUrl: String?) {
        titleTextView.text = title
        imageLoader.load(imageUrl).apply(requestOptions).into(searchResultImageView)
    }

    fun clear() {
        imageLoader.clear(searchResultImageView)
    }
}

class TvShowSearchResultViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchResultViewHolder(containerView, imageLoader) {

    fun bind(tvShow: TvShow) {
        super.bind(tvShow.name, tvShow.posterPath)
    }
}

class MovieSearchResultViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchResultViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)
    }
}

class PersonSearchResultViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : SearchResultViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
    }
}
