package org.ligi.scr;

import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Set;

import info.metadude.java.library.halfnarp.model.TalkIds;

public class App {

    public static Integer selectedEventId;
    public static Bus bus = new Bus();
    public static TalkIds talkIds = new TalkIds();
}
