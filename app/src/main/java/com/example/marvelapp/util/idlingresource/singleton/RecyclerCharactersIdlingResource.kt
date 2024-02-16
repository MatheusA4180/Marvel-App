package com.example.marvelapp.util.idlingresource.singleton

import androidx.test.espresso.idling.CountingIdlingResource
import com.example.marvelapp.util.idlingresource.constant.ResourceNameConstants

object RecyclerCharactersIdlingResource {

    private const val RESOURCE = ResourceNameConstants.RESOURCE_NAME_CHARACTER_RECYCLER

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }

}
