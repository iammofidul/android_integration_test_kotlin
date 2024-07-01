package com.flingo.helloworld

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FirstFragmentTestHilt {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavigationToSecondFragment() {
        // Simulate clicking the button to navigate to SecondFragment
        onView(withId(R.id.button_first)).perform(click())

        // Verify that the SecondFragment is displayed
        onView(withId(R.id.button_second)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewDisplaysPosts() {
        // Assuming that the ViewModel is mocked and returns some test data

        // Wait for the posts to be loaded and the RecyclerView to be populated
        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText("Test Post 1"))))
        onView(withId(R.id.recycler_view))
            .check(matches(hasDescendant(withText("Test Post 2"))))
    }
}