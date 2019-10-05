package com.atherton.upnext.presentation.features.shows.content

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.util.extension.inflateLayout
import kotlinx.android.synthetic.main.item_show.view.*

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class ShowListAdapter(
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests,
    private val onItemClickListener: (ShowListItem) -> Unit,
    private val onWatchlistButtonClickListener: (ShowListItem) -> Unit,
    private val onWatchedButtonClickListener: (ShowListItem) -> Unit,
    private val onAddToListClickListener: (ShowListItem) -> Unit
) : ListAdapter<ShowListItem, ShowViewHolder>(ShowDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_show)
        return ShowViewHolder(view, imageLoader, glideRequests).withClickListeners()
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun ShowViewHolder.withClickListeners(): ShowViewHolder = this.apply {
        itemView.setOnClickListener { onItemClickListener.invoke(getItem(adapterPosition)) }
        itemView.watchlistButton.setOnClickListener { onWatchlistButtonClickListener.invoke(getItem(adapterPosition)) }
        itemView.watchedButton.setOnClickListener { onWatchedButtonClickListener.invoke(getItem(adapterPosition)) }
        itemView.addToListButton.setOnClickListener { onAddToListClickListener.invoke(getItem(adapterPosition)) }
    }

    companion object {

        private object ShowDiffCallback : DiffUtil.ItemCallback<ShowListItem>() {

            override fun areItemsTheSame(oldItem: ShowListItem, newItem: ShowListItem): Boolean {
                return oldItem.showId == newItem.showId
            }

            override fun areContentsTheSame(oldItem: ShowListItem, newItem: ShowListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
