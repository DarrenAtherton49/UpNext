package com.atherton.upnext.presentation.features.movies.content

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.atherton.upnext.R
import com.atherton.upnext.util.extension.inflateLayout
import com.atherton.upnext.presentation.util.glide.GlideRequests
import kotlinx.android.synthetic.main.item_movie.view.*

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class MovieListAdapter(
    private val imageLoader: GlideRequests,
    private val onItemClickListener: (MovieListItem) -> Unit,
    private val onWatchlistButtonClickListener: (MovieListItem) -> Unit,
    private val onWatchedButtonClickListener: (MovieListItem) -> Unit,
    private val onAddToListClickListener: (MovieListItem) -> Unit
) : ListAdapter<MovieListItem, MovieViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_movie)
        return MovieViewHolder(view, imageLoader).withClickListeners()
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun MovieViewHolder.withClickListeners(): MovieViewHolder = this.apply {
        itemView.setOnClickListener { onItemClickListener.invoke(getItem(adapterPosition)) }
        itemView.watchlistButton.setOnClickListener { onWatchlistButtonClickListener.invoke(getItem(adapterPosition)) }
        itemView.watchedButton.setOnClickListener { onWatchedButtonClickListener.invoke(getItem(adapterPosition)) }
        itemView.addToListButton.setOnClickListener { onAddToListClickListener.invoke(getItem(adapterPosition)) }
    }

    companion object {

        private object MovieDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {

            override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
                return oldItem.movieId == newItem.movieId
            }

            override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
