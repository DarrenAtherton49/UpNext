package com.atherton.upnext.presentation.common.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import kotlinx.android.synthetic.main.item_detail_scrolling_item.*

class ModelDetailSimilarItemsAdapter(
    private val imageLoader: GlideRequests,
    private val onSimilarItemClickListener: (Movie) -> Unit //todo change to SearchModel so we can reuse adapter
) : ModelDetailAdapter.ScrollingChildAdapter<Movie, ModelDetailScrollingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_scrolling_item), imageLoader).apply {
            itemView.setOnClickListener {
                onSimilarItemClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val movie = getItem(position)

        imageLoader
            .load(movie.posterPath)
            .apply(UpNextAppGlideModule.searchModelPosterRequestOptions)
            .into(holder.photoImageView)

        holder.firstRowTextView.text = movie.title
        holder.secondRowTextView.isVisible = false
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<Movie>() {

            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
