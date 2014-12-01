package org.ligi.scr;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;
import org.ligi.axt.listeners.ActivityFinishingOnClickListener;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
    private EventViewHolderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setSubtitle("Schedule Conflict Resolver");
        ButterKnife.inject(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        trackRecycler.setLayoutManager(mLayoutManager);

        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.bus.register(this);

        if (getDataFile().exists()) {
            try {
                HashSet<Integer> hashSet = new Gson().fromJson(AXT.at(getDataFile()).readToString(), new TypeToken<HashSet<Integer>>() {}.getType());
                App.talkIds.add(hashSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {

        App.bus.unregister(this);

        final Gson gson = new Gson();

        final File dataFile = getDataFile();

        try {
            if (dataFile.exists()) {
                dataFile.delete();
            }

            dataFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }


        AXT.at(dataFile).writeString(gson.toJson(App.talkIds));
        super.onPause();
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data");
    }

    private void loadData() {
        TalkPreferencesService service = ApiModule.getTalkPreferencesService();
        service.getTalkPreferencesResponse(new Callback<List<TalkPreferencesResponse>>() {
            @Override
            public void success(
                    final List<TalkPreferencesResponse> talkPreferencesResponses,
                    Response response) {

                adapter = new EventViewHolderAdapter(talkPreferencesResponses);
                trackRecycler.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(error.toString())
                        .setTitle("Error")
                        .setPositiveButton(android.R.string.ok, new ActivityFinishingOnClickListener(MainActivity.this))
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_help).setVisible(App.selectedEventId == null);

        menu.findItem(R.id.action_add).setVisible(App.selectedEventId != null && !App.talkIds.getTalkIds().contains(App.selectedEventId));
        menu.findItem(R.id.action_remove).setVisible(App.selectedEventId != null && App.talkIds.getTalkIds().contains(App.selectedEventId));
        menu.findItem(R.id.action_share).setVisible(App.selectedEventId != null);


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AXT.at(this).startCommonIntent().activityFromClass(HelpActivity.class);
                break;

            case R.id.action_add:
                App.talkIds.add(App.selectedEventId);
                App.selectedEventId = null;
                App.bus.post(new CurrentScopeChangeEvent());
                break;

            case R.id.action_remove:
                App.talkIds.getTalkIds().remove(App.selectedEventId);
                App.selectedEventId = null;
                App.bus.post(new CurrentScopeChangeEvent());
                break;

            case R.id.action_share:
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, adapter.findById(App.selectedEventId).getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, adapter.findById(App.selectedEventId).getAbstract());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(CurrentScopeChangeEvent scopeChangeEvent) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        }
    }

}