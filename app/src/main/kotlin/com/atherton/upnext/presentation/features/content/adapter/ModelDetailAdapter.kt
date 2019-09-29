package com.atherton.upnext.presentation.features.content.adapter

import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.*
import com.atherton.upnext.presentation.features.content.ModelDetailSection
import com.atherton.upnext.util.extension.inflateLayout
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import kotlinx.android.synthetic.main.item_detail_scrolling_section.view.*

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class ModelDetailAdapter(
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests,
    private val childRecyclerItemSpacingPx: Int,
    private val onSeasonClickListener: (TvSeason) -> Unit,
    private val onCastMemberClickListener: (CastMember) -> Unit,
    private val onCrewMemberClickListener: (CrewMember) -> Unit,
    private val onVideoClickListener: (Video) -> Unit,
    private val onRecommendedItemClickListener: (Watchable) -> Unit
) : ListAdapter<ModelDetailSection, ModelDetailSectionViewHolder>(ModelDetailDiffCallback) {

    private val recycledViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    //todo create a second recycledViewPool to share photos and videos views?

    // Maps of child RecyclerView adapters and state to restore
    private lateinit var childAdapters: SparseArray<ScrollingChildAdapter<*, *>>
    private lateinit var childAdapterStates: SparseArray<Parcelable?>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailSectionViewHolder {
        return when (viewType) {
            ModelDetailSection.RUNTIME_RELEASE -> ModelDetailInfoPanelViewHolder(
                parent.inflateLayout(R.layout.item_detail_info_panel)
            )
            ModelDetailSection.OVERVIEW -> ModelDetailOverviewViewHolder(parent.inflateLayout(R.layout.item_detail_overview))
            ModelDetailSection.GENRES -> ModelDetailGenresViewHolder(parent.inflateLayout(R.layout.item_detail_genres))
            //todo implement layout
            ModelDetailSection.RATINGS -> ModelDetailRatingsViewHolder(
                parent.inflateLayout(R.layout.item_detail_ratings)
            )
            ModelDetailSection.SEASONS -> ModelDetailSeasonsViewHolder(
                parent.inflateLayout(R.layout.item_detail_scrolling_season_section)
            )
            ModelDetailSection.CAST -> ModelDetailCastViewHolder(
                parent.inflateLayout(R.layout.item_detail_scrolling_section),
                recycledViewPool,
                childRecyclerItemSpacingPx
            )
            ModelDetailSection.CREW -> ModelDetailCrewViewHolder(
                parent.inflateLayout(R.layout.item_detail_scrolling_section),
                recycledViewPool,
                childRecyclerItemSpacingPx
            )
            ModelDetailSection.VIDEOS -> ModelDetailVideosViewHolder(
                parent.inflateLayout(R.layout.item_detail_scrolling_section),
                null,
                childRecyclerItemSpacingPx
            )
            //todo implement layout
            ModelDetailSection.PHOTOS -> ModelDetailPhotosViewHolder(
                parent.inflateLayout(R.layout.item_detail_photos),
                null,
                childRecyclerItemSpacingPx
            )
            //todo implement layout
            ModelDetailSection.REVIEWS -> ModelDetailReviewsViewHolder(parent.inflateLayout(R.layout.item_detail_reviews))
            //todo implement layout
            ModelDetailSection.COMMENTS -> ModelDetailCommentsViewHolder(parent.inflateLayout(R.layout.item_detail_comments))
            ModelDetailSection.RECOMMENDED_ITEMS -> ModelDetailRecommendedItemsViewHolder(
                parent.inflateLayout(R.layout.item_detail_scrolling_section),
                recycledViewPool,
                childRecyclerItemSpacingPx
            )
            //todo implement layout
            ModelDetailSection.EXTERNAL_LINKS -> ModelDetailExternalLinksViewHolder(
                parent.inflateLayout(R.layout.item_detail_external_links)
            )
            else -> ModelDetailEmptyViewHolder(parent.inflateLayout(R.layout.item_detail_empty))
        }
    }

    override fun onBindViewHolder(holder: ModelDetailSectionViewHolder, position: Int) {
        val section = getItem(position)
        when (holder) {
            is ModelDetailInfoPanelViewHolder -> holder.bind(section as ModelDetailSection.InfoPanel)
            is ModelDetailOverviewViewHolder -> holder.bind(section as ModelDetailSection.Overview)
            is ModelDetailGenresViewHolder -> holder.bind(section as ModelDetailSection.Genres)
            is ModelDetailRatingsViewHolder -> holder.bind(section as ModelDetailSection.Ratings)
            is ModelDetailSeasonsViewHolder -> holder.bind(
                section as ModelDetailSection.Seasons,
                childAdapters[section.viewType] as ModelDetailSeasonAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailCastViewHolder -> holder.bindHorizontalAdapter(
                section as ModelDetailSection.Cast,
                childAdapters[section.viewType] as ModelDetailCastAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailCrewViewHolder -> holder.bindHorizontalAdapter(
                section as ModelDetailSection.Crew,
                childAdapters[section.viewType] as ModelDetailCrewAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailVideosViewHolder -> holder.bindHorizontalAdapter(
                section as ModelDetailSection.Videos,
                childAdapters[section.viewType] as ModelDetailVideoAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailPhotosViewHolder -> holder.bindHorizontalAdapter(
                section as ModelDetailSection.Photos,
                childAdapters[section.viewType] as ModelDetailPhotosAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailReviewsViewHolder -> holder.bind(section as ModelDetailSection.Reviews)
            is ModelDetailCommentsViewHolder -> holder.bind(section as ModelDetailSection.Comments)
            is ModelDetailRecommendedItemsViewHolder -> holder.bindHorizontalAdapter(
                section as ModelDetailSection.RecommendedItems,
                childAdapters[section.viewType] as ModelDetailRecommendedItemsAdapter,
                childAdapterStates[section.viewType]
            )
            is ModelDetailExternalLinksViewHolder -> holder.bind(section as ModelDetailSection.ExternalLinks)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    // we can use the viewType here because in this specific adapter, we only have at max 1 item
    // per view type.
    override fun getItemId(position: Int): Long = getItem(position).viewType.toLong()

    // Cache the scroll position of the lists so that we can restore it when re-binding.
    override fun onViewRecycled(holder: ModelDetailSectionViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val item = getItem(position)
            if (item.hasScrollingChildAdapter) {
                val key = getItem(position).viewType
                childAdapterStates.put(key, holder.itemView.childRecyclerView.layoutManager?.onSaveInstanceState())
            }
        }
        super.onViewRecycled(holder)
    }

    fun submitData(sections: List<ModelDetailSection>) {
        initChildAdapters(sections)
        submitList(sections)
    }

    private fun initChildAdapters(sections: List<ModelDetailSection>) {
        childAdapters = SparseArray()
        childAdapterStates = SparseArray()

        // store an adapter for each section (parent item - each carousel) only if it has
        // scrolling content and needs an adapter
        sections.forEach { section ->
            val adapter = when (section) {
                is ModelDetailSection.Seasons -> ModelDetailSeasonAdapter(imageLoader, glideRequests, onSeasonClickListener)
                is ModelDetailSection.Cast -> ModelDetailCastAdapter(imageLoader, glideRequests, onCastMemberClickListener)
                is ModelDetailSection.Crew -> ModelDetailCrewAdapter(imageLoader, glideRequests, onCrewMemberClickListener)
                is ModelDetailSection.Videos -> ModelDetailVideoAdapter(imageLoader, glideRequests,  onVideoClickListener)
                is ModelDetailSection.Photos -> ModelDetailPhotosAdapter(imageLoader, glideRequests)
                is ModelDetailSection.RecommendedItems -> ModelDetailRecommendedItemsAdapter(
                    imageLoader,
                    glideRequests,
                    onRecommendedItemClickListener
                )
                else -> null
            }
            if (adapter != null) {
                childAdapters.put(section.viewType, adapter)
            }
        }
    }

    companion object {
        private object ModelDetailDiffCallback : DiffUtil.ItemCallback<ModelDetailSection>() {

            override fun areItemsTheSame(oldItem: ModelDetailSection, newItem: ModelDetailSection): Boolean {
                return oldItem.viewType == newItem.viewType
            }

            override fun areContentsTheSame(oldItem: ModelDetailSection, newItem: ModelDetailSection): Boolean {
                return oldItem == newItem
            }
        }
    }

    abstract class ScrollingChildAdapter<T, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>)
        : ListAdapter<T, VH>(diffCallback)
}
