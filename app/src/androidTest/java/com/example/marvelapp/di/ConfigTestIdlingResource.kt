package com.example.marvelapp.di

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
object ConfigTestIdlingResource {

    @Provides
    fun provideFlipperCharactersIdlingResource()
            : FlipperCharactersIdlingResource = FlipperCharactersIdlingResource

    @Provides
    fun provideRecyclerCharactersIdlingResource()
            : RecyclerCharactersIdlingResource = RecyclerCharactersIdlingResource

    @Provides
    fun provideRequestCharactersIdlingResource()
            : RequestCharactersIdlingResource = RequestCharactersIdlingResource

    @Provides
    fun provideRequestDetailIdlingResource()
            : RequestDetailIdlingResource = RequestDetailIdlingResource

}
