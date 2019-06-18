package com.atherton.upnext.presentation.features.movies.content

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.extensions.LayoutContainer

class MovieViewHolder(
    override val containerView: View,
    private val imageLoader: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(movie: Movie) {
        //todo
    }

    fun clear() {
        //todo imageLoader.clear(imageView)
    }
}
