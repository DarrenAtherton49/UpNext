package com.atherton.upnext.presentation.common.addtolists

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.ContentListStatus
import com.atherton.upnext.util.extension.inflateLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_to_lists.*
import kotlinx.android.synthetic.main.item_add_to_lists.view.*

class AddToListsAdapter(
    private val onListClickListener: (ContentListStatus) -> Unit
) : ListAdapter<ContentListStatus, AddToListsAdapter.ViewHolder>(DiffCallback) {

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

        fun bind(listItem: ContentListStatus) {
            addToListsCheckBox.isChecked = listItem.contentIsInList
            addToListsCheckBox.text = listItem.listName
        }
    }

    companion object {
        private object DiffCallback : DiffUtil.ItemCallback<ContentListStatus>() {

            override fun areItemsTheSame(oldItem: ContentListStatus, newItem: ContentListStatus): Boolean {
                return oldItem.listId == newItem.listId
            }

            override fun areContentsTheSame(oldItem: ContentListStatus, newItem: ContentListStatus): Boolean {
                return oldItem == newItem
            }
        }
    }
}
