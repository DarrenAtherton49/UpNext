package com.atherton.upnext.presentation.common.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.extensions.LayoutContainer

class ModelDetailCrewAdapter(
    private val imageLoader: GlideRequests
) : ModelDetailAdapter.ScrollingChildAdapter<ModelDetailSection.Crew, ModelDetailCrewAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(crewMember: ModelDetailSection.Crew) {
            //todo bind image, crew name, character etc
        }
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<ModelDetailSection.Crew>() {

            override fun areItemsTheSame(oldItem: ModelDetailSection.Crew, newItem: ModelDetailSection.Crew): Boolean {
                return oldItem.viewType == newItem.viewType
            }

            override fun areContentsTheSame(oldItem: ModelDetailSection.Crew, newItem: ModelDetailSection.Crew): Boolean {
                return oldItem == newItem
            }
        }
    }
}
