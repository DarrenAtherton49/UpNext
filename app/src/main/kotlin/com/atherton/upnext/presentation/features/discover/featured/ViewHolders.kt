package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_discover_result.*
import kotlinx.android.synthetic.main.item_discover_carousel_section.*
import kotlinx.android.synthetic.main.item_discover_carousel_section.view.*

class DiscoverSectionViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(section: DiscoverCarouselSection, childAdapter: DiscoverChildAdapter?, childAdapterState: Parcelable?) {
        sectionTitleTextView.text = section.title
        childRecyclerView.adapter = childAdapter
        childAdapter?.submitList(section.data)
        childRecyclerView.layoutManager?.onRestoreInstanceState(childAdapterState)
    }

    companion object Factory {
        fun create(
            itemView: View,
            recycledViewPool: RecyclerView.RecycledViewPool,
            itemSpacingPx: Int
        ): DiscoverSectionViewHolder {
            val holder = DiscoverSectionViewHolder(itemView)

            // The line below ensures that child RecyclerView does not capture vertical scrolls,
            // as we want the outer RecyclerView to handle those instead.
            itemView.childRecyclerView.isNestedScrollingEnabled = false
            itemView.childRecyclerView.setHasFixedSize(true)
            itemView.childRecyclerView.addItemDecoration(
                LinearSpacingItemDecoration(itemSpacingPx, LinearSpacingItemDecoration.Orientation.Horizontal)
            )

            val layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false).apply {
                initialPrefetchItemCount = ITEM_PREFETCH_COUNT //todo set based on orientation
            }
            itemView.childRecyclerView.layoutManager = layoutManager
            itemView.childRecyclerView.setRecycledViewPool(recycledViewPool)
            return holder
        }

        private const val ITEM_PREFETCH_COUNT = 5
    }
}

sealed class DiscoverChildViewHolder(
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
        discoverTitleTextView.text = title
        imageLoader.load(imageUrl).apply(requestOptions).into(discoverItemImageView)
    }

    fun clear() {
        imageLoader.clear(discoverItemImageView)
    }
}

class TvShowDiscoverViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : DiscoverChildViewHolder(containerView, imageLoader) {

    fun bind(tvShow: TvShow) {
        super.bind(tvShow.name, tvShow.posterPath)
    }
}

class MovieDiscoverViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : DiscoverChildViewHolder(containerView, imageLoader) {

    fun bind(movie: Movie) {
        super.bind(movie.title, movie.posterPath)
    }
}

class PersonDiscoverViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : DiscoverChildViewHolder(containerView, imageLoader) {

    fun bind(person: Person) {
        super.bind(person.name, person.profilePath)
    }
}
