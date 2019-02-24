package com.atherton.upnext.presentation.common.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.CrewMember
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests
import com.atherton.upnext.util.glide.UpNextAppGlideModule
import kotlinx.android.synthetic.main.item_detail_scrolling_item.*

class ModelDetailCrewAdapter(
    private val imageLoader: GlideRequests,
    private val onCrewMemberClickListener: (CrewMember) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<CrewMember, ModelDetailScrollingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_scrolling_item), imageLoader).apply {
            itemView.setOnClickListener {
                onCrewMemberClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val crewMember = getItem(position)

        imageLoader
            .load(crewMember.profilePath)
            .apply(UpNextAppGlideModule.searchModelPosterRequestOptions)
            .into(holder.photoImageView)

        holder.firstRowTextView.text = crewMember.name
        holder.secondRowTextView.text = crewMember.job
    }

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
