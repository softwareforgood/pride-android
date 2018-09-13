package com.softwareforgood.pridefestival.ui.parade

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.ui.misc.BindableRecyclerAdapter
import javax.inject.Inject

abstract class ParadeAdapter : BindableRecyclerAdapter<ParadeEvent>() {
    abstract fun loadParadeEvents(paradeEvents: List<ParadeEvent>)
}

@ParadeScope
class DefaultParadeAdapter @Inject constructor(
    private val favoritesStorage: FavoritesStorage
) : ParadeAdapter() {

    private var paradeEvents = listOf<ParadeEvent>()

    init { setHasStableIds(true) }

    override fun loadParadeEvents(paradeEvents: List<ParadeEvent>) {
        this.paradeEvents = paradeEvents
        notifyDataSetChanged()
    }

    override fun getItemCount() = paradeEvents.size

    override fun getItem(position: Int) = paradeEvents[position]

    override fun newView(inflater: LayoutInflater, viewType: Int, parent: ViewGroup) =
            inflater.inflate(R.layout.view_parade_list_item, parent, false)!!

    override fun bindView(item: ParadeEvent, view: View, position: Int) =
            (view as ParadeItemView).bind(item, favoritesStorage)

    override fun getItemId(position: Int) = paradeEvents[position].objectId.hashCode().toLong()
}
