package org.ligi.scr;

import android.app.Application;
import com.google.gson.Gson;
import java.io.IOException;
import net.danlew.android.joda.JodaTimeAndroid;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ligi.scr.model.Conference;
import org.ligi.scr.model.ScheduleContainer;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    public static Conference conference;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

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
