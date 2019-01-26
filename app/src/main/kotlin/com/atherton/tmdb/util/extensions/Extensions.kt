package com.atherton.tmdb.util.extensions

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.atherton.tmdb.App
import com.atherton.tmdb.util.injection.AppComponent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.Executors

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun ViewGroup.inflateLayout(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun CharSequence?.notNullOrBlank(): Boolean = this != null && !this.isBlank()

internal fun Context.getAppComponent(): AppComponent {
    return if (this is Application) {
        (this as App).appComponent
    } else {
        (this.applicationContext as App).appComponent
    }
}

internal fun Fragment.getAppComponent(): AppComponent = (this.context!!.getAppComponent())

internal fun View.getAppComponent(): AppComponent = (this.context.getAppComponent())

inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value) }
        is Long -> edit { it.putLong(key, value) }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
    this.observe(owner, Observer(observer))
}

fun <T : ViewModel> Fragment.getViewModel(vmFactory: ViewModelProvider.Factory, modelClass: Class<T>): T {
    return ViewModelProviders.of(this, vmFactory).get(modelClass)
}

fun <T : ViewModel> Fragment.getActivityViewModel(vmFactory: ViewModelProvider.Factory, modelClass: Class<T>): T {
    return ViewModelProviders.of(activity!!, vmFactory).get(modelClass)
}

fun <T : ViewModel> AppCompatActivity.getViewModel(vmFactory: ViewModelProvider.Factory, modelClass: Class<T>): T {
    return ViewModelProviders.of(this, vmFactory).get(modelClass)
}

fun View.showShortSnackbar(@StringRes stringResId: Int) {
    Snackbar.make(this, stringResId, Snackbar.LENGTH_SHORT).show()
}

fun View.showLongSnackbar(@StringRes stringResId: Int) {
    Snackbar.make(this, stringResId, Snackbar.LENGTH_LONG).show()
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

fun FloatingActionButton.show(show: Boolean) {
    if (show) show() else hide()
}

fun ioThread(block : () -> Unit) {
    Executors.newSingleThreadExecutor().execute(block)
}
