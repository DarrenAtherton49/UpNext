package com.atherton.upnext.presentation.features.content.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.CastMember
import com.atherton.upnext.util.extension.inflateLayout
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.glide.UpNextAppGlideModule
import kotlinx.android.synthetic.main.item_detail_scrolling_item.*

class ModelDetailCastAdapter(
    private val imageLoader: GlideRequests,
    private val onCastMemberClickListener: (CastMember) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<CastMember, ModelDetailScrollingViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelDetailScrollingViewHolder {
        return ModelDetailScrollingViewHolder(parent.inflateLayout(R.layout.item_detail_scrolling_item), imageLoader).apply {
            itemView.setOnClickListener {
                onCastMemberClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ModelDetailScrollingViewHolder, position: Int) {
        val castMember = getItem(position)

        imageLoader
            .load(castMember.profilePath)
            .apply(UpNextAppGlideModule.searchModelPosterRequestOptions)
            .into(holder.photoImageView)

        holder.firstRowTextView.text = castMember.name
        holder.secondRowTextView.text = castMember.character
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<CastMember>() {

            override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                return oldItem.castId == newItem.castId
            }

            override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}
