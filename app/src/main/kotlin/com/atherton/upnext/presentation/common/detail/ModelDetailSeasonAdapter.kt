package com.atherton.upnext.presentation.common.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Season
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.synthetic.main.item_detail_season_item.*

class ModelDetailSeasonAdapter(
    private val imageLoader: GlideRequests,
    private val onSeasonClickListener: (Season) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<Season, ModelDetailScrollingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_season_item), imageLoader).apply {
            itemView.setOnClickListener {
                onSeasonClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val season = getItem(position)

        holder.firstRowTextView.text = season.name
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<Season>() {

            override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean {
                return oldItem == newItem
            }
        }
    }
}
