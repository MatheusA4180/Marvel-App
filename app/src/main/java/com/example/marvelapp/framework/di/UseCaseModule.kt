package com.example.marvelapp.framework.di

import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.GetCharactersSortingUseCase
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.GetFavoritesUseCase
import com.example.core.usecase.RemoveFavoriteUseCase
import com.example.core.usecase.SaveCharactersSortingUseCase
import com.example.core.usecase.impl.AddFavoriteUseCaseImpl
import com.example.core.usecase.impl.CheckFavoriteUseCaseImpl
import com.example.core.usecase.impl.GetCharacterCategoriesUseCaseImpl
import com.example.core.usecase.impl.GetCharactersSortingUseCaseImpl
import com.example.core.usecase.impl.GetCharactersUseCaseImpl
import com.example.core.usecase.impl.GetFavoritesUseCaseImpl
import com.example.core.usecase.impl.RemoveFavoriteUseCaseImpl
import com.example.core.usecase.impl.SaveCharactersSortingUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    fun bindGetCharactersUseCase(useCase: GetCharactersUseCaseImpl): GetCharactersUseCase

    @Binds
    fun bindGetComicsAndEventUseCase(
        useCase: GetCharacterCategoriesUseCaseImpl
    ): GetCharacterCategoriesUseCase

    @Binds
    fun bindCheckFavoriteUseCase(useCase: CheckFavoriteUseCaseImpl): CheckFavoriteUseCase

    @Binds
    fun bindAddFavoriteUseCase(useCase: AddFavoriteUseCaseImpl): AddFavoriteUseCase

    @Binds
    fun bindRemoveFavoriteUseCase(useCase: RemoveFavoriteUseCaseImpl): RemoveFavoriteUseCase

    @Binds
    fun bindGetFavoritesUseCase(useCase: GetFavoritesUseCaseImpl): GetFavoritesUseCase

    @Binds
    fun bindGetCharactersSortingUseCase(
        useCase: GetCharactersSortingUseCaseImpl
    ): GetCharactersSortingUseCase

    @Binds
    fun bindSaveCharactersSortingUseCase(
        useCase: SaveCharactersSortingUseCaseImpl
    ): SaveCharactersSortingUseCase

}
