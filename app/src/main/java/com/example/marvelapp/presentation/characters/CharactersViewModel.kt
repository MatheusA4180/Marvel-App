package com.example.marvelapp.presentation.characters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.core.domain.model.Character
import com.example.core.usecase.GetCharactersUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.marvelapp.util.idlingresource.singleton.FlipperCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RequestCharactersIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val coroutinesDispatchers: CoroutinesDispatchers,
    private val flipperCharactersIdlingResource: FlipperCharactersIdlingResource?,
    private val requestCharactersIdlingResource: RequestCharactersIdlingResource?
) : ViewModel() {

    var currentSearchQuery = DEFAULT_VALUE_SEARCH

    private val action = MutableLiveData<Action>()
    val state: LiveData<UiState> = action
        .switchMap<Action, UiState> { action ->
            flipperCharactersIdlingResource?.increment()
            requestCharactersIdlingResource?.increment()
            val result: LiveData<UiState> = when (action) {
                is Action.Search, Action.Sort -> {
                    getCharactersUseCase(
                        GetCharactersUseCase.GetCharactersParams(
                            currentSearchQuery,
                            getPageConfig()
                        )
                    ).cachedIn(viewModelScope).map {
                        UiState.SearchResult(it)
                    }.asLiveData(coroutinesDispatchers.main())
                }
            }
            requestCharactersIdlingResource?.decrement()
            result
        }

    private fun getPageConfig() = PagingConfig(
        pageSize = 20
    )

    fun searchCharacters() {
        action.value = Action.Search
    }

    fun applySort() {
        action.value = Action.Sort
    }

    fun closeSearch() {
        if (currentSearchQuery.isNotEmpty()) {
            currentSearchQuery = DEFAULT_VALUE_SEARCH
        }
    }

    sealed class UiState {
        data class SearchResult(val data: PagingData<Character>) : UiState()
    }

    sealed class Action {
        object Search : Action()
        object Sort : Action()
    }

    companion object {
        private const val DEFAULT_VALUE_SEARCH = ""
    }
}
