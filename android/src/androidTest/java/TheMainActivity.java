import android.support.test.rule.ActivityTestRule;
import com.jraska.falcon.FalconSpoon;
import org.junit.Rule;
import org.junit.Test;
import org.ligi.scr.MainActivity;

public class TheMainActivity {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testMainActivityStarts() {

        FalconSpoon.screenshot(rule.getActivity(), "main");
    }
}
