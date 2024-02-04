package com.example.marvelapp.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marvelapp.R
import com.example.marvelapp.extension.asJsonString
import com.example.marvelapp.framework.di.BaseUrlModule
import com.example.marvelapp.framework.di.CoroutinesModule
import com.example.marvelapp.presentation.characters.adapter.CharactersViewHolder
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
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@UninstallModules(BaseUrlModule::class, CoroutinesModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var rule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this))
        .around(ActivityScenarioRule(MainActivity::class.java))

    private lateinit var server: MockWebServer
    private val portUrl = 8080

    private val charactersResponsePage1Mock = "characters_p1.json".asJsonString()
    private val charactersResponsePage2Mock = "characters_p2.json".asJsonString()

    private val timeResponseUI = 400L

    @Before
    fun setUp() {
        server = MockWebServer().apply {
            start(portUrl)
        }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldNavigationToFavoritesFragments_whenClickInFavoritesFromBottomNavigation(): Unit =
        runBlocking {
            // Arrange
            server.run {
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
                enqueue(MockResponse().setBody(charactersResponsePage2Mock))
            }

            // Act
            onView(
                withId(R.id.favorite)
            ).perform(
                click()
            )

            // Assert
            onView(
                withId(R.id.flipper_favorites)
            ).check(
                matches(isDisplayed())
            )
        }

    @Test
    fun shouldNavigationBetweenCharactersAndFavoriteFragments_whenClickBottomNavigation(): Unit =
        runBlocking {
            // Arrange
            server.run {
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
                enqueue(MockResponse().setBody(charactersResponsePage2Mock))
                enqueue(MockResponse().setBody(charactersResponsePage1Mock))
            }

            // Act
            onView(
                withId(R.id.favorite)
            ).perform(
                click()
            )
            onView(
                withId(R.id.home)
            ).perform(
                click()
            )
            delay(timeResponseUI)

            // Assert
            onView(
                withId(R.id.recycler_characters)
            ).check(
                matches(isDisplayed())
            )
        }

    @Test
    fun shouldHideBottomNavigation_whenFragmentIsNotTopLevelDestination(): Unit =
        runBlocking {
            // Arrange
            server.enqueue(MockResponse().setBody(charactersResponsePage1Mock))

            // Act
            delay(timeResponseUI)
            onView(
                withId(R.id.recycler_characters)
            ).perform(
                RecyclerViewActions.actionOnItemAtPosition<CharactersViewHolder>(
                    0,
                    click()
                )
            )

            // Assert
            onView(
                withId(R.id.bottom_nav_main)
            ).check(
                matches(withEffectiveVisibility(Visibility.GONE))
            )
        }

    @Test
    fun shouldShowCharactersFragmentNameScreenInToolbar_whenCharactersFragmentIsAttached(): Unit =
        runBlocking {
            onView(
                withId(R.id.toolbar_app)
            ).check(
                matches(hasDescendant(withText(R.string.characters_screen_title)))
            )
        }

    @Test
    fun shouldShowFavoritesFragmentNameScreenInToolbar_whenFragmentFavoritesIsAttached(): Unit =
        runBlocking {
            // Arrange
            onView(
                withId(R.id.favorite)
            ).perform(
                click()
            )

            // Act
            delay(timeResponseUI)

            // Assert
            onView(
                withId(R.id.toolbar_app)
            ).check(
                matches(hasDescendant(withText(R.string.favorites_screen_title)))
            )
        }

//    @Test
//    fun shouldShowNameHeroInToolbar_whenDetailFragmentIsAttached(): Unit =
//        runBlocking {
//            // Arrange
//            server.enqueue(MockResponse().setBody(charactersResponsePage1Mock))
//            val expectedNameHero = "3-D Man"
//
//            // Act
//            delay(timeResponseUI)
//            onView(
//                withId(R.id.recycler_characters)
//            ).perform(
//                RecyclerViewActions.actionOnItemAtPosition<CharactersViewHolder>(
//                    0,
//                    click()
//                )
//            )
//            delay(timeResponseUI)
//
//            onView(
//                withId(R.id.toolbar_app)
//            ).check(
//                matches(hasDescendant(withText(expectedNameHero)))
//            )
//        }

}