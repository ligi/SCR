import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.junit.Rule
import org.junit.Test
import org.ligi.scr.HelpActivity
import org.ligi.scr.R
import org.ligi.trulesk.TruleskActivityRule

class TheHelpActivity {

    @get:Rule
    var rule = TruleskActivityRule(HelpActivity::class.java)

    @Test
    fun testHelpIsThere() {
        onView(withId(R.id.helpText)).check(matches(isDisplayed()))

        rule.screenShot("help")
    }
}
