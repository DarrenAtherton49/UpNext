package com.atherton.upnext.presentation.common.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_similar_item.*

class ModelDetailSimilarItemsAdapter(
    private val imageLoader: GlideRequests,
    private val onSimilarItemClickListener: (Movie) -> Unit //todo change to SearchModel so we can reuse adapter
) : ModelDetailAdapter.ScrollingChildAdapter<Movie, ModelDetailSimilarItemsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateLayout(R.layout.item_detail_similar_item), imageLoader).apply {
            itemView.setOnClickListener {
                onSimilarItemClickListener(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(override val containerView: View, private val imageLoader: GlideRequests)
        : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(movie: Movie) {
            imageLoader
                .load(movie.posterPath)
                .apply(UpNextAppGlideModule.searchModelPosterRequestOptions)
                .into(similarItemImageView)

            similarItemTitleTextView.text = movie.title
        }
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
