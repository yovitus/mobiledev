package dk.itu.moapd.scootersharing.vime.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.utils.awaitView
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
         * setUpAll() is for logging in to a test user. @BeforeClass @JvmStatic ensures that this function is called once
         * before all tests.
         */
        @BeforeClass @JvmStatic
        fun setUpAll() {
            val loginScenario = launch(LoginActivity::class.java)

            onView(withText("Sign in with email").awaitView()).perform(click())

            onView(withHint("Email").awaitView()).perform(typeText("john@doe.com"))
            onView(withText("NEXT")).perform(click())

            onView(withHint("Password").awaitView()).perform(typeText("123456"))
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