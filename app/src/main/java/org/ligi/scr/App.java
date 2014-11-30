package org.ligi.scr;

import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Set;

public class App {

    public static Integer selectedEventId;
    public static Bus bus = new Bus();
    public static Set<Integer> viewSet = new HashSet<>();
}
