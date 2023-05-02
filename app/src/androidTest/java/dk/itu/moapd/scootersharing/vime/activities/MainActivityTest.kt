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
import kotlin.random.Random

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
    fun saveButtonSavesCardNumber() {
        // Checking if new mock card number is saved
        val lowestCardNumber = 1000000000000000L
        val highestCardNumber = 9999999999999999L

        val mockCardNumber = Random.nextLong(lowestCardNumber, highestCardNumber).toString()

        val mockMonthYear = "12/24"
        val mockCvv = "000"

        onView(withId(R.id.profile))
            .perform(click())

        onView(withId(R.id.edit_card_button))
            .perform(click())

        onView(withId(R.id.save_button))
            .check(matches(isDisplayed()))

        onView(withId(R.id.edit_text_cardnumber))
            .perform(typeText(mockCardNumber))

        onView(withId(R.id.edit_text_expiration))
            .perform(typeText(mockMonthYear))

        onView(withId(R.id.edit_text_cvv))
            .perform(typeText(mockCvv))

        onView(withId(R.id.save_button))
            .perform(click())

        onView(withId(R.id.edit_card_button).awaitView())
            .perform(click())

        onView(withId(R.id.edit_text_cardnumber))
            .check(matches(withText(mockCardNumber)))
    }

    @Test
    fun signOutButtonSignsOut() {
        onView(withId(R.id.profile))
            .perform(click())

        onView(withId(R.id.sign_out_button))
            .perform(click())

        onView(withText("Sign in with email").awaitView())
            .check(matches(isDisplayed()))
    }
}