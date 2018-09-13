package com.softwareforgood.pridefestival.data

import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.Reusable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface InfoLoader {
    val infoText: Single<String>
}

@Reusable
class DefaultInfoLoader @Inject constructor() : InfoLoader {
    override val infoText: Single<String>
        get() = Single.fromCallable {
            ParseQuery.getQuery<ParseObject>("Info")
                    .fromLocalDatastore()
                    .first
                    .getString("text")
        }
        .timeout(3, TimeUnit.SECONDS)
        .onErrorReturn {
            val info = ParseQuery.getQuery<ParseObject>("Info").first
            info.pinInBackground { e -> if (e != null) Timber.e(e, "Error saving info object") }
            info.getString("text")
        }
        // remove all \n and replace to <br /> so the entire string is valid html
        .map { it.replace("\n", "<br />") }
        .subscribeOn(Schedulers.io())
}
