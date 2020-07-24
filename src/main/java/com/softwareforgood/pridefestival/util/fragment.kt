package com.softwareforgood.pridefestival.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

inline fun <reified T> FragmentManager.getFragmentByTag(tag: String): T = findFragmentByTag(tag) as T

val Fragment.actionbar get() = (activity as AppCompatActivity).supportActionBar

fun Fragment.setSupportActionBar(toolbar: Toolbar) = (activity as AppCompatActivity).setSupportActionBar(toolbar)
fun Fragment.setDisplayHomeAsUpEnabled(boolean: Boolean) = actionbar?.setDisplayHomeAsUpEnabled(boolean)
fun Fragment.setDisplayShowHomeEnabled(boolean: Boolean) = actionbar?.setDisplayShowHomeEnabled(boolean)
var Fragment.toolBarTitle: CharSequence?
get() = actionbar?.title
set(value) { actionbar?.title = value }
