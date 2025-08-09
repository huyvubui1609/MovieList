package com.example.movielist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.movielist.data.repository.MovieRepositoryImpl
import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.domain.repository.MovieRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(com.example.movielist.di.AppModule::class)
class MovieListUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: MovieRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun movieList_loads_and_navigates_to_detail() {
        // Wait for trending movies to load
        composeTestRule.onNodeWithText("Trending movies").assertIsDisplayed()

        // Check if any movie item is displayed (using generic content)
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule.onNodeWithText("★").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Click on the first movie item that appears
        composeTestRule.onNodeWithText("★").performClick()

        // Verify navigation to detail screen
        composeTestRule.onNodeWithText("Movie Details").assertIsDisplayed()

        // Verify back navigation works
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText("Movies").assertIsDisplayed()
    }
}
