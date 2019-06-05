package com.atherton.upnext.presentation.common.searchmodel

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class SearchModelAdapter(
    private val imageLoader: GlideRequests,
    private val onClickListener: (Searchable) -> Unit
) : ListAdapter<Searchable, SearchModelGridViewHolder>(SearchDiffCallback) {

    lateinit var viewMode: GridViewMode

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchModelGridViewHolder {
        return when (viewMode) {
            is GridViewMode.Grid -> {
                val view: View = parent.inflateLayout(R.layout.item_search_model_grid)
                when (viewType) {
                    TV_VIEW_TYPE -> TvShowModelGridViewHolder(view, imageLoader).withClickListener()
                    MOVIE_VIEW_TYPE -> MovieModelGridViewHolder(view, imageLoader).withClickListener()
                    PERSON_VIEW_TYPE -> PersonModelGridViewHolder(view, imageLoader).withClickListener()
                    else -> MovieModelGridViewHolder(view, imageLoader).withClickListener()
                }
            }
            is GridViewMode.List -> {
                val view: View = parent.inflateLayout(R.layout.item_search_model_list)
                when (viewType) {
                    TV_VIEW_TYPE -> TvShowModelListViewHolder(view, imageLoader).withClickListener()
                    MOVIE_VIEW_TYPE -> MovieModelListViewHolder(view, imageLoader).withClickListener()
                    PERSON_VIEW_TYPE -> PersonModelListViewHolder(view, imageLoader).withClickListener()
                    else -> MovieModelListViewHolder(view, imageLoader).withClickListener()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SearchModelGridViewHolder, position: Int) {
        when (holder) {
            is TvShowModelGridViewHolder -> holder.bind(getItem(position) as TvShow)
            is TvShowModelListViewHolder -> holder.bind(getItem(position) as TvShow)
            is MovieModelGridViewHolder -> holder.bind(getItem(position) as Movie)
            is MovieModelListViewHolder -> holder.bind(getItem(position) as Movie)
            is PersonModelGridViewHolder -> holder.bind(getItem(position) as Person)
            is PersonModelListViewHolder -> holder.bind(getItem(position) as Person)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TvShow -> TV_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
            is Person -> PERSON_VIEW_TYPE
            else -> TV_VIEW_TYPE
        }
    }

    private fun SearchModelGridViewHolder.withClickListener(): SearchModelGridViewHolder = this.apply {
        itemView.setOnClickListener { onClickListener.invoke(getItem(adapterPosition)) }
    }

    override fun onViewRecycled(holder: SearchModelGridViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    companion object {
        private const val TV_VIEW_TYPE = 0
        private const val MOVIE_VIEW_TYPE = 1
        private const val PERSON_VIEW_TYPE = 2

        private object SearchDiffCallback : DiffUtil.ItemCallback<Searchable>() {

            // TMDB ids are not globally unique - only unique per type (e.g. movie)
            override fun areItemsTheSame(oldItem: Searchable, newItem: Searchable): Boolean {
                return oldItem.tmdbId == newItem.tmdbId && oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: Searchable, newItem: Searchable): Boolean {
                return oldItem == newItem
            }
        }
    }
}
