package com.atherton.upnext.presentation.common.detail

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.domain.model.CastMember
import com.atherton.upnext.domain.model.CrewMember
import com.atherton.upnext.domain.model.Video
import com.atherton.upnext.domain.model.Watchable
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import com.google.android.material.chip.Chip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_genres.*
import kotlinx.android.synthetic.main.item_detail_info_panel.*
import kotlinx.android.synthetic.main.item_detail_overview.*
import kotlinx.android.synthetic.main.item_detail_scrolling_section.*
import kotlinx.android.synthetic.main.item_detail_scrolling_section.view.*

sealed class ModelDetailSectionViewHolder(override val containerView: View)
    : RecyclerView.ViewHolder(containerView),
    LayoutContainer

class ModelDetailInfoPanelViewHolder(override val containerView: View)
    : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.InfoPanel) {
        if (section.releaseDate != null) {
            releaseDateTextView.text = section.releaseDate
            releaseDateTextView.isVisible = true
        } else {
            releaseDateTextView.isVisible = false
        }
        if (section.runtime != null) {
            runtimeTextView.text = section.runtime
            runtimeTextView.isVisible = true
        } else {
            runtimeTextView.isVisible = false
        }
        if (section.voteAverage != null) {
            ratingTextView.text = section.voteAverage
            ratingTextView.isVisible = true
        } else {
            ratingTextView.isVisible = false
        }
        infoPanelFirstDivider.isVisible = section.showFirstDivider
        infoPanelSecondDivider.isVisible = section.showSecondDivider
    }
}

class ModelDetailOverviewViewHolder(override val containerView: View) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Overview) {
        overviewTextView.text = section.overview
    }
}

class ModelDetailGenresViewHolder(override val containerView: View) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Genres) {
        section.genres.forEach { genre ->
            genre.name?.let { name ->
                val chip = Chip(containerView.context).apply {
                    text = name
                    isClickable = false
                    isCheckable = false
                }
                genreChipGroup.addView(chip as View)
            }
        }
    }
}

class ModelDetailRatingsViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Ratings) {
        //todo
    }
}

class ModelDetailSeasonsViewHolder(override val containerView: View) : ModelDetailSectionViewHolder(containerView) {

    init {
        val layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)

        // The line below ensures that child RecyclerView does not capture vertical scrolls,
        // as we want the outer RecyclerView to handle those instead.
        itemView.childRecyclerView.isNestedScrollingEnabled = false
        itemView.childRecyclerView.setHasFixedSize(false)
        itemView.childRecyclerView.layoutManager = layoutManager
    }

    fun bind(section: ModelDetailSection.Seasons, childAdapter: ModelDetailSeasonAdapter, childAdapterState: Parcelable?) {
        childRecyclerView.adapter = childAdapter
        sectionHeaderTextView.text = section.sectionTitle
        childAdapter.submitList(section.seasons)
        childRecyclerView.layoutManager?.onRestoreInstanceState(childAdapterState)
    }
}

class ModelDetailCastViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Cast, CastMember, ModelDetailCastAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Cast, childAdapter: ModelDetailCastAdapter) {
        sectionHeaderTextView.text = section.sectionTitle
        childAdapter.submitList(section.cast)
    }
}

class ModelDetailCrewViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Crew, CrewMember, ModelDetailCrewAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Crew, childAdapter: ModelDetailCrewAdapter) {
        sectionHeaderTextView.text = section.sectionTitle
        childAdapter.submitList(section.crew)
    }
}

class ModelDetailVideosViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool?,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Videos, Video, ModelDetailVideosAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Videos, childAdapter: ModelDetailVideosAdapter) {
        sectionHeaderTextView.text = section.sectionTitle
        childAdapter.submitList(section.videos)
    }
}

class ModelDetailPhotosViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool?,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Photos, String, ModelDetailPhotosAdapter>( //todo change String to Photo
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Photos, childAdapter: ModelDetailPhotosAdapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ModelDetailReviewsViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Reviews) {
        //todo
    }
}

class ModelDetailCommentsViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Comments) {
        //todo
    }
}

class ModelDetailRecommendedItemsViewHolder(
    containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.RecommendedItems, Watchable, ModelDetailRecommendedItemsAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.RecommendedItems, childAdapter: ModelDetailRecommendedItemsAdapter) {
        sectionHeaderTextView.text = section.sectionTitle
        childAdapter.submitList(section.recommendedItems)
    }
}

class ModelDetailExternalLinksViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.ExternalLinks) {
        //todo
    }
}

// used as am empty default when view type is not known
class ModelDetailEmptyViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView)

// used for all scrolling CHILD ViewHolders - i.e. cast, crew and recommended movie/tv items as using this common type
// allows for us to use a RecyclerViewPool to reuse views
class ModelDetailScrollingViewHolder(override val containerView: View, private val imageLoader: GlideRequests)
    : RecyclerView.ViewHolder(containerView),
    LayoutContainer

abstract class ModelDetailScrollableViewHolder<SECTION : ModelDetailSection, DATA: Any, ADAPTER: ListAdapter<DATA, *>>(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool?,
    itemSpacingPx: Int
) : ModelDetailSectionViewHolder(containerView) {

    init {
        val layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        layoutManager.initialPrefetchItemCount = ITEM_PREFETCH_COUNT //todo set based on orientation

        // The line below ensures that child RecyclerView does not capture vertical scrolls,
        // as we want the outer RecyclerView to handle those instead.
        itemView.childRecyclerView.isNestedScrollingEnabled = false
        itemView.childRecyclerView.setHasFixedSize(true)
        itemView.childRecyclerView.addItemDecoration(
            LinearSpacingItemDecoration(itemSpacingPx, LinearSpacingItemDecoration.Orientation.Horizontal)
        )
        itemView.childRecyclerView.layoutManager = layoutManager

        // if we pass in a RecycledViewPool it means we want to share scrap views between multiple RecyclerViews
        recycledViewPool?.let {
            itemView.childRecyclerView.setRecycledViewPool(it)
        }
    }

    fun bindHorizontalAdapter(section: SECTION, childAdapter: ADAPTER, childAdapterState: Parcelable?) {
        childRecyclerView.adapter = childAdapter
        bind(section, childAdapter)
        childRecyclerView.layoutManager?.onRestoreInstanceState(childAdapterState)
    }

    protected abstract fun bind(section: SECTION, childAdapter: ADAPTER)

    companion object Factory {
        private const val ITEM_PREFETCH_COUNT = 5
    }
}
