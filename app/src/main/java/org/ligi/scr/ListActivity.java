package org.ligi.scr;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.common.collect.Iterables;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.ligi.scr.model.Day;
import org.ligi.scr.model.Event;
import org.ligi.scr.model.decorated.EventDecorator;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListActivity extends ActionBarActivity {

    @InjectView(R.id.trackRecycler1)
    RecyclerView trackRecycler1;


    @InjectView(R.id.trackRecycler2)
    RecyclerView trackRecycler2;

    private EventViewHolderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(new DaySelector(this));

        getSupportActionBar().setSubtitle("Schedule Conflict Resolver");
        ButterKnife.inject(this);


        final GridLayoutManager layoutManager1 = new GridLayoutManager(this, 1);
        trackRecycler1.setLayoutManager(layoutManager1);
        final GridLayoutManager layoutManager2 = new GridLayoutManager(this, 1);
        trackRecycler2.setLayoutManager(layoutManager2);

        DateTime earliest_start = DateTime.parse(App.conference.days.get(0).date);

        for (Day day : App.conference.days) {
            if (DateTime.parse(day.date).isBefore(earliest_start)) {
                earliest_start = DateTime.parse(day.date);
            }
        }

        HashMap<String, ArrayList<Event>> roomToAllEvents = new HashMap<>();



        for (String room : App.conference.days.get(0).rooms.keySet()) {
            DateTime act_time = earliest_start;
            final ArrayList<Event> newEventList = new ArrayList<>();
            for (Day day : App.conference.days) {

                for (Event event : day.rooms.get(room)) {
                    final EventDecorator eventDecorator = new EventDecorator(event);

                    if (act_time.isBefore(eventDecorator.getStart())) {
                        final Event object = new Event();
                        object.title = "break";

                        object.date = act_time.toString(ISODateTimeFormat.dateTime());

                        final EventDecorator eventDecorator1 = new EventDecorator(object);

                        eventDecorator1.setEnd(eventDecorator.getStart());
                        newEventList.add(object);
                    }
                    act_time = eventDecorator.getEnd();
                    newEventList.add(event);
                }
            }
            roomToAllEvents.put(room, newEventList);
        }

        trackRecycler1.setAdapter((new EventAdapter(Iterables.get(roomToAllEvents.values(), 0))));
        trackRecycler2.setAdapter((new EventAdapter(Iterables.get(roomToAllEvents.values(), 1))));

        linkRecyclers(trackRecycler1, trackRecycler2);
    }

    private void linkRecyclers(final RecyclerView... recyclerViews) {

        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getTag(R.id.tag_scroll_sync) != null) {
                        return;
                    }

                    for (RecyclerView recyclerView1 : recyclerViews) {
                        if (!recyclerView1.equals(recyclerView)) {
                            recyclerView1.setTag(R.id.tag_scroll_sync, true);
                            recyclerView1.scrollBy(dx, dy);
                            recyclerView1.setTag(R.id.tag_scroll_sync, null);
                        }
                    }
                }
            });
        }
    }

    // one recycler per day is the solution
}