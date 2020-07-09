package com.softwareforgood.pridefestival.ui.info

import com.softwareforgood.pridefestival.databinding.ActivityInfoBinding
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import javax.inject.Scope

@Scope annotation class InfoScope

@InfoScope
@Subcomponent(modules = [InfoModule::class])
interface InfoComponent {
    fun inject(view: DefaultInfoView)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun activityInfoBinding(binding: ActivityInfoBinding): Builder
        fun build(): InfoComponent
    }
}

@Module
abstract class InfoModule {
    @Binds abstract fun bindsInfoPresenter(presenter: DefaultInfoPresenter): InfoPresenter
}
