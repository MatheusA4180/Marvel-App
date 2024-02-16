package com.example.marvelapp.extension

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import kotlinx.coroutines.CoroutineScope

object IdlingResourceUtil {
    fun CoroutineScope.registerIdlingResources(vararg idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().register(*idlingResource)
    }

    fun CoroutineScope.unregisterIdlingResources(vararg idlingResource: IdlingResource) {
        IdlingRegistry.getInstance().unregister(*idlingResource)
    }
}
