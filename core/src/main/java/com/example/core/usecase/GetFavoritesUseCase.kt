package com.example.core.usecase

import com.example.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface GetFavoritesUseCase {
    suspend operator fun invoke(params: Unit = Unit): Flow<List<Character>>
}
