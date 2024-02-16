package com.example.marvelapp.presentation.detail

import androidx.lifecycle.ViewModel
import com.example.core.usecase.GetCharacterCategoriesUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    getCharacterCategoriesUseCase: GetCharacterCategoriesUseCase,
    coroutinesDispatchers: CoroutinesDispatchers
) : ViewModel() {


    val categories = UiActionStateLiveData(
        getCharacterCategoriesUseCase,
        coroutinesDispatchers.main()
    )

}
