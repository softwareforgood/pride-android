package com.softwareforgood.pridefestival.ui


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.f2prateek.rx.preferences2.Preference
import com.jakewharton.processphoenix.ProcessPhoenix
import com.softwareforgood.pridefestival.UseStagingUrlPref
import com.softwareforgood.pridefestival.util.HasComponent
import com.softwareforgood.pridefestival.util.makeActivityComponent
import com.softwareforgood.pridefestival.util.showLongToast
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Activity that receives deep links to the urls that match pride://env/\*
 * If the first path segment is stage the staging url will be used when the application
 * is relaunched. Any other path segment will default to the use of the production url.
 */
class DeepLinkActivity : AppCompatActivity(), HasComponent<ActivityComponent> {

    @Inject @UseStagingUrlPref lateinit var useStagingUrl: Preference<Boolean>

    private val intentDataPathSegments: List<String> by lazy { intent?.data?.pathSegments.orEmpty() }

    private var disposeable = Disposables.empty()

    override val component: ActivityComponent by lazy { makeActivityComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        val useStage = intentDataPathSegments.firstOrNull() == "stage"

        showLongToast(if (useStage) "Set to use staging url!" else "Set to use production url!")

        useStagingUrl.set(useStage)

        // wait 2 second to allow toast to display
        // then relaunch the application with the newly set url.
        disposeable = Completable.timer(2L, TimeUnit.SECONDS)
                .subscribe {
                    val nextIntent = Intent(this, MainActivity::class.java)
                    ProcessPhoenix.triggerRebirth(this, nextIntent)
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeable.dispose()
    }
}
