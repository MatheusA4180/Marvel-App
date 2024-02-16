package com.example.core.usecase.impl

import com.example.core.data.mapper.SortingMapper
import com.example.core.data.repository.StorageRepository
import com.example.core.usecase.SaveCharactersSortingUseCase
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SaveCharactersSortingUseCaseImpl @Inject constructor(
    private val storageRepository: StorageRepository,
    private val sortingMapper: SortingMapper,
    private val dispatchers: CoroutinesDispatchers
) : UseCase<SaveCharactersSortingUseCase.Params, Unit>(), SaveCharactersSortingUseCase {

    override suspend fun doWork(params: SaveCharactersSortingUseCase.Params): ResultStatus<Unit> {
        return withContext(dispatchers.io()) {
            storageRepository.saveSorting(sortingMapper.mapFromPair(params.sortingPair))
            ResultStatus.Success(Unit)
        }
    }
}
