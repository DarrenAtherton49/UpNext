package com.atherton.upnext.presentation.features.discover.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.TvShow
import kotlinx.android.extensions.LayoutContainer

sealed class SearchResultViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer

class TvShowSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(tvShow: TvShow) {
        //todo show details
    }
}

class MovieSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(movie: Movie) {
        //todo show details
    }
}

class PersonSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(person: Person) {
        //todo show details
    }
}
