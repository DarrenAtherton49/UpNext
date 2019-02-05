package com.atherton.upnext.presentation.features.discover.search

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.TvShow
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie_search_result.*
import kotlinx.android.synthetic.main.item_person_search_result.*
import kotlinx.android.synthetic.main.item_tv_search_result.*

sealed class SearchResultViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer

class TvShowSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(tvShow: TvShow) {
        //todo show details
        tvShowTitleTextView.text = tvShow.name
    }
}

class MovieSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(movie: Movie) {
        //todo movie details
        movieTitleTextView.text = movie.title
    }
}

class PersonSearchResultViewHolder(itemView: View) : SearchResultViewHolder(itemView) {
    fun bind(person: Person) {
        //todo person details
        personNameTextView.text = person.name
    }
}
