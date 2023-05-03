package dk.itu.moapd.scootersharing.vime.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
         * setUpAll() is for logging in to a test user. @BeforeClass @JvmStatic ensures that this
         * function is called once before all tests.
         */
        @BeforeClass @JvmStatic
        fun setUpAll() {
            launch(LoginActivity::class.java)

            onView(withText("Sign in with email").awaitView()).perform(click())

            onView(withHint("Email").awaitView()).perform(typeText("john@doe.com"))
            onView(withText("NEXT")).perform(click())

            onView(withHint("Password").awaitView()).perform(typeText("123456"))
            onView(withText("SIGN IN")).perform(click())
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
        /*
         As per documentation:
         Generates an Int random value uniformly distributed between the specified from (inclusive)
         and until (exclusive) bounds.
         From is included, until is excluded.
         */
        val fromCardNumber = 1000000000000000L
        val untilCardNumber = 10000000000000000L
        val mockCardNumber = Random.nextLong(fromCardNumber, untilCardNumber).toString()

        val fromMonth = 1
        val toMonth = 13
        val mockMonth = Random.nextInt(fromMonth, toMonth).toString()

        val untilYear = 100
        val mockYear = Random.nextInt(untilYear).toString()

        val mockMonthAndYear = "$mockMonth/$mockYear"

        val fromCvv = 100
        val untilCvv = 1000
        val mockCvv = Random.nextInt(fromCvv, untilCvv).toString()

        onView(withId(R.id.profile))
            .perform(click())

        onView(withId(R.id.edit_card_button))
            .perform(click())

        onView(withId(R.id.save_button).awaitView())
            .check(matches(isDisplayed()))

        onView(withId(R.id.edit_text_cardnumber))
            .perform(clearText(), typeText(mockCardNumber), closeSoftKeyboard())

        onView(withId(R.id.edit_text_expiration))
            .perform(clearText(), typeText(mockMonthAndYear), closeSoftKeyboard())

        onView(withId(R.id.edit_text_cvv))
            .perform(clearText(), typeText(mockCvv), closeSoftKeyboard())

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