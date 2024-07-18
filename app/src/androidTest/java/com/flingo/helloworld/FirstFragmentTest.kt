package com.flingo.helloworld

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class FirstFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    @Before
    fun setUp() {
        // Additional setup if required
    }

    @After
    fun tearDown() {
        // Cleanup after tests if required
    }

    @Test
    fun navigateSecondFragment() {
        onView(withId(R.id.button_first)).perform(click())
        onView(withId(R.id.button_second)).check(matches(withText("Previous")))
        onView(withId(R.id.fab)).perform(click())
    }

    @Test
    fun rvTest() {
        launchFragmentInHiltContainer<FirstFragment>(null, R.style.Theme_HelloWorld)

        // Register an IdlingResource to wait for the RecyclerView to be displayed and populated
        val recyclerViewIdlingResource = RecyclerViewIdlingResource(R.id.recycler_view)
        IdlingRegistry.getInstance().register(recyclerViewIdlingResource)

        // Wait for the button_first to be displayed
        //waitForView(withId(R.id.button_first), 5000)

        // Perform action on the first item in RecyclerView
        /*onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ContactRv.ViewHolder>(
                    0,
                    clickOnButtonInRecyclerView(R.id.contact_name)
                )
            )*/

        // Unregister the IdlingResource
        IdlingRegistry.getInstance().unregister(recyclerViewIdlingResource)

        // Wait for the button_second to be displayed and check its text
        //waitForView(withId(R.id.button_second), 5000)
        onView(withId(R.id.button_second)).check(matches(withText("Previous")))
    }

    private fun waitForView(viewMatcher: Matcher<View>, timeout: Long) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        do {
            try {
                onView(viewMatcher).check(matches(isDisplayed()))
                Log.d("WaitForView", "View is displayed")
                return
            } catch (e: Exception) {
                if (System.currentTimeMillis() >= endTime) {
                    Log.e("WaitForView", "View is not displayed within the given timeout")
                    throw TimeoutException("View is not displayed within the given timeout")
                }
                Thread.sleep(50)  // Sleep briefly to avoid hammering the CPU
            }
        } while (true)
    }

    private fun clickOnButtonInRecyclerView(buttonId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on specific button in RecyclerView item"
            }

            override fun perform(uiController: UiController, view: View) {
                val button = view.findViewById<View>(buttonId)
                button?.performClick()
            }
        }
    }

    class RecyclerViewIdlingResource(private val viewId: Int) : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String {
            return this::class.java.name + ":" + viewId
        }

        override fun isIdleNow(): Boolean {
            val view = getCurrentActivity()?.findViewById<View>(viewId)
            val isIdle = view != null && view.isShown && view.height > 0
            if (isIdle) {
                callback?.onTransitionToIdle()
            }
            return isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }

        private fun getCurrentActivity(): Activity? {
            val activity = arrayOfNulls<Activity>(1)
            onView(isRoot()).check { view, _ ->
                activity[0] = view.context as Activity
            }
            return activity[0]
        }
    }
}
