package com.example.marvelapp.presentation.detail.viewmodel

import androidx.lifecycle.ViewModel
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.RemoveFavoriteUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.marvelapp.util.idlingresource.singleton.RequestDetailIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase,
    checkFavoriteUseCase: CheckFavoriteUseCase,
    addFavoriteUseCase: AddFavoriteUseCase,
    removeFavoriteUseCase: RemoveFavoriteUseCase,
    coroutinesDispatchers: CoroutinesDispatchers,
    requestDetailIdlingResource: RequestDetailIdlingResource?
) : ViewModel() {


    val categories = UiActionStateLiveData(
        getCharacterCategoriesUseCase,
        coroutinesDispatchers.main(),
        requestDetailIdlingResource
    )

    val favorite = FavoriteUiActionStateLiveData(
        coroutinesDispatchers.main(),
        checkFavoriteUseCase,
        addFavoriteUseCase,
        removeFavoriteUseCase
    )

}
