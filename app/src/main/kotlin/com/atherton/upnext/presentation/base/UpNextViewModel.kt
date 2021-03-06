package com.atherton.upnext.presentation.base

import com.jakewharton.rxrelay2.PublishRelay
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import io.reactivex.Observable

abstract class UpNextViewModel<A : BaseAction, S : BaseState, V : BaseViewEffect> : BaseViewModel<A, S>() {

    protected val viewEffects: PublishRelay<V> = PublishRelay.create()

    fun viewEffects(): Observable<V> = viewEffects

    protected inline fun postViewEffect(viewEffect: () -> V) {
        viewEffects.accept(viewEffect.invoke())
    }
}
