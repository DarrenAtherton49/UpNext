package com.atherton.upnext.util.extensions

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.atherton.upnext.App
import com.atherton.upnext.util.injection.AppComponent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.Moshi
import java.util.concurrent.Executors

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun ViewGroup.inflateLayout(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

internal fun Context.getAppComponent(): AppComponent {
    return if (this is Application) {
        (this as App).appComponent
    } else {
        (this.applicationContext as App).appComponent
    }
}

internal fun Fragment.getAppComponent(): AppComponent = (this.requireContext().getAppComponent())

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

inline fun <reified T : ViewModel> Fragment.getViewModel(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, vmFactory).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getActivityViewModel(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(requireActivity(), vmFactory).get(T::class.java)
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(vmFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, vmFactory).get(T::class.java)
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

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.hideSoftKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun onAndroidPieOrLater(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

fun EditText.whenTextChanges(emitInitialValue: Boolean = false, block: (String) -> Unit) {
    if (emitInitialValue) {
        block.invoke(this.text.toString())
    }
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { block.invoke(it.toString()) }
        }
    })
}

inline fun <reified T> Moshi.adapt(json: String): T? = this.adapter(T::class.java).fromJson(json)

fun TextView.setTextOrHide(text: String?) {
    isVisible = if (text != null) {
        setText(text)
        true
    } else {
        false
    }
}

fun Context.readFileFromAssets(rawPath: Int): String {
    return this.resources.openRawResource(rawPath)
        .bufferedReader()
        .use { it.readText() }
}

fun ioThread(block: () -> Unit) {
    Executors.newSingleThreadExecutor().execute(block)
}