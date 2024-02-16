package com.example.marvelapp.util.idlingresource.singleton

import androidx.test.espresso.idling.CountingIdlingResource
import com.example.marvelapp.util.idlingresource.constant.ResourceNameConstants

object RequestDetailIdlingResource {

    private const val RESOURCE = ResourceNameConstants.RESOURCE_NAME_DETAIL_REQUEST

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
