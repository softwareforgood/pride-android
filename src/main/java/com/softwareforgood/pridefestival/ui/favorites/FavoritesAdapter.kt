package com.softwareforgood.pridefestival.ui.favorites

import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.HasParseId
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.ui.events.EventItemView
import com.softwareforgood.pridefestival.ui.misc.SimpleListHeaderView
import com.softwareforgood.pridefestival.ui.parade.ParadeItemView
import com.softwareforgood.pridefestival.ui.vendor.VendorItemView
import com.softwareforgood.pridefestival.util.inflate
import com.softwareforgood.pridefestival.util.toDateString
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

sealed class FavoriteData<out T : HasParseId> {
    abstract val data: T
}

data class EventData(override val data: Event) : FavoriteData<Event>()
data class VendorData(override val data: Vendor) : FavoriteData<Vendor>()
data class ParadeData(override val data: ParadeEvent) : FavoriteData<ParadeEvent>()

class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

abstract class FavoritesAdapter : RecyclerView.Adapter<FavoritesViewHolder>(),
        StickyHeaderAdapter<FavoritesViewHolder> {
    abstract fun loadData(searchParam: String = ""): Completable
}

@FavoritesScope
class DefaultFavoritesAdapter @Inject constructor(
    private val context: Application,
    private val favoritesStorage: FavoritesStorage
) : FavoritesAdapter() {

    private var headers = listOf<String>()
    private var data = listOf<FavoriteData<HasParseId>>()

    init { setHasStableIds(true) }

    override fun loadData(searchParam: String): Completable {
        val eventData = favoritesStorage.events
                .doOnSuccess { Timber.v("loaded events = [%s]", it) }
                .toObservable()
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .flatMapIterable { it }
                .filter { it.startTime != null }
                .groupBy { it.startTime?.toLocalDate() }
                .flatMapSingle { group ->
                    group.map { event -> EventData(event) }
                            .toList()
                            .map { group.key!! to it }
                }
                .map { (key, eventData) ->
                    context.getString(R.string.favorite_events_header, key.toDateString()) to eventData
                }
                .toList()
                .map { it.sortBy { it.first }; it }
                .toObservable()
                .flatMapIterable { it }

        val paradeData = favoritesStorage.parades
                .doOnSuccess { Timber.v("loaded parades = [%s]", it) }
                .toObservable()
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .flatMapIterable { it }
                .map(::ParadeData)
                .toList()
                .map { context.getString(R.string.parade) to it }

        val vendorData = favoritesStorage.vendors
                .doOnSuccess { Timber.v("loaded vendors = [%s]", it) }
                .toObservable()
                .map { if (searchParam.isBlank()) it else it.filter { it.name.contains(searchParam, ignoreCase = true) } }
                .flatMapIterable { it }
                .map(::VendorData)
                .toList()
                .map { context.getString(R.string.vendors) to it }

        return Observable.concat(eventData, paradeData.toObservable(), vendorData.toObservable())
                .toList()
                .map { it.toMap() }
                .flatMapCompletable { grouped ->
                    Completable.fromAction {
                        headers = grouped.entries.map { (key, value) ->
                            value.map { key }
                        }.flatten()
                        data = grouped.values.flatten()
                        Timber.v("headers = [%s]", headers)
                        Timber.v("data = [%s]", data)
                        notifyDataSetChanged()
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                }
    }

    override fun getHeaderId(position: Int) = headers[position].hashCode().toLong()

    override fun onCreateHeaderViewHolder(parent: ViewGroup): FavoritesViewHolder =
            FavoritesViewHolder(parent.inflate(R.layout.view_simple_list_header))

    override fun onBindHeaderViewHolder(viewholder: FavoritesViewHolder, position: Int) {
        (viewholder.itemView as SimpleListHeaderView).bind(headers[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            FavoritesViewHolder(parent.inflate(viewType.layout()))

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = data[position]
        val view = holder.itemView

        // the storage wrappers COULD cause performance issues. If there is noticeable jank
        // these could be made once and recycled like everything else.
        item.executeOn(
                event = { (view as EventItemView).bind(this, eventFavoriteStorageWrapper(item, holder)) },
                vendor = { (view as VendorItemView).bind(this, vendorFavoriteStorageWrapper(item, holder)) },
                parade = { (view as ParadeItemView).bind(this, paradeFavoriteStorageWrapper(item, holder)) }
        )
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position]::class.hashCode()

    override fun getItemId(position: Int) = data[position].data.objectId.hashCode().toLong()

    private fun <T : Any> FavoriteData<HasParseId>.executeOn(
        event: Event.() -> T,
        vendor: Vendor.() -> T,
        parade: ParadeEvent.() -> T
    ) = when (this) {
            is EventData -> event(data)
            is VendorData -> vendor(data)
            is ParadeData -> parade(data)
        }

    private fun eventFavoriteStorageWrapper(item: FavoriteData<HasParseId>, viewHolder: FavoritesViewHolder) =
            object : FavoritesStorage by favoritesStorage {
                override fun deleteEvent(event: Event) = favoritesStorage.deleteEvent(event)
                        .andThen(removeItem(item, viewHolder.layoutPosition))
            }

    private fun vendorFavoriteStorageWrapper(item: FavoriteData<HasParseId>, viewHolder: FavoritesViewHolder) =
            object : FavoritesStorage by favoritesStorage {
                override fun deleteVendor(vendor: Vendor) = favoritesStorage.deleteVendor(vendor)
                        .andThen(removeItem(item, viewHolder.layoutPosition))
            }

    private fun paradeFavoriteStorageWrapper(item: FavoriteData<HasParseId>, viewHolder: FavoritesViewHolder) =
            object : FavoritesStorage by favoritesStorage {
                override fun deleteParade(parade: ParadeEvent) = favoritesStorage.deleteParade(parade)
                        .andThen(removeItem(item, viewHolder.layoutPosition))
            }

    private fun removeItem(item: FavoriteData<HasParseId>, position: Int) =
            Completable.create {
                data -= item
                headers -= headers[position]
                notifyItemRemoved(position)
            }.subscribeOn(AndroidSchedulers.mainThread())

    private fun Int.layout() = when (this) {
        EventData::class.hashCode() -> R.layout.view_event_list_item
        VendorData::class.hashCode() -> R.layout.view_vendor_list_item
        ParadeData::class.hashCode() -> R.layout.view_parade_list_item
        else -> throw UnsupportedOperationException("Layout can not be gotten from $this")
    }
}
