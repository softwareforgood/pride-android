package com.softwareforgood.pridefestival.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.softwareforgood.pridefestival.R
import kotlin.reflect.KClass


val Context.defaultSharedPreference: SharedPreferences get() =
    getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(resource, this, attachToRoot)!!

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)

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

@ColorInt
fun Context.colorAttribute(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@get:ColorInt
val Context.colorPrimary: Int get() = colorAttribute(R.attr.colorPrimary)

fun Uri.launchCustomTab(context: Context) {
    val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(context.colorPrimary)
            .build()
    customTabsIntent.launchUrl(context, this)
}
