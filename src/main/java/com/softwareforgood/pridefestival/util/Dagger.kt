package com.softwareforgood.pridefestival.util

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.softwareforgood.pridefestival.PrideApp
import com.softwareforgood.pridefestival.ui.ActivityComponent

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

fun <T> T.makeActivityComponent(): ActivityComponent where T : HasComponent<*>, T : AppCompatActivity = (application as PrideApp)
    .component
    .activityComponentBuilder()
    .activity(this)
    .build()

@Suppress("UNCHECKED_CAST")
fun <T> View.component(): T = (context as HasComponent<T>).component
