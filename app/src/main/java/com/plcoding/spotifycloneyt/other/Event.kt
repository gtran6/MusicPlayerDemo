package com.plcoding.spotifycloneyt.other

open class Event<out T>(private val data: T) {

    var hesBeenHandled = false
        private set

    fun getContentIfNotHandled() : T? {
        return if (hesBeenHandled) {
            null
        } else {
            hesBeenHandled = true
            data
        }
    }
    fun peekContent() = data
}