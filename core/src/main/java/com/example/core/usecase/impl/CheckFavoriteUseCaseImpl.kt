package com.example.core.usecase.impl

import com.example.core.data.repository.FavoritesRepository
import com.example.core.usecase.CheckFavoriteUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckFavoriteUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: CoroutinesDispatchers
) : UseCase<CheckFavoriteUseCase.Params, Boolean>(), CheckFavoriteUseCase {

    override suspend fun doWork(params: CheckFavoriteUseCase.Params): ResultStatus<Boolean> {
        return withContext(dispatchers.io()) {
            val isFavorite = favoritesRepository.isFavorite(params.characterId)
            ResultStatus.Success(isFavorite)
        }
    }
}
