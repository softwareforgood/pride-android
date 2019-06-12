package com.softwareforgood.pridefestival.ui.vendor

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Vendor
import com.softwareforgood.pridefestival.data.model.VendorColor
import com.softwareforgood.pridefestival.data.model.VendorType
import com.softwareforgood.pridefestival.util.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_vendor_list_item.view.*
import timber.log.Timber

class VendorItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val disposables = CompositeDisposable()

    fun bind(vendor: Vendor, favoritesStorage: FavoritesStorage) {
        Timber.d("bind() called vendor = [%s]", vendor)

        with(vendor) {
            vendor_list_item_image_container.setBackgroundColor(sectionColor.toColorInt())
            vendor_list_item_title.text = name
            vendor_list_item_details.text = details?.trim()
            vendor_list_item_details.visibility = if (details == null) View.INVISIBLE else View.VISIBLE
            vendor_list_item_location.text = locationName
            vendor_list_item_location.setTextColor(sectionColor.toColorInt())

            vendor_list_item_website.visibility = if (website.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
            vendor_list_item_website.setOnClickListener {
                !website.isNullOrBlank() || return@setOnClickListener
                website?.toUri()?.launchCustomTab(context)
            }

            @DrawableRes val vendorImage: Int = when (vendorType) {
                VendorType.FOOD -> R.drawable.vendor_cell_type_food_icon
                VendorType.NON_FOOD -> R.drawable.vendor_cell_type_nonfood_icon
                VendorType.UNKNOWN -> R.drawable.cell_type_miscellaneous_icon
            }
            vendor_list_item_image.setImageResource(vendorImage)
        }

        disposables += favoritesStorage.hasVendor(vendor)
                .observeOnAndroidScheduler()
                .subscribe(handleFavoriteVendor)

        vendor_list_item_favorite_button.setOnClickListener {
            disposables += favoritesStorage.hasVendor(vendor)
                    .flatMap { hasVendor: Boolean ->
                        if (hasVendor) favoritesStorage.deleteVendor(vendor).toSingleDefault(!hasVendor)
                        else favoritesStorage.saveVendor(vendor).toSingleDefault(!hasVendor)
                    }
                    .observeOnAndroidScheduler()
                    .subscribe(handleFavoriteVendor)
        }
    }

    private fun VendorColor.toColorInt() = ResourcesCompat.getColor(resources, colorId, null)

    override fun onDetachedFromWindow() {
        disposables.clear()
        super.onDetachedFromWindow()
    }

    private val handleFavoriteVendor = { hasEvent: Boolean ->
        with(vendor_list_item_favorite_button) {
            if (hasEvent) setAsFavorited() else setAsNotFavorited()
        }
    }
}
