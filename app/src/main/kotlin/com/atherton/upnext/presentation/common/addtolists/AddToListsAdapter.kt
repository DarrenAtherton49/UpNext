package com.atherton.upnext.presentation.common.addtolists

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.util.extensions.inflateLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_to_lists.*
import kotlinx.android.synthetic.main.item_add_to_lists.view.*

class AddToListsAdapter(
    private val onListClickListener: (ContentList) -> Unit
) : ListAdapter<ContentList, AddToListsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateLayout(R.layout.item_add_to_lists)).apply {
            itemView.addToListsCheckBox.setOnClickListener {
                onListClickListener.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(listItem: ContentList) {
            //addToListsCheckBox.isChecked = listItem.isChecked
            addToListsCheckBox.text = listItem.name
        }
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<ContentList>() {

            override fun areItemsTheSame(oldItem: ContentList, newItem: ContentList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ContentList, newItem: ContentList): Boolean {
                return oldItem == newItem
            }
        }
    }
}
