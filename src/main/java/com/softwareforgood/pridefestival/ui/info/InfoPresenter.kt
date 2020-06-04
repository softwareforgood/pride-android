package com.softwareforgood.pridefestival.ui.info

import androidx.annotation.VisibleForTesting
import com.softwareforgood.pridefestival.data.InfoLoader
import com.softwareforgood.pridefestival.ui.mvp.Presenter
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import io.reactivex.disposables.Disposables
import timber.log.Timber
import javax.inject.Inject

abstract class InfoPresenter : Presenter<InfoView>()

@InfoScope
class DefaultInfoPresenter @Inject constructor(
    private val infoLoader: InfoLoader
) : InfoPresenter() {

    @VisibleForTesting
    var eventsDisposable = Disposables.empty()

    override fun onViewAttached() {
        Timber.d("onViewAttached() called")
        loadInfo()

        view.tryAgainButton.setOnClickListener {
            view.showSpinner()
            loadInfo()
        }
    }

    override fun onViewDetached() {
        Timber.d("onViewDetached() called")
        eventsDisposable.dispose()
    }

    private fun loadInfo() {
        eventsDisposable = infoLoader.infoText
                .observeOnAndroidScheduler()
                .subscribe(::showInfo, ::handleError)
    }

    private fun showInfo(infoText: String) {
        Timber.d("showInfo() called with infoText = %s", infoText)
        view.showInfo(infoText)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "Error loading info")
        view.showError()
    }
}
