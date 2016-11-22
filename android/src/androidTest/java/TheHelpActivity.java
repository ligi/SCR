import android.support.test.rule.ActivityTestRule;
import com.jraska.falcon.FalconSpoon;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.scr.HelpActivity;
import org.ligi.scr.R;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class TheHelpActivity {

    @Rule
    public ActivityTestRule<HelpActivity> rule = new ActivityTestRule<>(HelpActivity.class);

    @Test
    public void testHelpIsThere() {
        onView(withId(R.id.helpText)).check(matches(isDisplayed()));

        FalconSpoon.screenshot(rule.getActivity(),"help");
    }
}
