package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.Character
import com.example.marvelapp.factory.response.CharacterPagingFactory
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var remoteDataSource: CharactersRemoteDataSource

    private val dataWrapperResponseFactory = CharacterPagingFactory()

    private val characterFactory = CharacterFactory()
    private val charactersMock = listOf(
        characterFactory.create(CharacterFactory.Hero.ThreeDMan),
        characterFactory.create(CharacterFactory.Hero.ABomb)
    )

    private lateinit var charactersPagingSource: CharactersPagingSource

    @Before
    fun setUp() {
        charactersPagingSource = CharactersPagingSource(remoteDataSource, "")
    }

    @Test
    fun `should return a success load result when load is called`() = runTest {
        // arrange
        whenever(
            remoteDataSource.fetchCharacters(any())
        ).thenReturn(
            dataWrapperResponseFactory.create()
        )

        // act
        val expectedResult = PagingSource.LoadResult.Page(
            data = charactersMock,
            prevKey = null,
            nextKey = 20
        )
        val result = charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        //assert
        Assert.assertEquals(
            expectedResult,
            result
        )

    }

    @Test
    fun `should return a error load result when load is called`() = runTest {
        //arrange
        val exception = RuntimeException()
        whenever(
            remoteDataSource.fetchCharacters(any())
        ).thenThrow(exception)

        // Act
        val expectedResult = PagingSource.LoadResult.Error<Int,Character>(exception)
        val result = charactersPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        // Assert
        Assert.assertEquals(expectedResult,result)
    }

}
