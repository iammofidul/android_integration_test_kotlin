package com.flingo.helloworld

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
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class FirstFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private var idlingResource: ViewVisibilityIdlingResource? = null

    @Before
    fun setUp() {
        // Initialize the IdlingResource with the view and desired visibility state
        activityRule.scenario.onActivity { activity ->
            val buttonSecond = activity.findViewById<View>(R.id.button_second)
            if (buttonSecond != null) {
                idlingResource = ViewVisibilityIdlingResource(
                    buttonSecond,
                    View.VISIBLE  // Pass the desired visibility state here
                )
                // Register the IdlingResource
                IdlingRegistry.getInstance().register(idlingResource)
            }
        }
    }

    @After
    fun tearDown() {
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun navigateSecondFragment() {
        onView(withId(R.id.button_first)).perform(click())
        onView(withId(R.id.button_second)).check(matches(withText("Previous")))
        onView(withId(R.id.fab)).perform(click())
    }

    @Test
    fun rvTest() {

        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ContactRv.ViewHolder>(
                    0,
                    clickOnButtonInRecyclerView(0, R.id.contact_name)
                )
            )

        waitForView(withId(R.id.button_second), 5000)

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

    class ViewVisibilityIdlingResource(
        private val view: View,
        private val visibility: Int = View.VISIBLE
    ) : IdlingResource {

        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String {
            return this::class.java.name
        }

        override fun isIdleNow(): Boolean {
            val isIdle = view.visibility == visibility
            if (isIdle) {
                callback?.onTransitionToIdle()
            }
            return isIdle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }
    private fun clickOnButtonInRecyclerView(itemPosition: Int, buttonId: Int): ViewAction {
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
}
