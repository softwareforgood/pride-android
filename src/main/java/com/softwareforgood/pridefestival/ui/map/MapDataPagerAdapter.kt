package com.softwareforgood.pridefestival.ui.map

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.Mappable
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.events.EventItemView
import com.softwareforgood.pridefestival.ui.vendor.VendorItemView
import com.softwareforgood.pridefestival.util.inflate
import javax.inject.Inject

class MapListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

@MapScope
class MapDataPagerAdapter @Inject constructor(
    private val favoritesStorage: FavoritesStorage
) : RecyclerView.Adapter<MapListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapListViewHolder =
            MapListViewHolder(parent.inflate(viewType.layout()))

    override fun onBindViewHolder(holder: MapListViewHolder, position: Int) {
        val mappable = data[position]
        when (mappable) {
            is VendorMarkerData -> (holder.itemView as VendorItemView).apply {
                bind(mappable.data, favoritesStorage)
            }
            is EventMarkerData -> (holder.itemView as EventItemView).apply {
                bind(mappable.data, favoritesStorage)
            }
        }
    }

    private var data = listOf<MapMarkerData<Mappable>>()

    fun setData(data: List<MapMarkerData<Mappable>>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].data.objectId.hashCode().toLong()

    override fun getItemViewType(position: Int) = data[position].data::class.hashCode()

    private fun Int.layout() = when (this) {
        Event::class.hashCode() -> R.layout.view_event_list_item
        Vendor::class.hashCode() -> R.layout.view_vendor_list_item
        else -> throw UnsupportedOperationException("Layout can not be gotten from $this")
    }
}
