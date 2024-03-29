package dk.itu.moapd.scootersharing.vime.utils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher


/**
 * Wait for the specified view to appear and for the view assertion to be true,
 * then returns the view.
 * @param delayMillis Millisecond delay between each check if it has appeared. Default is 500.
 * @param maxTries Amount of tries checking if it has appeared. Will throw exception
 * when tries exceed maxTries. Default is 5.
 * @param assertion The View assertion that should be true when the Matcher<View> is returned
 * Default is 'matches(isDisplayed())'.
 * @return this for further operations on the view.
 * @throws NoMatchingViewException If no view is found within maxTries of delayMillis.
 */
fun Matcher<View>.awaitView(
    assertion: ViewAssertion = ViewAssertions.matches(isDisplayed()),
    delayMillis: Long = 500,
    maxTries: Int = 5
): Matcher<View> {
    for (tries in 1..maxTries) {
        try {
            onView(this).check(assertion)
            return this
        } catch (e: NoMatchingViewException) {
            if (tries == maxTries)
                throw e
            Thread.sleep(delayMillis)
        }
    }
    return this
}

