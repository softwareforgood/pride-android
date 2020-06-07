package com.softwareforgood.pridefestival.ui.vendor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.misc.BindableRecyclerAdapter
import com.softwareforgood.pridefestival.ui.misc.SimpleListHeaderView
import com.softwareforgood.pridefestival.ui.misc.VendorClickHandler
import com.softwareforgood.pridefestival.util.inflate
import javax.inject.Inject

abstract class VendorAdapter : BindableRecyclerAdapter<Vendor>(),
        StickyHeaderAdapter<BindableRecyclerAdapter.ViewHolder> {
    abstract fun loadVendors(vendors: List<Vendor>)
}

@VendorScope
class DefaultVendorAdapter @Inject constructor(
    private val favoritesStorage: FavoritesStorage,
    private val vendorClickHandler: VendorClickHandler
) : VendorAdapter() {
    private var headers = listOf<Char>()
    private var vendors = listOf<Vendor>()

    override fun loadVendors(vendors: List<Vendor>) {
        val grouped = vendors.asSequence()
                .filter { it.name.isNotBlank() }
                .groupBy { it.name.first() }

        this.headers = grouped.entries.map { (key, value) -> value.map { key } }.flatten()
        this.vendors = grouped.values.flatten()

        notifyDataSetChanged()
    }

    override fun newView(inflater: LayoutInflater, viewType: Int, parent: ViewGroup): View =
            inflater.inflate(R.layout.view_vendor_list_item, parent, false)

    override fun getItem(position: Int) = vendors[position]

    override fun getItemCount() = vendors.size

    override fun bindView(item: Vendor, view: View, position: Int) {
        (view as VendorItemView).apply {
            setOnClickListener { vendorClickHandler.publishClick(item) }
            bind(item, favoritesStorage)
        }
    }

    override fun getHeaderId(position: Int): Long = headers[position]
            .hashCode()
            .toLong()

    override fun onBindHeaderViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as SimpleListHeaderView).bind(headers[position].toString())
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): ViewHolder =
            ViewHolder(parent.inflate(R.layout.view_simple_list_header))

    override fun getItemId(position: Int) = vendors[position].objectId.hashCode().toLong()
}
