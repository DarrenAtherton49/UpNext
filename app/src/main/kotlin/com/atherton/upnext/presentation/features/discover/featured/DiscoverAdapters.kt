package com.atherton.upnext.presentation.features.discover.featured

import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Person
import com.atherton.upnext.domain.model.SearchModel
import com.atherton.upnext.domain.model.TvShow
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.synthetic.main.item_discover_section.view.*

class DiscoverSectionAdapter(
    private val imageLoader: GlideRequests,
    private val childRecyclerItemSpacingPx: Int,
    private val onClickListener: (SearchModel) -> Unit
) : ListAdapter<DiscoverSection, DiscoverSectionViewHolder>(DiscoverSectionsDiffCallback) {

    private val recyclerViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    // Maps of child RecyclerView adapters and state to restore
    private lateinit var childAdapters: HashMap<String, DiscoverChildAdapter>
    private lateinit var childAdapterStates: HashMap<String, Parcelable?>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverSectionViewHolder {
        return DiscoverSectionViewHolder.create(
            parent.inflateLayout(R.layout.item_discover_section),
            recyclerViewPool,
            childRecyclerItemSpacingPx
        )
    }

    override fun onBindViewHolder(holder: DiscoverSectionViewHolder, position: Int) {
        val section = getItem(position)
        holder.bind(section, childAdapters[section.title], childAdapterStates[section.title])
    }

    // Cache the scroll position of the lists so that we can restore it when re-binding.
    override fun onViewRecycled(holder: DiscoverSectionViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val key = getItem(position).title
            childAdapterStates[key] = holder.itemView.childRecyclerView.layoutManager?.onSaveInstanceState()
        }
        super.onViewRecycled(holder)
    }

    fun submitData(sections: List<DiscoverSection>) {
        initChildAdapters(sections)
        submitList(sections)
    }

    private fun initChildAdapters(sections: List<DiscoverSection>) {
        childAdapters = HashMap(sections.size)
        childAdapterStates = HashMap(sections.size)

        // store an adapter for each parent item (each carousel)
        sections.forEach { section ->
            childAdapters[section.title] = DiscoverChildAdapter(imageLoader, onClickListener)
        }
    }

    companion object {
        private object DiscoverSectionsDiffCallback : DiffUtil.ItemCallback<DiscoverSection>() {

            override fun areItemsTheSame(oldItem: DiscoverSection, newItem: DiscoverSection): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: DiscoverSection, newItem: DiscoverSection): Boolean {
                return oldItem == newItem
            }
        }
    }
}

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class DiscoverChildAdapter(
    private val imageLoader: GlideRequests,
    private val onClickListener: (SearchModel) -> Unit
) : ListAdapter<SearchModel, DiscoverChildViewHolder>(DiscoverChildDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverChildViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_discover_result)
        return when (viewType) {
            TV_VIEW_TYPE -> TvShowDiscoverViewHolder(view, imageLoader).withClickListener()
            MOVIE_VIEW_TYPE -> MovieDiscoverViewHolder(view, imageLoader).withClickListener()
            PERSON_VIEW_TYPE -> PersonDiscoverViewHolder(view, imageLoader).withClickListener()
            else -> MovieDiscoverViewHolder(view, imageLoader).withClickListener()
        }
    }

    override fun onBindViewHolder(holder: DiscoverChildViewHolder, position: Int) {
        when (holder) {
            is TvShowDiscoverViewHolder -> holder.bind(getItem(position) as TvShow)
            is MovieDiscoverViewHolder -> holder.bind(getItem(position) as Movie)
            is PersonDiscoverViewHolder -> holder.bind(getItem(position) as Person)
        }
    }

    private fun DiscoverChildViewHolder.withClickListener(): DiscoverChildViewHolder = this.apply {
        itemView.setOnClickListener { onClickListener.invoke(getItem(adapterPosition)) }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TvShow -> TV_VIEW_TYPE
            is Movie -> MOVIE_VIEW_TYPE
            is Person -> PERSON_VIEW_TYPE
        }
    }

    override fun onViewRecycled(holder: DiscoverChildViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    companion object {
        private const val TV_VIEW_TYPE = 0
        private const val MOVIE_VIEW_TYPE = 1
        private const val PERSON_VIEW_TYPE = 2

        private object DiscoverChildDiffCallback : DiffUtil.ItemCallback<SearchModel>() {

            // TMDB ids are not globally unique - only unique per type (e.g. movie)
            override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem.id == newItem.id && oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
