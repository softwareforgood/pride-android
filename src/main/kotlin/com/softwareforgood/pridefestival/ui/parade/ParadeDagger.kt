package com.softwareforgood.pridefestival.ui.parade

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class ParadeScope

@ParadeScope
@Subcomponent(modules = [ParadeModule::class])
interface ParadeComponent {
    fun inject(paradeView: DefaultParadeView)
}

@Module abstract class ParadeModule {
    @Binds abstract fun bindParadePresenter(presenter: DefaultParadePresenter): ParadePresenter
    @Binds abstract fun bindParadeAdapter(adapter: DefaultParadeAdapter): ParadeAdapter
}
