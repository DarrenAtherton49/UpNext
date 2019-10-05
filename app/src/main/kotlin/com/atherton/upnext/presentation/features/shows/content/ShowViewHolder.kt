package com.atherton.upnext.presentation.features.shows.content

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.util.extension.setTextOrHide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_show.*

class ShowViewHolder(
    override val containerView: View,
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    fun bind(showListItem: ShowListItem) {

        imageLoader.loadContentListItem(
            with = glideRequests,
            url = showListItem.posterPath,
            into = showImageView,
            onSuccess = { colorCombination ->
                showCardView.setCardBackgroundColor(colorCombination.backgroundColor)
            }
        )

        ratingTextView.setTextOrHide(showListItem.voteAverage)

        imageLoader.load(with = glideRequests, drawableResId = showListItem.watchlistButtonResId, into = watchlistButton)
        imageLoader.load(with = glideRequests, drawableResId = showListItem.watchedButtonResId, into = watchedButton)
        imageLoader.load(with = glideRequests, drawableResId = showListItem.addToListButtonResId, into = addToListButton)

        releaseDateTextView.setTextOrHide(showListItem.releaseDate)
        titleTextView.setTextOrHide(showListItem.title)
        genresTextView.setTextOrHide(showListItem.genresString)
        runtimeTextView.setTextOrHide(showListItem.runtime)
    }

    fun clear() {
        imageLoader.clear(with = glideRequests, imageView = showImageView)
    }
}
