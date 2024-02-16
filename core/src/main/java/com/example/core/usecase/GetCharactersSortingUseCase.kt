package com.example.core.usecase

import kotlinx.coroutines.flow.Flow

interface GetCharactersSortingUseCase {
    suspend operator fun invoke(params: Unit = Unit): Flow<Pair<String, String>>
}
