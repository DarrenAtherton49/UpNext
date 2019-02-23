package com.atherton.upnext.presentation.common.detail

import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests

//todo preload some images when scrolling https://bumptech.github.io/glide/int/recyclerview.html
class ModelDetailAdapter(
    private val imageLoader: GlideRequests
) : ListAdapter<ModelDetailSection, ModelDetailSectionViewHolder>(ModelDetailDiffCallback) {

    private val recyclerViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    // Maps of child RecyclerView adapters and state to restore
    //todo private lateinit var childAdapters: SparseArray<ModelDetailChildAdapter?>
    private lateinit var childAdapterStates: SparseArray<Parcelable?>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailSectionViewHolder {

        return when (viewType) {
            ModelDetailSection.RUNTIME_RELEASE -> ModelDetailRuntimeReleaseDateViewHolder(
                parent.inflateLayout(R.layout.item_detail_runtime_release_date)
            )
            ModelDetailSection.OVERVIEW -> ModelDetailOverviewViewHolder(
                parent.inflateLayout(R.layout.item_detail_overview)
            )
            else -> ModelDetailEmptyViewHolder(parent.inflateLayout(R.layout.item_detail_empty))
        }
    }

    override fun onBindViewHolder(holder: ModelDetailSectionViewHolder, position: Int) {
        //val section = getItem(position)
        //todo holder.bind(section, childAdapters[section.viewType], childAdapterStates[section.viewType])


        when (holder) {
            is ModelDetailRuntimeReleaseDateViewHolder -> holder.bind(getItem(position) as ModelDetailSection.RuntimeRelease)
            is ModelDetailOverviewViewHolder -> holder.bind(getItem(position) as ModelDetailSection.Overview)
            is ModelDetailGenresViewHolder -> holder.bind(getItem(position) as ModelDetailSection.Genres)
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    // Cache the scroll position of the lists so that we can restore it when re-binding.
    override fun onViewRecycled(holder: ModelDetailSectionViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            val key = getItem(position).viewType
            //todo childAdapterStates.setValueAt(key, holder.itemView.childRecyclerView.layoutManager?.onSaveInstanceState())
        }
        super.onViewRecycled(holder)
    }

    fun submitData(sections: List<ModelDetailSection>) {
        initChildAdapters(sections)
        submitList(sections)
    }

    private fun initChildAdapters(sections: List<ModelDetailSection>) {
        //todo childAdapters = SparseArray(sections.size)
        childAdapterStates = SparseArray(sections.size)

        // store an adapter for each section (parent item - each carousel) only if it has
        // scrolling content and needs and adapter
        //todo
//        sections.forEach { section ->
//            val adapter = if (section.hasScrollingChildAdapter) {
//                DiscoverChildAdapter(imageLoader, onClickListener)
//            } else {
//                null
//            }
//            childAdapters.setValueAt(section.viewType, adapter)
//        }
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
}
