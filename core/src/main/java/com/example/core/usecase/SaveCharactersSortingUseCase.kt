package com.example.core.usecase

import com.example.core.usecase.base.ResultStatus
import kotlinx.coroutines.flow.Flow

interface SaveCharactersSortingUseCase {

    operator fun invoke(params: Params): Flow<ResultStatus<Unit>>

    data class Params(val sortingPair: Pair<String, String>)
}
