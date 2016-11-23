import com.jraska.falcon.FalconSpoon
import org.junit.Rule
import org.junit.Test
import org.ligi.scr.MainActivity
import org.ligi.trulesk.TruleskActivityRule


class TheMainActivity {

    @get:Rule
    var rule = TruleskActivityRule(MainActivity::class.java)

    @Test
    fun testMainActivityStarts() {

        FalconSpoon.screenshot(rule.activity, "main")
    }
}
