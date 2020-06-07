package com.softwareforgood.pridefestival.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.R
import kotlin.reflect.KClass

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(resource, this, attachToRoot)!!

fun View.getColor(@ColorRes color: Int) = ContextCompat.getColor(context, color)
fun View.getDrawable(@DrawableRes drawable: Int) = ContextCompat.getDrawable(context, drawable)

fun View.getString(@StringRes id: Int): String = context.getString(id)
fun View.getString(@StringRes id: Int, vararg formatArgs: Any): String = context.getString(id, *formatArgs)

// divider is horizontal but our layout manager is vertical
fun RecyclerView.horizontalDivider() =
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun <T : Activity> Activity.launchActivity(activityClass: KClass<T>) = startActivity(Intent(this, activityClass.java))

fun Uri.launchCustomTab(context: Context) {
    val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.accent))
            .build()
    customTabsIntent.launchUrl(context, this)
}
