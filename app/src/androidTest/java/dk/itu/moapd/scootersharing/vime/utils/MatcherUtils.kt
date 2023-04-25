package dk.itu.moapd.scootersharing.vime.utils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher


/**
 * Wait for the specified view to appear, then returns the view.
 * @param delayMillis Millisecond delay between each check if it has appeared.
 * @param maxTries Amount of tries checking if it has appeared. Will throw exception
 * when tries exceed maxTries.
 * @return this for further operations on the view.
 * @throws NoMatchingViewException If no view is found within maxTries of delayMillis.
 */
fun Matcher<View>.awaitView(delayMillis: Long=500, maxTries: Int=5): Matcher<View> {
    for (tries in 2..maxTries) {
        Thread.sleep(delayMillis)
        try {
            onView(this).check(ViewAssertions.matches(isDisplayed()))
            return this
        } catch (e: NoMatchingViewException) {
            continue
        }
    }

    onView(this).check(ViewAssertions.matches(isClickable()))
    return this
}