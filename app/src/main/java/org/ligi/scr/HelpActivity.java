package org.ligi.scr;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class HelpActivity extends ActionBarActivity {

    @InjectView(R.id.helpText)
    TextView helpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().setSubtitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);

        helpText.setText(Html.fromHtml(getString(R.string.help_html)));
        helpText.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
