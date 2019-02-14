package com.atherton.upnext.presentation.common

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
    private val viewMode: SearchModelViewMode,
    private val onClickListener: (SearchModel) -> Unit
) : ListAdapter<SearchModel, SearchModelViewHolder>(SearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchModelViewHolder {
        val view: View = when (viewMode) {
            is SearchModelViewMode.Grid -> parent.inflateLayout(R.layout.item_search_model_grid)
            is SearchModelViewMode.List -> parent.inflateLayout(R.layout.item_search_model_list)
        }
        return when (viewType) {
            TV_VIEW_TYPE -> TvShowModelViewHolder(view, imageLoader).withClickListener()
            MOVIE_VIEW_TYPE -> MovieModelViewHolder(view, imageLoader).withClickListener()
            PERSON_VIEW_TYPE -> PersonModelViewHolder(view, imageLoader).withClickListener()
            else -> MovieModelViewHolder(view, imageLoader).withClickListener()
        }
    }

    override fun onBindViewHolder(holder: SearchModelViewHolder, position: Int) {
        when (holder) {
            is TvShowModelViewHolder -> holder.bind(getItem(position) as TvShow)
            is MovieModelViewHolder -> holder.bind(getItem(position) as Movie)
            is PersonModelViewHolder -> holder.bind(getItem(position) as Person)
        }
    }

    private fun SearchModelViewHolder.withClickListener(): SearchModelViewHolder = this.apply {
        itemView.setOnClickListener { onClickListener.invoke(getItem(adapterPosition)) }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TvShow -> TV_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
            is Person -> PERSON_VIEW_TYPE
        }
    }

    override fun onViewRecycled(holder: SearchModelViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    companion object {
        private const val TV_VIEW_TYPE = 0
        private const val MOVIE_VIEW_TYPE = 1
        private const val PERSON_VIEW_TYPE = 2

        private object SearchDiffCallback : DiffUtil.ItemCallback<SearchModel>() {

            // TMDB ids are not globally unique - only unique per type (e.g. movie)
            override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem.id == newItem.id && oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem == newItem
            }
        }

    }
}
