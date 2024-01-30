package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character

class CharactersPagingSource(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val query: String
) : PagingSource<Int, Character>() {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        try {
            val offset = params.key ?: 0
            val queries = hashMapOf(
                "offset" to offset.toString()
            )
            if (query.isNotEmpty()) {
                queries["NameStartsWith"] = query
            }

            val response = remoteDataSource.fetchCharacters(queries)

            val responseOffset = response.offset
            val totalCharacters = response.total

            return LoadResult.Page(
                data = response.characters,
                prevKey = null,
                nextKey = if (responseOffset < totalCharacters) {
                    responseOffset + LIMIT
                } else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(LIMIT) ?: anchorPage?.nextKey?.minus(LIMIT)
        }
    }

    companion object {
        private const val LIMIT = 20
    }

}
