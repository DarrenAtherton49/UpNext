package com.atherton.upnext.presentation.util.extension

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.preventMultipleClicks(): Observable<T> {
    return this.throttleFirst(300, TimeUnit.MILLISECONDS)
}

inline fun View.setOnGesturesListener(crossinline onDoubleTap: () -> Unit, crossinline onLongPress: () -> Unit) {
    val listener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTap.invoke()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            onLongPress.invoke()
        }
    }
    val detector = GestureDetector(context, listener).apply {
        setOnDoubleTapListener(listener)
        setIsLongpressEnabled(true)
    }
    this.setOnTouchListener { _, event -> detector.onTouchEvent(event) }
}

inline fun ViewPager.onPageChanged(crossinline block: (page: Int) -> Unit) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            block.invoke(position)
        }
    })
}
