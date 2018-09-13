package com.softwareforgood.pridefestival.ui.favorites

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class FavoritesScope

@FavoritesScope
@Subcomponent(modules = [FavoritesModule::class])
interface FavoritesComponent {
    fun inject(favoritesView: DefaultFavoritesView)
}

@Module abstract class FavoritesModule {
    @Binds abstract fun bindFavoriteAdapter(favoritesAdapter: DefaultFavoritesAdapter): FavoritesAdapter

    @Module
    companion object {
        @JvmStatic
        @FavoritesScope
        @Provides
        fun provideStickyHeaderDecoration(adapter: FavoritesAdapter) = StickyHeaderDecoration(adapter)
    }
}
