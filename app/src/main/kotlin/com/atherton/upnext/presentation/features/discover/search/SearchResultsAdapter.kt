package com.atherton.upnext.presentation.features.discover.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.model.TvShow
import com.atherton.upnext.util.extensions.inflateLayout

class SearchResultsAdapter(
    private val onClickListener: () -> Unit //todo use this
) : ListAdapter<SearchModel, SearchResultViewHolder>(SearchResultsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return when (viewType) {
            TV_VIEW_TYPE -> TvShowSearchResultViewHolder(parent.inflateLayout(R.layout.item_tv_search_result))
            MOVIE_VIEW_TYPE -> MovieSearchResultViewHolder(parent.inflateLayout(R.layout.item_movie_search_result))
            PERSON_VIEW_TYPE -> PersonSearchResultViewHolder(parent.inflateLayout(R.layout.item_person_search_result))
            else -> MovieSearchResultViewHolder(parent.inflateLayout(R.layout.item_movie_search_result))
        }
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        when (holder) {
            is TvShowSearchResultViewHolder -> holder.bind(getItem(position) as TvShow)
            is MovieSearchResultViewHolder -> holder.bind(getItem(position) as Movie)
            is PersonSearchResultViewHolder -> holder.bind(getItem(position) as Person)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TvShow -> TV_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
            is Person -> PERSON_VIEW_TYPE
        }
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
