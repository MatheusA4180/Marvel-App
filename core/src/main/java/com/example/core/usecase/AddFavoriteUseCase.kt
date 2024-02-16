package com.example.core.usecase

import com.example.core.usecase.base.ResultStatus
import kotlinx.coroutines.flow.Flow

interface AddFavoriteUseCase {

    operator fun invoke(params: Params): Flow<ResultStatus<Unit>>

    data class Params(
        val characterId: Int,
        val name: String,
        val imageUrl: String,
        val description: String
    )
}
