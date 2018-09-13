package com.softwareforgood.pridefestival

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import com.jakewharton.threetenabp.AndroidThreeTen
import com.parse.Parse
import com.softwareforgood.pridefestival.util.HasComponent
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import io.reactivex.disposables.Disposables
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

open class PrideApp : Application(), HasComponent<PrideAppComponent> {
    private lateinit var _component: PrideAppComponent
    override val component: PrideAppComponent by lazy { _component }

    private var disposable = Disposables.empty()

    override fun onCreate() {
        super.onCreate()

        if (ProcessPhoenix.isPhoenixProcess(this)) return

        _component = DaggerPrideAppComponent.builder()
                .application(this)
                .build()

        initialize(this)

        // handle any exceptions that may happen after onError was called
        RxJavaPlugins.setErrorHandler { throwable ->
            Timber.e(throwable, "Unhandled exception")
        }

        AndroidThreeTen.init(this)
        with(component) {
            firstRunCachingService.cacheParseObjects()
            Parse.initialize(parseConfig)
        }

        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs
        )

        val config = FontRequestEmojiCompatConfig(this, fontRequest)
        EmojiCompat.init(config)
    }

    override fun onTerminate() {
        super.onTerminate()
        disposable.dispose()
    }
}
