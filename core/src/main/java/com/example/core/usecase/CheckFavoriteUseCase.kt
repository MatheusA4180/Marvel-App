package com.example.core.usecase

import com.example.core.usecase.base.ResultStatus
import kotlinx.coroutines.flow.Flow

interface CheckFavoriteUseCase {

    operator fun invoke(params: Params): Flow<ResultStatus<Boolean>>

    data class Params(val characterId: Int)

}
