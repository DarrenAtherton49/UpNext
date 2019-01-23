package com.atherton.tmdb.util.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

    protected val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    protected inline fun addToAutoUnsubscribe(action: () -> Disposable?) {
        action()?.let { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}