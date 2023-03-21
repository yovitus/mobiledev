package dk.itu.moapd.scootersharing.vime.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import dk.itu.moapd.scootersharing.vime.R

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
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