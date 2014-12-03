package org.ligi.scr;

import android.app.Application;

import com.squareup.otto.Bus;

public class App extends Application {

    public static final Bus bus = new Bus();
    public static PersistentTalkIds talkIds;

    @Override
    public void onCreate() {
        talkIds = new PersistentTalkIds(this);
        super.onCreate();
    }
}
