package com.atherton.upnext.presentation.features.settings.licenses

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atherton.upnext.R
import com.atherton.upnext.util.extensions.inflateLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_open_source_license.*
import kotlinx.android.synthetic.main.item_open_source_license.view.*

class LicensesAdapter(private val onClickListener: (License) -> Unit) :
    ListAdapter<License, LicensesAdapter.ViewHolder>(LicensesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = parent.inflateLayout(R.layout.item_open_source_license)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.viewLicenseButton.setOnClickListener {
            onClickListener.invoke(getItem(viewHolder.adapterPosition))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(license: License) {
            nameContributorTextView.text = license.name
            descriptionTextView.text = license.description
        }
    }

    companion object {
        private object LicensesDiffCallback : DiffUtil.ItemCallback<License>() {

            override fun areItemsTheSame(oldItem: License, newItem: License): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: License, newItem: License): Boolean {
                return oldItem == newItem
            }
        }
    }
}
