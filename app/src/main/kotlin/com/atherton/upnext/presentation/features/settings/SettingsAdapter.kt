package com.atherton.upnext.presentation.features.settings

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.util.extensions.inflateLayout
import com.atherton.upnext.util.glide.GlideRequests
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_setting.*

class SettingsAdapter(
    private val imageLoader: GlideRequests,
    private val onClickListener: (Setting) -> Unit
) : ListAdapter<Setting, SettingsAdapter.ViewHolder>(SettingsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_setting)
        val viewHolder = ViewHolder(view, imageLoader)
        viewHolder.itemView.setOnClickListener {
            onClickListener.invoke(getItem(viewHolder.adapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    class ViewHolder(override val containerView: View, private val imageLoader: GlideRequests)
        : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(setting: Setting) {
            imageLoader.load(setting.logoResId).into(settingIconImageView)
            settingTitleTextView.text = setting.title
        }

        fun clear() {
            imageLoader.clear(settingIconImageView)
        }
    }

    companion object {
        private object SettingsDiffCallback : DiffUtil.ItemCallback<Setting>() {

            override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean {
                return oldItem == newItem
            }
        }
    }
}
