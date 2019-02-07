package com.atherton.upnext.presentation.features.discover.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class SearchResultsAdapter(
    private val imageLoader: GlideRequests,
    private val onClickListener: (SearchModel) -> Unit
) : ListAdapter<SearchModel, SearchResultViewHolder>(SearchResultsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_search_result)
        return when (viewType) {
            TV_VIEW_TYPE -> TvShowSearchResultViewHolder(view, imageLoader).withClickListener()
            MOVIE_VIEW_TYPE -> MovieSearchResultViewHolder(view, imageLoader).withClickListener()
            PERSON_VIEW_TYPE -> PersonSearchResultViewHolder(view, imageLoader).withClickListener()
            else -> MovieSearchResultViewHolder(view, imageLoader).withClickListener()
        }
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        when (holder) {
            is TvShowSearchResultViewHolder -> holder.bind(getItem(position) as TvShow)
            is MovieSearchResultViewHolder -> holder.bind(getItem(position) as Movie)
            is PersonSearchResultViewHolder -> holder.bind(getItem(position) as Person)
        }
    }

    private fun SearchResultViewHolder.withClickListener(): SearchResultViewHolder = this.apply {
        itemView.setOnClickListener { onClickListener.invoke(getItem(adapterPosition)) }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TvShow -> TV_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
            is Person -> PERSON_VIEW_TYPE
        }
    }

    override fun onViewRecycled(holder: SearchResultViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    companion object {
        private const val TV_VIEW_TYPE = 0
        private const val MOVIE_VIEW_TYPE = 1
        private const val PERSON_VIEW_TYPE = 2
    }
}

private class SearchResultsDiffCallback : DiffUtil.ItemCallback<SearchModel>() {

    // TMDB ids are not globally unique - only unique per type (e.g. movie)
    override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
        return oldItem.id == newItem.id && oldItem::class == newItem::class
    }

    override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
        return oldItem == newItem
    }
}
