package org.ligi.scr;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.ligi.axt.AXT;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.metadude.java.library.halfnarp.ApiModule;
import info.metadude.java.library.halfnarp.TalkPreferencesService;
import info.metadude.java.library.halfnarp.model.CreateTalkPreferencesSuccessResponse;
import info.metadude.java.library.halfnarp.model.GetTalksResponse;
import info.metadude.java.library.halfnarp.model.UpdateTalkPreferencesSuccessResponse;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.trackRecycler)
    RecyclerView trackRecycler;

    private EventViewHolderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setSubtitle("Schedule Conflict Resolver");
        ButterKnife.inject(this);


        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.rows), OrientationHelper.VERTICAL);
        trackRecycler.setLayoutManager(layoutManager);

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                trackRecycler.setBackgroundResource(R.drawable.bg_src);
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.bg_src);
                trackRecycler.setBackgroundDrawable(new BitmapDrawable(getResources(), Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // form follows function
        }

        App.bus.register(this);
        App.talkIds.load();
    }

    @Override
    protected void onPause() {
        App.bus.unregister(this);
        App.talkIds.save();
        super.onPause();
    }


    private void loadData() {
        final TalkPreferencesService service = ApiModule.getTalkPreferencesService();
        service.getTalks(new DefaultRetrofitCallback<List<GetTalksResponse>>(true, this) {
            @Override
            public void success(List<GetTalksResponse> getTalksResponses, Response response) {
                adapter = new EventViewHolderAdapter(getTalksResponses);
                trackRecycler.setAdapter(adapter);
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
        menu.findItem(R.id.action_upload).setVisible(App.talkIds.size() > 0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AXT.at(this).startCommonIntent().activityFromClass(HelpActivity.class);
                break;

            case R.id.action_upload:
                final String uuidOrNull = getPrefs().getString("uuid", null);
                if (uuidOrNull == null) {
                    ApiModule.getTalkPreferencesService().createTalkPreferences(App.talkIds, new DefaultRetrofitCallback<CreateTalkPreferencesSuccessResponse>(false, this) {
                        @Override
                        public void success(CreateTalkPreferencesSuccessResponse postSuccessResponse, Response response) {
                            getPrefs().edit().putString("uuid", postSuccessResponse.getUid()).commit();
                            Toast.makeText(MainActivity.this, "Yay initial upload done!-)", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    ApiModule.getTalkPreferencesService().updateTalkPreferences(uuidOrNull, App.talkIds, new DefaultRetrofitCallback<UpdateTalkPreferencesSuccessResponse>(false, this) {
                        @Override
                        public void success(UpdateTalkPreferencesSuccessResponse updateTalkPreferencesSuccessResponse, Response response) {
                            Toast.makeText(MainActivity.this, "Yay list update done!-)", Toast.LENGTH_LONG).show();
                        }

                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences getPrefs() {
        return getSharedPreferences("prefs", MODE_PRIVATE);
    }

    @Subscribe
    public void onEvent(TalkIdsChangeEvent scopeChangeEvent) {
        invalidateOptionsMenu();
    }

}