package org.ligi.scr;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.metadude.java.library.halfnarp.ApiModule;
import info.metadude.java.library.halfnarp.TalkPreferencesService;
import info.metadude.java.library.halfnarp.model.TalkPreferencesResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.trackRecycler)
    RecyclerView trackRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setSubtitle("Schedule Conflict Resolver");
        ButterKnife.inject(this);
        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(this);

        trackRecycler.setLayoutManager(mLayoutManager);

        loadData();
    }

    private void loadData() {
        TalkPreferencesService service = ApiModule.getTalkPreferencesService();
        service.getTalkPreferencesResponse(new Callback<List<TalkPreferencesResponse>>() {
            @Override
            public void success(
                    final List<TalkPreferencesResponse> talkPreferencesResponses,
                    Response response) {


                trackRecycler.setAdapter(new EventViewHolderAdapter(talkPreferencesResponses));
            }

            @Override
            public void failure(RetrofitError error) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(error.toString())
                        .setTitle("Error")
                        .setPositiveButton(android.R.string.ok,new ActivityFinishingOnClickListener(MainActivity.this))
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                break;

            case R.id.action_help:
                AXT.at(this).startCommonIntent().activityFromClass(HelpActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}