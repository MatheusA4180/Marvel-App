package com.example.core.usecase.impl

import com.example.core.data.repository.FavoritesRepository
import com.example.core.domain.model.Character
import com.example.core.usecase.AddFavoriteUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddFavoriteUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val dispatchers: CoroutinesDispatchers
) : UseCase<AddFavoriteUseCase.Params, Unit>(), AddFavoriteUseCase {

    override suspend fun doWork(params: AddFavoriteUseCase.Params): ResultStatus<Unit> {
        return withContext(dispatchers.io()) {
            favoritesRepository.saveFavorite(
                Character(params.characterId, params.name, params.imageUrl, params.description)
            )
            ResultStatus.Success(Unit)
        }
    }
}
