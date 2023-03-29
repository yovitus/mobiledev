package dk.itu.moapd.scootersharing.vime.activities

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.itu.moapd.scootersharing.vime.R
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    // companion object is for @BeforeClass annotations
    companion object {

        /**
         * Helper function for setUpAll(). Waits until a specified view is visible.
         * @param view The view that the function waits for.
         * @param delayMillis Millisecond delay between each check if it has become visible.
         * @param maxTries Amount of tries checking if it has become visible, will throw exception
         * when tries exceed maxTries.
         */
        private fun waitForView(view: Matcher<View>, delayMillis: Long=500, maxTries: Int=5) {
            var viewNotFound = true
            var tries = 0
            while (viewNotFound) {
                Thread.sleep(delayMillis)

                try {
                    onView(view)
                    viewNotFound = false
                } catch (e: NoMatchingViewException) {
                    tries++
                    if (tries > maxTries) {
                        throw e
                    }
                }
            }
        }

        /**
         * setUpAll() is for logging in to a test user.
         * @BeforeClass @JvmStatic ensures that this function is called once
         * before all
         */
        @BeforeClass @JvmStatic
        fun setUpAll() {
            val loginScenario = launch(LoginActivity::class.java)

            waitForView(withText("Sign in with email"))
            onView(withText("Sign in with email")).perform(click())

            waitForView(withHint("Email"))
            onView(withHint("Email")).perform(typeText("john@doe.com"))
            onView(withText("NEXT")).perform(click())

            waitForView(withHint("Password"))
            onView(withHint("Password")).perform(typeText("123456"))
            onView(withText("SIGN IN")).perform(click())

            loginScenario.close()
        }
    }

    private lateinit var mainScenario: ActivityScenario<MainActivity>

    @Before
    fun setUpEach() {
        mainScenario = launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        mainScenario.close()
    }

    @Test
    fun showsRideListOnRideListButtonClick() {
        onView(withId(R.id.show_ridelist_button))
            .perform(click())

        onView(withId(R.id.recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun startRideAddsRideToRecyclerView() {
        onView(withId(R.id.recycler_view))
            .check(matches(hasChildCount(3)))

        onView(withId(R.id.start_ride_button))
            .perform(click())

        onView(withId(R.id.edit_text_name))
            .perform(typeText("testScooter"))

        onView(withId(R.id.edit_text_location))
            .perform(typeText("testLocation"))

        onView(withId(R.id.start_ride_button))
            .perform(click())

        onView(withText("OK"))
            .perform(click())

        onView(withId(R.id.recycler_view))
            .check(matches(hasChildCount(4)))
    }
}