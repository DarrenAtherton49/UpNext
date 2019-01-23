package com.atherton.tmdb.util.livedata

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * This class serves as a replacement for 'SingleLiveEvent', in that it allows us to
 * use 'getContentIfNotHandled()' to only react to events the first time they first,
 * but also allows other observers to 'peek' at the content if the want to.
 */
class SingleEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(consumer: (T) -> Unit) {
        if (!hasBeenHandled) {
            hasBeenHandled = true
            consumer(content)
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}