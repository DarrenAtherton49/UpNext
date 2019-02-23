package com.atherton.upnext.presentation.common.detail

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import com.google.android.material.chip.Chip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_child_recyclerview.*
import kotlinx.android.synthetic.main.item_detail_child_recyclerview.view.*
import kotlinx.android.synthetic.main.item_detail_genres.*
import kotlinx.android.synthetic.main.item_detail_overview.*
import kotlinx.android.synthetic.main.item_detail_runtime_release_date.*

sealed class ModelDetailSectionViewHolder(override val containerView: View)
    : RecyclerView.ViewHolder(containerView),
    LayoutContainer

class ModelDetailRuntimeReleaseDateViewHolder(override val containerView: View)
    : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.RuntimeRelease) {
        runtimeTextView.text = section.runtime
        releaseDateTextView.text = section.releaseDate
        releaseDateRuntimeDivider.isVisible = section.showDivider
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

class ModelDetailSeasonsViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Seasons) {
        //todo
    }
}

class ModelDetailCastViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Cast, ModelDetailCastAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Cast, childAdapter: ModelDetailCastAdapter) {
        //childAdapter.submitList(section.cast)
    }
}

class ModelDetailCrewViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Crew, ModelDetailCrewAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Crew, childAdapter: ModelDetailCrewAdapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ModelDetailTrailersViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Trailers, ModelDetailTrailersAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.Trailers, childAdapter: ModelDetailTrailersAdapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ModelDetailPhotosViewHolder(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.Photos, ModelDetailPhotosAdapter>(
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

class ModelDetailSimilarItemsViewHolder(
    containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
    itemSpacingPx: Int
) : ModelDetailScrollableViewHolder<ModelDetailSection.SimilarItems, ModelDetailSimilarItemsAdapter>(
    containerView,
    recycledViewPool,
    itemSpacingPx
) {
    override fun bind(section: ModelDetailSection.SimilarItems, childAdapter: ModelDetailSimilarItemsAdapter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

class ModelDetailEmptyViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView)

abstract class ModelDetailScrollableViewHolder<SECTION : ModelDetailSection, ADAPTER: ListAdapter<SECTION, *>>(
    override val containerView: View,
    recycledViewPool: RecyclerView.RecycledViewPool,
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
        itemView.childRecyclerView.setRecycledViewPool(recycledViewPool)
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
