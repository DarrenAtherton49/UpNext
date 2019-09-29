package com.atherton.upnext.presentation.common.searchmodel

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.util.extension.inflateLayout
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class SearchModelAdapter(
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests,
    private val onClickListener: (Searchable) -> Unit
) : ListAdapter<Searchable, SearchModelGridViewHolder>(SearchDiffCallback) {

    lateinit var viewMode: GridViewMode

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchModelGridViewHolder {
        return when (viewMode) {
            is GridViewMode.Grid -> {
                val view: View = parent.inflateLayout(R.layout.item_search_model_grid)
                when (viewType) {
                    TV_VIEW_TYPE -> TvShowModelGridViewHolder(view, imageLoader, glideRequests).withClickListener()
                    MOVIE_VIEW_TYPE -> MovieModelGridViewHolder(view, imageLoader, glideRequests).withClickListener()
                    PERSON_VIEW_TYPE -> PersonModelGridViewHolder(view, imageLoader, glideRequests).withClickListener()
                    else -> MovieModelGridViewHolder(view, imageLoader, glideRequests).withClickListener()
                }
            }
            is GridViewMode.List -> {
                val view: View = parent.inflateLayout(R.layout.item_search_model_list)
                when (viewType) {
                    TV_VIEW_TYPE -> TvShowModelListViewHolder(view, imageLoader, glideRequests).withClickListener()
                    MOVIE_VIEW_TYPE -> MovieModelListViewHolder(view, imageLoader, glideRequests).withClickListener()
                    PERSON_VIEW_TYPE -> PersonModelListViewHolder(view, imageLoader, glideRequests).withClickListener()
                    else -> MovieModelListViewHolder(view, imageLoader, glideRequests).withClickListener()
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
                return oldItem.id == newItem.id && oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: Searchable, newItem: Searchable): Boolean {
                return oldItem == newItem
            }
        }
    }
}
