package com.example.marvelapp.presentation.detail

import androidx.core.os.bundleOf
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marvelapp.R
import com.example.marvelapp.extension.asJsonString
import com.example.marvelapp.framework.di.BaseUrlModule
import com.example.marvelapp.framework.di.CoroutinesModule
import com.example.marvelapp.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(BaseUrlModule::class, CoroutinesModule::class)
@HiltAndroidTest
class DetailFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var server: MockWebServer
    private val portUrl = 8080

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )

    private val detailViewArgMock = DetailViewArg(
        characterId = 1011334,
        name = "3-D Man",
        imageUrl = "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg",
        description = ""
    )
    private val screenNameMock = "Hero Name"

    private val detailFragmentArgsMock = bundleOf(
        "detailViewArg" to detailViewArgMock,
        "screenTitle" to screenNameMock
    )
    private val comicResponseMock = "comics_by_character_id.json".asJsonString()
    private val eventResponseMock = "events_by_character_id.json".asJsonString()

    private val comicEmptyResponseMock = "comics_empty_response.json".asJsonString()
    private val eventEmptyResponseMock = "events_empty_response.json".asJsonString()

    private val codeError = 404

    private val timeResponseUI = 100L

    @Before
    fun setUp() {
        server = MockWebServer().apply {
            start(portUrl)
        }
        launchFragmentInHiltContainer<DetailFragment>(
            fragmentArgs = detailFragmentArgsMock,
            navHostController = navController,
            navGraph = R.navigation.characters
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldShowDefaultDescriptionText_whenFragmentReceiveEmptyArg(): Unit = runBlocking {
        onView(
            withId(R.id.text_description_character)
        ).check(
            matches(withText(R.string.empty_description))
        )
    }

    @Test
    fun shouldShowLoadingInListComicsAndEvents_whenViewIsCreated(): Unit = runBlocking {
        onView(
            withId(R.id.include_detail_loading_view)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldShowComicsAndEvents_whenReceiveSuccessResponseFromApi(): Unit = runBlocking {
        // Arrange
        server.run {
            enqueue(MockResponse().setBody(comicResponseMock))
            enqueue(MockResponse().setBody(eventResponseMock))
        }

        // Act
        delay(timeResponseUI)

        // Assert
        onView(
            withId(R.id.recycler_parent_detail)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldShowComicsList_whenReceiveJustComicsFromApi(): Unit = runBlocking {
        // Arrange
        server.run {
            enqueue(MockResponse().setBody(comicResponseMock))
            enqueue(MockResponse().setBody(eventEmptyResponseMock))
        }

        // Act
        delay(timeResponseUI)

        // Assert
        onView(
            withId(R.id.recycler_parent_detail)
        ).check(
            matches(hasChildCount(1))
        )
    }

    @Test
    fun shouldShowEventsList_whenReceiveJustEventsFromApi(): Unit = runBlocking {
        // Arrange
        server.run {
            enqueue(MockResponse().setBody(comicEmptyResponseMock))
            enqueue(MockResponse().setBody(eventResponseMock))
        }

        // Act
        delay(timeResponseUI)

        // Assert
        onView(
            withId(R.id.recycler_parent_detail)
        ).check(
            matches(hasChildCount(1))
        )
    }

    @Test
    fun shouldShowDefaultTextNoResults_whenReceivesEmptyResponseFromApi(): Unit = runBlocking {
        // Arrange
        server.run {
            enqueue(MockResponse().setBody(comicEmptyResponseMock))
            enqueue(MockResponse().setBody(eventEmptyResponseMock))
        }

        // Act
        delay(timeResponseUI)

        // Assert
        onView(
            withId(R.id.text_no_results)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldShowErrorView_whenReceivesAnErrorFromApi(): Unit = runBlocking {
        // Arrange
        server.enqueue(MockResponse().setResponseCode(codeError))

        // Act
        delay(timeResponseUI)

        // Assert
        onView(
            withId(R.id.include_error_view)
        ).check(
            matches(isDisplayed())
        )
    }

}