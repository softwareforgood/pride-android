package com.softwareforgood.pridefestival.ui.map

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class MapScope

@MapScope
@Subcomponent(modules = [MapModule::class])
interface MapComponent {
    fun inject(mapView: DefaultMapView)
}

@Module abstract class MapModule {
    @Binds abstract fun bindMapPresenter(presenter: DefaultMapPresenter): MapPresenter
}
