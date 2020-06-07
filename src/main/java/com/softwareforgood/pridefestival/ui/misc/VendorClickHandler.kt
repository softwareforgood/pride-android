package com.softwareforgood.pridefestival.ui.misc

import com.softwareforgood.pridefestival.ApplicationScope
import com.softwareforgood.pridefestival.data.model.Vendor
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@ApplicationScope
class VendorClickHandler @Inject constructor() {

    private val vendorStream = PublishSubject.create<Vendor>()

    fun publishClick(vendor: Vendor) {
        vendorStream.onNext(vendor)
    }

    fun vendorClicks(): Observable<Vendor> = vendorStream
}
