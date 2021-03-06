package com.atherton.upnext.presentation.features.content.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Video
import com.atherton.upnext.presentation.util.glide.GlideRequests
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.util.extension.inflateLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_detail_scrolling_video_item.*

class ModelDetailVideoAdapter(
    private val imageLoader: ImageLoader,
    private val glideRequests: GlideRequests,
    private val onVideoClickListener: (Video) -> Unit
) : ModelDetailAdapter.ScrollingChildAdapter<Video, ModelDetailVideoAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            parent.inflateLayout(R.layout.item_detail_scrolling_video_item),
            imageLoader,
            glideRequests
        ).apply {
            itemView.setOnClickListener {
                onVideoClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        override val containerView: View,
        private val imageLoader: ImageLoader,
        private val glideRequests: GlideRequests
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(video: Video) {
            //todo figure out correct width for this ImageView - we want it to be full screen width minus margin each side
            imageLoader.load(
                with = glideRequests,
                url = video.thumbnail,
                requestOptions = ImageLoader.modelDetailVideoRequestOptions,
                into = thumbnailImageView
            )

            videoTitleTextView.text = video.name
            videoTypeTextView.text = video.type
        }
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<Video>() {

            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem == newItem
            }
        }
    }
}
