package com.atherton.tmdb.util.base

import com.atherton.tmdb.util.injection.PerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

@PerView
abstract class BasePresenter<View : BaseView> {

    protected var view: View? = null
        private set

    protected val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    fun viewAttached(view: View) {
        this.view = view
        onViewAttached()
    }

    fun viewDetached() {
        if (view == null) {
            throw IllegalStateException("View is already detached.")
        }
        compositeDisposable.clear()
        onViewDetached()
        view = null
    }

    protected open fun onViewAttached() {}

    protected open fun onViewDetached() {}

    protected inline fun addToAutoUnsubscribe(action: () -> Disposable?) {
        action()?.let { compositeDisposable.add(it) }
    }
}