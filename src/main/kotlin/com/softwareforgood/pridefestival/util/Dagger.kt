package com.softwareforgood.pridefestival.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.softwareforgood.pridefestival.PrideApp
import com.softwareforgood.pridefestival.ui.ActivityComponent
import io.reactivex.Single

/**
 * Interface to express that an object is the owner of a component. You can get a reference to this
 * component to build a subcomponent.
 *
 *
 * The component method should always return the same component. Typical practice is to store the
 * component as a private field. In your `component` implementation, check if the field is null, and
 * if so, instantiate the component object.
 */
interface HasComponent<out T> {
    val component: T
}

fun <T> T.makeComponent(
    searchViewProvider: Single<SearchView>? = null
): ActivityComponent where T : HasComponent<ActivityComponent>,
                                T : AppCompatActivity = (application as PrideApp)
        .component
        .activityComponentBuilder()
        .activity(this)
        .searchViewProvider(searchViewProvider ?: Single.error<SearchView>(Throwable("Search view not provided by the activity")))
        .build()

@Suppress("UNCHECKED_CAST")
val Context.activityComponent
    get() = (this as HasComponent<ActivityComponent>).component
