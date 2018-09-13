package com.softwareforgood.pridefestival.ui.events

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class EventsScope

@EventsScope
@Subcomponent(modules = [EventsModule::class])
interface EventsComponent {
    fun inject(view: DefaultEventsView)
}

@Module
abstract class EventsModule {
    @Binds abstract fun bindsEventsPresenter(presenter: DefaultEventsPresenter): EventsPresenter
    @Binds abstract fun bindsEventsAdapter(adapter: DefaultEventsAdapter): EventsAdapter

    @Module
    companion object {
        @JvmStatic
        @EventsScope
        @Provides
        fun provideStickyHeaderDecoration(adapter: EventsAdapter) = StickyHeaderDecoration(adapter)
    }
}
