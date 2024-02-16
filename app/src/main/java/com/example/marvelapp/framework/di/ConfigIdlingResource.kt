package com.example.marvelapp.framework.di

import com.example.marvelapp.util.idlingresource.singleton.FlipperCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RecyclerCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RequestCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RequestDetailIdlingResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ConfigIdlingResource {

    private val DEFAULT_VALUE_IDLING_RESOURCES_PROD = null

    @Provides
    fun provideFlipperCharactersIdlingResource()
            : FlipperCharactersIdlingResource? = DEFAULT_VALUE_IDLING_RESOURCES_PROD

    @Provides
    fun provideRecyclerCharactersIdlingResource()
            : RecyclerCharactersIdlingResource? = DEFAULT_VALUE_IDLING_RESOURCES_PROD

    @Provides
    fun provideRequestCharactersIdlingResource()
            : RequestCharactersIdlingResource? = DEFAULT_VALUE_IDLING_RESOURCES_PROD

    @Provides
    fun provideRequestDetailIdlingResource()
            : RequestDetailIdlingResource? = DEFAULT_VALUE_IDLING_RESOURCES_PROD

}
