package com.example.marvelapp.presentation.characters

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marvelapp.R
import com.example.marvelapp.extension.asJsonString
import com.example.marvelapp.framework.di.BaseUrlModule
import com.example.marvelapp.launchFragmentInHiltContainer
import com.example.marvelapp.presentation.characters.adapter.CharactersViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(BaseUrlModule::class)
@HiltAndroidTest
class CharactersFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var server: MockWebServer
    private val portUrl = 8080

    private val charactersResponsePage1Mock = "characters_p1.json".asJsonString()
    private val charactersResponsePage2Mock = "characters_p2.json".asJsonString()

    private val codeError = 404

    @Before
    fun setUp() {
        server = MockWebServer().apply {
            start(portUrl)
        }
        launchFragmentInHiltContainer<CharactersFragment>()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldShowCharacters_whenViewIsCreated() {
        server.enqueue(MockResponse().setBody(charactersResponsePage1Mock))

        onView(
            withId(R.id.recycler_characters)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldLoadMoreCharacters_whenNewPageIsRequested() {
        // Arrange
        with(server) {
            enqueue(MockResponse().setBody(charactersResponsePage1Mock))
            enqueue(MockResponse().setBody(charactersResponsePage2Mock))
        }
        val expectedFirstResultName = "Amora"

        // Action
        onView(
            withId(R.id.recycler_characters)
        ).perform(
            RecyclerViewActions.scrollToPosition<CharactersViewHolder>(20)
        )

        // Assert
        onView(
            withText(expectedFirstResultName)
        ).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun shouldShowErrorView_whenReceivesAnErrorAsPaging(){
        // Arrange
        server.enqueue(MockResponse().setResponseCode(codeError))

        onView(
            withId(R.id.text_initial_loading_error)
        ).check(
            matches(isDisplayed())
        )
    }

}