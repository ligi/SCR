package org.ligi.scr;

import android.app.Application;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;
import java.io.IOException;
import org.ligi.scr.model.Conference;
import org.ligi.scr.model.ScheduleContainer;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    public static final Bus bus = new Bus();
    public static PersistentTalkIds talkIds;
    public static Conference conference;

    @Override
    public void onCreate() {
        talkIds = new PersistentTalkIds(this);
        super.onCreate();


        new Thread(new Runnable() {

            @Override
            public void run() {

                final OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://events.ccc.de/congress/2015/Fahrplan/schedule.json")
                        .build();

                try {
                    final Response response = client.newCall(request).execute();
                    final Gson gson=new Gson();
                    final ScheduleContainer schedule = gson.fromJson(response.body().string(), ScheduleContainer.class);
                    conference= schedule.getSchedule().conference;
                    Log.i(schedule.getSchedule().version);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
