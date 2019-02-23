package com.atherton.upnext.presentation.common.detail

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_overview.*
import kotlinx.android.synthetic.main.item_detail_runtime_release_date.*

sealed class ModelDetailSectionViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView),
    LayoutContainer

class ModelDetailRuntimeReleaseDateViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.RuntimeRelease) {
        runtimeTextView.text = section.runtime
        releaseDateTextView.text = section.releaseDate
        releaseDateRuntimeDivider.isVisible = section.showDivider
    }
}

class ModelDetailOverviewViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Overview) {
        overviewTextView.text = section.overview
    }
}

class ModelDetailGenresViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind(section: ModelDetailSection.Genres) {
        //todo
    }
}

class ModelDetailRatingsViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailSeasonsViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailCastViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailCrewViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailTrailersViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailPhotosViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailReviewsViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailCommentsViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailSimilarItemsViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailExternalLinksViewHolder(
    override val containerView: View,
    imageLoader: GlideRequests
) : ModelDetailSectionViewHolder(containerView) {

    fun bind() {
        //todo
    }
}

class ModelDetailEmptyViewHolder(
    override val containerView: View
) : ModelDetailSectionViewHolder(containerView)
