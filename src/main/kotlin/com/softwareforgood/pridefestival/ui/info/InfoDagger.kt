package com.softwareforgood.pridefestival.ui.info

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class InfoScope

@InfoScope
@Subcomponent(modules = [InfoModule::class])
interface InfoComponent {
    fun inject(view: DefaultInfoView)
}

@Module
abstract class InfoModule {
    @Binds abstract fun bindsInfoPresenter(presenter: DefaultInfoPresenter): InfoPresenter
}
