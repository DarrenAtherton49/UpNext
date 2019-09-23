package com.atherton.upnext.presentation.features.content.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.util.extension.inflateLayout
import com.atherton.upnext.util.extension.isVisible
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.glide.UpNextAppGlideModule
import kotlinx.android.synthetic.main.item_detail_scrolling_item.*

class ModelDetailRecommendedItemsAdapter(
    private val imageLoader: GlideRequests,
    private val onRecommendedItemClickListener: (Watchable) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<Watchable, ModelDetailScrollingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_scrolling_item), imageLoader).apply {
            itemView.setOnClickListener {
                onRecommendedItemClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val watchable: Watchable = getItem(position)

        imageLoader
            .load(watchable.posterPath)
            .apply(UpNextAppGlideModule.searchModelPosterRequestOptions)
            .into(holder.photoImageView)

        holder.firstRowTextView.text = watchable.title
        holder.secondRowTextView.isVisible = false
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<Watchable>() {

            override fun areItemsTheSame(oldItem: Watchable, newItem: Watchable): Boolean {
                return oldItem.id == newItem.id && oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: Watchable, newItem: Watchable): Boolean {
                return oldItem.title == newItem.title && oldItem.posterPath == newItem.posterPath
            }
        }
    }
}
