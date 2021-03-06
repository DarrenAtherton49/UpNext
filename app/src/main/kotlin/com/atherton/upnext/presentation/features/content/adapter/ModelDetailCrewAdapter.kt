package com.atherton.upnext.presentation.features.content.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.CrewMember
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.util.extension.inflateLayout
import kotlinx.android.synthetic.main.item_detail_scrolling_item.*

class ModelDetailCrewAdapter(
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests,
    private val onCrewMemberClickListener: (CrewMember) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<CrewMember, ModelDetailScrollingViewHolder>(DiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_scrolling_item)).apply {
            itemView.setOnClickListener {
                onCrewMemberClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val crewMember = getItem(position)

        imageLoader.load(
            with = glideRequests,
            url = crewMember.profilePath,
            requestOptions = ImageLoader.searchModelPosterRequestOptions,
            into = holder.photoImageView
        )

        holder.firstRowTextView.text = crewMember.name
        holder.secondRowTextView.text = crewMember.job
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<CrewMember>() {

            override fun areItemsTheSame(oldItem: CrewMember, newItem: CrewMember): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CrewMember, newItem: CrewMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}
