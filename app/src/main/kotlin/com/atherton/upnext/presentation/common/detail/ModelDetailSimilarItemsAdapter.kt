package com.atherton.upnext.presentation.common.detail

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.extensions.LayoutContainer

class ModelDetailSimilarItemsAdapter(
    private val imageLoader: GlideRequests
) : ModelDetailAdapter.ScrollingChildAdapter<ModelDetailSection.SimilarItems, ModelDetailSimilarItemsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(trailer: ModelDetailSection.SimilarItems) {
            //todo bind similar item photo, name etc.
        }
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<ModelDetailSection.SimilarItems>() {

            override fun areItemsTheSame(oldItem: ModelDetailSection.SimilarItems, newItem: ModelDetailSection.SimilarItems): Boolean {
                return oldItem.viewType == newItem.viewType
            }

            override fun areContentsTheSame(oldItem: ModelDetailSection.SimilarItems, newItem: ModelDetailSection.SimilarItems): Boolean {
                return oldItem == newItem
            }
        }
    }
}
