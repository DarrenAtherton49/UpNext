package com.atherton.upnext.util.base

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class UpNextViewModel<A : BaseAction, S : BaseState, V : BaseViewEffect> : BaseViewModel<A, S>() {

    protected val viewEffects: PublishSubject<V> = PublishSubject.create<V>()

    fun viewEffects(): Observable<V> = viewEffects
}
