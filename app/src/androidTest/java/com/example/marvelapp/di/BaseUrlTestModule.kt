package com.example.marvelapp.di

import com.example.marvelapp.BuildConfig
import com.example.marvelapp.framework.di.qualifier.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BaseUrlTestModule {

    private const val FAKE_URL = "http://localhost:8080/"

    @BaseUrl
    @Provides
    fun provideBaseUrl(): String = FAKE_URL

}
