package com.example.core.usecase

import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.usecase.base.ResultStatus
import kotlinx.coroutines.flow.Flow

interface GetCharacterCategoriesUseCase {

    operator fun invoke(params: GetCategoriesParams): Flow<ResultStatus<Pair<List<Comic>, List<Event>>>>

    data class GetCategoriesParams(val characterId: Int)
}
