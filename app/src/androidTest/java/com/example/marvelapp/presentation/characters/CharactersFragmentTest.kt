package com.example.marvelapp.presentation.characters

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marvelapp.R
import com.example.marvelapp.extension.IdlingResourceUtil.registerIdlingResources
import com.example.marvelapp.extension.IdlingResourceUtil.unregisterIdlingResources
import com.example.marvelapp.extension.asJsonString
import com.example.marvelapp.framework.di.BaseUrlModule
import com.example.marvelapp.framework.di.ConfigIdlingResource
import com.example.marvelapp.framework.di.CoroutinesModule
import com.example.marvelapp.launchFragmentInHiltContainer
import com.example.marvelapp.matcher.RecyclerViewMatcher
import com.example.marvelapp.presentation.characters.adapter.CharactersViewHolder
import com.example.marvelapp.util.idlingresource.singleton.FlipperCharactersIdlingResource
import com.example.marvelapp.util.idlingresource.singleton.RecyclerCharactersIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@UninstallModules(BaseUrlModule::class, CoroutinesModule::class, ConfigIdlingResource::class)
@HiltAndroidTest
class CharactersFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var server: MockWebServer
    private val portUrl = 8080

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )

    private val charactersResponsePage1Mock = "characters_p1.json".asJsonString()
    private val charactersResponsePage2Mock = "characters_p2.json".asJsonString()

    private val codeError = 404

    @Before
    fun setUp() {
        server = MockWebServer().apply {
            start(portUrl)
        }
        launchFragmentInHiltContainer<CharactersFragment>(
            navHostController = navController,
            navGraph = R.navigation.main_nav
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldShowLoading_whenViewIsCreated(): Unit = runBlocking {
        // Assert
        onView(
            withId(R.id.include_view_characters_loading_state)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldShowCharacters_whenReceiveApiSuccessResponse(): Unit = runBlocking {
        // Arrange
        registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
        server.enqueue(MockResponse().setBody(charactersResponsePage1Mock))
        // Assert
        onView(
            withId(R.id.recycler_characters)
        ).check(
            matches(isDisplayed())
        )
        unregisterIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
    }

    @Test
    fun shouldLoadMoreCharacters_whenNewPageIsRequested(): Unit = runBlocking {
        // Arrange
        registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
        server.run {
            enqueue(MockResponse().setBody(charactersResponsePage1Mock))
            enqueue(MockResponse().setBody(charactersResponsePage2Mock))
        }
        // Act
        onView(
            withId(R.id.recycler_characters)
        ).perform(
            RecyclerViewActions.scrollToPosition<CharactersViewHolder>(20)
        )
        onView(
            withId(R.id.recycler_characters)
        ).perform(
            RecyclerViewActions.scrollToPosition<CharactersViewHolder>(31)
        )
        // Assert
        onView(
            RecyclerViewMatcher(R.id.recycler_characters)
                .atPositionOnView(30)
        ).check(
            matches(isDisplayed())
        )
        unregisterIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
    }

    @Test
    fun shouldShowErrorView_whenReceivesAnErrorAsPaging(): Unit = runBlocking {
        // Arrange
        registerIdlingResources(FlipperCharactersIdlingResource.countingIdlingResource)
        server.enqueue(MockResponse().setResponseCode(codeError))
        // Assert
        onView(
            withId(R.id.text_initial_loading_error)
        ).check(
            matches(isDisplayed())
        )
        unregisterIdlingResources(FlipperCharactersIdlingResource.countingIdlingResource)
    }

    @Test
    fun shouldShowButtonGoToTop_whenFirstItemListIsVisible(): Unit =
        runBlocking {
            // Arrange
            registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
            server.run {
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
                enqueue(MockResponse().setBody(charactersResponsePage2Mock))
            }
            // Act
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(10)
            )
            // Assert
            onView(
                withId(R.id.button_go_to_top)
            ).check(
                matches(withEffectiveVisibility(Visibility.VISIBLE))
            )
            unregisterIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
        }

    @Test
    fun shouldHideButtonGoToTop_whenFirstItemListIsNotVisible(): Unit =
        runBlocking {
            // Arrange
            registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
            server.run {
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
                enqueue(MockResponse().setBody(charactersResponsePage2Mock))
            }
            // Act
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(10)
            )
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(0)
            )
            // Assert
            onView(
                withId(R.id.button_go_to_top)
            ).check(
                matches(withEffectiveVisibility(Visibility.GONE))
            )
            unregisterIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
        }

    @Test
    fun shouldScrollToTopList_whenClickInButtonGoToTop(): Unit =
        runBlocking {
            // Arrange
            registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
            server.run {
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
                enqueue(MockResponse().setBody(charactersResponsePage2Mock))
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
            }
            // Act
            val expectedFirstItemName = "3-D Man"
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(20)
            )
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(40)
            )
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.scrollToPosition<CharactersViewHolder>(60)
            )
            onView(
                withId(R.id.button_go_to_top)
            ).perform(
                click()
            )
            // Assert
            onView(
                withText(expectedFirstItemName)
            ).check(
                matches(isDisplayed())
            )
            unregisterIdlingResources(
                RecyclerCharactersIdlingResource.countingIdlingResource
            )
        }

    @Test
    fun shouldNavigationToDetailFragment_whenClickInCharacterFromCharacterFragment(): Unit =
        runBlocking {
            // Arrange
            registerIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
            server.enqueue(MockResponse().setBody(charactersResponsePage1Mock))
            // Act
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.actionOnItemAtPosition<CharactersViewHolder>(
                    0,
                    click()
                )
            )
            // Assert
            assertEquals(R.id.detailFragment, navController.currentDestination?.id)
            unregisterIdlingResources(RecyclerCharactersIdlingResource.countingIdlingResource)
        }

}
