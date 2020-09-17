package com.softwareforgood.pridefestival.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.ui.misc.BindableRecyclerAdapter
import com.softwareforgood.pridefestival.ui.misc.MapNavigator
import com.softwareforgood.pridefestival.ui.misc.SimpleListHeaderView
import com.softwareforgood.pridefestival.util.inflate
import com.softwareforgood.pridefestival.util.toDateString
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

abstract class EventsAdapter : BindableRecyclerAdapter<Event>(),
        StickyHeaderAdapter<BindableRecyclerAdapter.ViewHolder> {
    abstract fun loadEvents(events: List<Event>)
}

@EventsScope
class DefaultEventsAdapter @Inject constructor(
    private val favoritesStorage: FavoritesStorage,
    private val mapNavigator: MapNavigator
) : EventsAdapter() {

    private var headers = listOf<LocalDate>()
    private var events = listOf<Event>()

    init { setHasStableIds(true) }

    override fun loadEvents(events: List<Event>) {
        Timber.d("loadEvents() called with events = [%s]", events)
        val grouped = events.filter { it.startTime != null }
                .groupBy { it.startTime?.toLocalDate() }

        // invert the list and flatten so we have a list of all the headers that directly
        // correlate to the position of the events.
        // !! is safe since we filter out any possible nulls above
        this.headers = grouped.entries.map { (key, value) -> value.map { requireNotNull(key) } }.flatten()
        this.events = grouped.values.flatten()

        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size

    override fun newView(inflater: LayoutInflater, viewType: Int, parent: ViewGroup): View =
            inflater.inflate(R.layout.view_event_list_item, parent, false)

    override fun getItem(position: Int): Event = events[position]

    override fun bindView(item: Event, view: View, position: Int) =
            with((view as EventItemView)) {
                setOnClickListener { mapNavigator.navigateToEvent(item) }
                bind(item, favoritesStorage)
            }

    override fun onBindHeaderViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as SimpleListHeaderView).bind(
                headers[position].toDateString()
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): ViewHolder =
            ViewHolder(parent.inflate(R.layout.view_simple_list_header))

    override fun getHeaderId(position: Int) = headers[position]
            .hashCode()
            .toLong()

    override fun getItemId(position: Int) = events[position].objectId.hashCode().toLong()
}
