package com.softwareforgood.pridefestival.ui.vendor

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class VendorScope

@VendorScope
@Subcomponent(modules = [VendorModule::class])
interface VendorComponent {
    fun inject(vendorView: DefaultVendorView)
}

@Module abstract class VendorModule {
    @Binds abstract fun bindVendorPresenter(vendorPresenter: DefaultVendorPresenter): VendorPresenter
    @Binds abstract fun bindVendorAdapter(vendorAdapter: DefaultVendorAdapter): VendorAdapter

    @Module
    companion object {
        @JvmStatic
        @VendorScope
        @Provides
        fun provideStickyHeaderDecoration(adapter: VendorAdapter) = StickyHeaderDecoration(adapter)
    }
}
